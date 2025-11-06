package cmd

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"net/url"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/prompt"
)

const (
	defaultDeployPollInterval = 15 * time.Second
	defaultDeployPollTimeout  = 10 * time.Minute
)

var (
	flagDeployEnv   string
	flagDeployNotes string
)

type workflowDispatch struct {
	Ref    string            `json:"ref"`
	Inputs map[string]string `json:"inputs,omitempty"`
}

type workflowRun struct {
	ID         int64     `json:"id"`
	HTMLURL    string    `json:"html_url"`
	Status     string    `json:"status"`
	Conclusion string    `json:"conclusion"`
	HeadBranch string    `json:"head_branch"`
	HeadSHA    string    `json:"head_sha"`
	CreatedAt  time.Time `json:"created_at"`
}

type workflowRunsResponse struct {
	WorkflowRuns []workflowRun `json:"workflow_runs"`
}

type deployContext struct {
	Env          string
	Notes        string
	Repo         string
	WorkflowPath string
	WorkflowSlug string
	Ref          string
	Branch       string
	SHA          string
	PollInterval time.Duration
	PollTimeout  time.Duration
	StartedAt    time.Time
}

type githubClient struct {
	token      string
	httpClient *http.Client
	baseURL    string
}

var deployCmd = &cobra.Command{
	Use:   "deploy",
	Short: "Trigger a deploy via GitHub Actions with safety checks",
	RunE: func(cmd *cobra.Command, args []string) error {
		if cfg == nil {
			return errors.New("configuration not loaded")
		}
		token := strings.TrimSpace(os.Getenv("GITHUB_TOKEN"))
		if token == "" {
			return errors.New("GITHUB_TOKEN env var is required for deploy")
		}

		ctx := cmd.Context()
		if ctx == nil {
			ctx = context.Background()
		}

		gh := newGitHubClient(token)
		plan, err := buildDeployContext(ctx, gh)
		if err != nil {
			return err
		}

		slog.Info("deploy plan",
			"env", plan.Env,
			"ref", plan.Ref,
			"branch", plan.Branch,
			"sha", plan.SHA,
			"workflow", plan.WorkflowSlug,
			"dryRun", flagDryRun,
			"yes", flagYes,
		)

		if flagDryRun {
			return nil
		}

		if !flagYes {
			question := fmt.Sprintf("Trigger workflow '%s' targeting %s (%s)?", plan.WorkflowSlug, plan.Ref, plan.Env)
			if !prompt.Confirm(question, false) {
				slog.Info("deploy aborted by user")
				return nil
			}
		}

		plan.StartedAt = time.Now()
		if err := dispatchWorkflow(ctx, gh, plan); err != nil {
			return err
		}
		slog.Info("deploy dispatched", "workflow", plan.WorkflowSlug, "env", plan.Env)

		run, err := awaitWorkflow(ctx, gh, plan)
		if err != nil {
			return err
		}

		slog.Info("deploy result", "status", run.Status, "conclusion", run.Conclusion, "url", run.HTMLURL)
		if strings.EqualFold(run.Conclusion, "success") {
			return nil
		}
		return fmt.Errorf("deploy workflow concluded with %s", run.Conclusion)
	},
}

func init() {
	deployCmd.Flags().StringVar(&flagDeployEnv, "env", "dev", "deployment environment (e.g., dev, prod)")
	deployCmd.Flags().StringVar(&flagDeployNotes, "notes", "", "optional notes for the deployment")
	rootCmd.AddCommand(deployCmd)
}

func buildDeployContext(ctx context.Context, gh *githubClient) (*deployContext, error) {
	env := strings.TrimSpace(flagDeployEnv)
	if env == "" {
		env = "dev"
	}
	if cfg.Deploy.Workflow == "" {
		return nil, errors.New("deploy.workflow is not configured")
	}
	if cfg.Deploy.Repo == "" {
		return nil, errors.New("deploy.repo is not configured")
	}

	plan := &deployContext{
		Env:          env,
		Notes:        strings.TrimSpace(flagDeployNotes),
		Repo:         cfg.Deploy.Repo,
		WorkflowPath: cfg.Deploy.Workflow,
		WorkflowSlug: filepath.Base(cfg.Deploy.Workflow),
		PollInterval: parseDurationDefault(cfg.Deploy.PollInterval, defaultDeployPollInterval),
		PollTimeout:  parseDurationDefault(cfg.Deploy.PollTimeout, defaultDeployPollTimeout),
	}

	statusOutput, err := gitStatusFn(ctx)
	if err != nil {
		return nil, err
	}
	if cfg.Deploy.RequireClean && statusOutput != "" {
		if flagYes {
			slog.Warn("workspace has uncommitted changes; proceeding due to --yes override")
		} else {
			return nil, errors.New("workspace has uncommitted changes; commit/stash or rerun with --yes")
		}
	}

	branch, err := gitRevParseFn(ctx, "--abbrev-ref", "HEAD")
	if err != nil {
		return nil, err
	}
	if branch == "HEAD" {
		return nil, errors.New("detached HEAD state; checkout a branch before deploying")
	}
	plan.Branch = branch

	ref := strings.TrimSpace(cfg.Deploy.DefaultRef)
	if ref == "" {
		ref = branch
	}
	if cfg.Deploy.EnvRefs != nil {
		if specific := strings.TrimSpace(cfg.Deploy.EnvRefs[env]); specific != "" {
			ref = specific
			if plan.Branch != specific {
				if flagYes {
					slog.Warn("branch does not match environment requirement; continuing due to --yes",
						"env", env, "requiredBranch", specific, "currentBranch", plan.Branch,
					)
				} else {
					return nil, fmt.Errorf("environment %s requires branch %s (current branch %s)", env, specific, plan.Branch)
				}
			}
		}
	}
	plan.Ref = ref

	sha, err := gitRevParseFn(ctx, "HEAD")
	if err != nil {
		return nil, err
	}
	plan.SHA = sha

	if cfg.Deploy.RequireProtected {
		if flagYes {
			slog.Warn("skipping branch protection check due to --yes override", "ref", plan.Ref)
		} else if err := ensureBranchProtection(ctx, gh, plan.Repo, plan.Ref); err != nil {
			return nil, err
		}
	}

	if cfg.Deploy.RequireGreen {
		if flagYes {
			slog.Warn("skipping commit status check due to --yes override", "sha", plan.SHA)
		} else if err := ensureCommitGreen(ctx, gh, plan.Repo, plan.SHA); err != nil {
			return nil, err
		}
	}

	return plan, nil
}

func dispatchWorkflow(ctx context.Context, gh *githubClient, plan *deployContext) error {
	workflowSlug := url.PathEscape(plan.WorkflowSlug)
	endpoint := gh.url(fmt.Sprintf("/repos/%s/actions/workflows/%s/dispatches", plan.Repo, workflowSlug))
	inputs := map[string]string{"env": plan.Env}
	if plan.Notes != "" {
		inputs["notes"] = plan.Notes
	}
	payload, err := json.Marshal(workflowDispatch{Ref: plan.Ref, Inputs: inputs})
	if err != nil {
		return err
	}
	resp, err := gh.do(ctx, http.MethodPost, endpoint, bytes.NewReader(payload))
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusNoContent {
		msg, _ := io.ReadAll(io.LimitReader(resp.Body, 2048))
		return fmt.Errorf("workflow dispatch failed: %s (%s)", resp.Status, strings.TrimSpace(string(msg)))
	}
	return nil
}

func awaitWorkflow(ctx context.Context, gh *githubClient, plan *deployContext) (*workflowRun, error) {
	ctx, cancel := context.WithTimeout(ctx, plan.PollTimeout)
	defer cancel()

	ticker := time.NewTicker(plan.PollInterval)
	defer ticker.Stop()

	for {
		run, err := fetchWorkflowRun(ctx, gh, plan)
		if err != nil {
			slog.Warn("deploy poll error", "error", err)
		} else if run != nil {
			if strings.EqualFold(run.Status, "completed") {
				return run, nil
			}
			slog.Info("deploy progress", "status", run.Status, "conclusion", run.Conclusion, "url", run.HTMLURL)
		}

		select {
		case <-ctx.Done():
			return nil, fmt.Errorf("timed out waiting for deploy workflow: %w", ctx.Err())
		case <-ticker.C:
		}
	}
}

func fetchWorkflowRun(ctx context.Context, gh *githubClient, plan *deployContext) (*workflowRun, error) {
	branchParam := url.QueryEscape(plan.Ref)
	workflowSlug := url.PathEscape(plan.WorkflowSlug)
	endpoint := gh.url(fmt.Sprintf("/repos/%s/actions/workflows/%s/runs?event=workflow_dispatch&branch=%s&per_page=5", plan.Repo, workflowSlug, branchParam))
	resp, err := gh.do(ctx, http.MethodGet, endpoint, nil)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		msg, _ := io.ReadAll(io.LimitReader(resp.Body, 2048))
		return nil, fmt.Errorf("failed to fetch workflow runs: %s (%s)", resp.Status, strings.TrimSpace(string(msg)))
	}

	var payload workflowRunsResponse
	if err := json.NewDecoder(resp.Body).Decode(&payload); err != nil {
		return nil, err
	}

	for _, run := range payload.WorkflowRuns {
		if run.HeadSHA == plan.SHA && !run.CreatedAt.Before(plan.StartedAt.Add(-2*time.Second)) {
			r := run
			return &r, nil
		}
	}
	return nil, nil
}

func ensureBranchProtection(ctx context.Context, gh *githubClient, repo string, ref string) error {
	endpoint := gh.url(fmt.Sprintf("/repos/%s/branches/%s/protection", repo, url.PathEscape(ref)))
	resp, err := gh.do(ctx, http.MethodGet, endpoint, nil)
	if err != nil {
		return fmt.Errorf("branch protection check failed: %w", err)
	}
	defer resp.Body.Close()

	switch resp.StatusCode {
	case http.StatusOK:
		return nil
	case http.StatusNotFound:
		return fmt.Errorf("branch %s does not have protection enabled; update settings or disable deploy.requireProtected", ref)
	case http.StatusForbidden:
		msg, _ := io.ReadAll(io.LimitReader(resp.Body, 2048))
		return fmt.Errorf("branch protection check forbidden: ensure the token has repo scope (%s)", strings.TrimSpace(string(msg)))
	default:
		msg, _ := io.ReadAll(io.LimitReader(resp.Body, 2048))
		return fmt.Errorf("branch protection check failed: %s (%s)", resp.Status, strings.TrimSpace(string(msg)))
	}
}

func ensureCommitGreen(ctx context.Context, gh *githubClient, repo string, sha string) error {
	endpoint := gh.url(fmt.Sprintf("/repos/%s/commits/%s/status", repo, sha))
	resp, err := gh.do(ctx, http.MethodGet, endpoint, nil)
	if err != nil {
		return fmt.Errorf("commit status lookup failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		msg, _ := io.ReadAll(io.LimitReader(resp.Body, 2048))
		return fmt.Errorf("commit status lookup failed: %s (%s)", resp.Status, strings.TrimSpace(string(msg)))
	}

	var payload struct {
		State    string `json:"state"`
		Statuses []struct {
			Context     string `json:"context"`
			State       string `json:"state"`
			Description string `json:"description"`
		} `json:"statuses"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&payload); err != nil {
		return err
	}

	if strings.EqualFold(payload.State, "success") {
		return nil
	}

	var failing []string
	for _, s := range payload.Statuses {
		if !strings.EqualFold(s.State, "success") {
			piece := s.Context
			if s.Description != "" {
				piece = fmt.Sprintf("%s (%s)", s.Context, s.Description)
			}
			failing = append(failing, piece)
		}
	}
	if len(failing) == 0 {
		failing = append(failing, payload.State)
	}
	return fmt.Errorf("commit %s is not green: %s", sha, strings.Join(failing, "; "))
}

func gitStatus(ctx context.Context) (string, error) {
	code, stdout, stderr, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           []string{"git", "status", "--porcelain"},
		Cwd:           cfg.Project.Root,
		Timeout:       5 * time.Second,
		CaptureOutput: true,
	})
	if err != nil {
		return "", fmt.Errorf("git status failed (exit=%d): %w (%s)", code, err, strings.TrimSpace(stderr))
	}
	return strings.TrimSpace(stdout), nil
}

func gitRevParse(ctx context.Context, args ...string) (string, error) {
	gitArgs := append([]string{"git", "rev-parse"}, args...)
	code, stdout, stderr, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           gitArgs,
		Cwd:           cfg.Project.Root,
		Timeout:       5 * time.Second,
		CaptureOutput: true,
	})
	if err != nil {
		return "", fmt.Errorf("git rev-parse failed (exit=%d): %w (%s)", code, err, strings.TrimSpace(stderr))
	}
	return strings.TrimSpace(stdout), nil
}

func newGitHubClient(token string) *githubClient {
	base := strings.TrimSpace(os.Getenv("RO_GITHUB_API_BASE"))
	if base == "" {
		base = "https://api.github.com"
	} else {
		base = strings.TrimRight(base, "/")
	}
	return &githubClient{
		token:      token,
		httpClient: &http.Client{Timeout: 30 * time.Second},
		baseURL:    base,
	}
}

func (c *githubClient) url(path string) string {
	if path == "" {
		return c.baseURL
	}
	if strings.HasPrefix(path, "/") {
		return c.baseURL + path
	}
	return c.baseURL + "/" + path
}

func (c *githubClient) do(ctx context.Context, method string, endpoint string, body io.Reader) (*http.Response, error) {
	req, err := http.NewRequestWithContext(ctx, method, endpoint, body)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Accept", "application/vnd.github+json")
	req.Header.Set("Authorization", "Bearer "+c.token)
	req.Header.Set("X-GitHub-Api-Version", "2022-11-28")
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	return c.httpClient.Do(req)
}

func parseDurationDefault(value string, fallback time.Duration) time.Duration {
	if value == "" {
		return fallback
	}
	d, err := time.ParseDuration(value)
	if err != nil {
		slog.Warn("invalid duration in deploy config; using fallback", "value", value, "fallback", fallback.String())
		return fallback
	}
	return d
}

var (
	gitStatusFn   = gitStatus
	gitRevParseFn = gitRevParse
)
