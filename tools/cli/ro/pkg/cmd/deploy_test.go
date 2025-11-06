package cmd

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
)

func TestParseDurationDefault(t *testing.T) {
	t.Run("returns fallback on empty", func(t *testing.T) {
		fallback := 10 * time.Second
		if got := parseDurationDefault("", fallback); got != fallback {
			t.Fatalf("expected fallback %s, got %s", fallback, got)
		}
	})
	t.Run("returns fallback on invalid", func(t *testing.T) {
		fallback := 5 * time.Second
		if got := parseDurationDefault("bogus", fallback); got != fallback {
			t.Fatalf("expected fallback %s, got %s", fallback, got)
		}
	})
	t.Run("parses valid duration", func(t *testing.T) {
		if got := parseDurationDefault("3m", time.Second); got != 3*time.Minute {
			t.Fatalf("expected 3m, got %s", got)
		}
	})
}

func TestBuildDeployContextBranchGuard(t *testing.T) {
	origStatus := gitStatusFn
	origRev := gitRevParseFn
	origCfg := cfg
	origYes := flagYes
	origEnv := flagDeployEnv
	t.Cleanup(func() {
		gitStatusFn = origStatus
		gitRevParseFn = origRev
		cfg = origCfg
		flagYes = origYes
		flagDeployEnv = origEnv
	})

	cfg = &config.Config{
		Project: config.Project{Root: "."},
		Deploy: config.Deploy{
			Repo:             "owner/repo",
			Workflow:         ".github/workflows/deploy.yaml",
			DefaultRef:       "main",
			EnvRefs:          map[string]string{"prod": "main"},
			RequireClean:     false,
			RequireProtected: false,
			RequireGreen:     false,
		},
	}
	gitStatusFn = func(ctx context.Context) (string, error) {
		return "", nil
	}
	gitRevParseFn = func(ctx context.Context, args ...string) (string, error) {
		if len(args) > 0 && args[0] == "--abbrev-ref" {
			return "feature/awesome", nil
		}
		return "abc123", nil
	}
	flagDeployEnv = "prod"
	flagYes = false

	_, err := buildDeployContext(context.Background(), &githubClient{})
	if err == nil {
		t.Fatalf("expected branch guard error")
	}
	if !strings.Contains(err.Error(), "requires branch main") {
		t.Fatalf("unexpected error: %v", err)
	}
}

func TestBuildDeployContextAllowsYesOverride(t *testing.T) {
	origStatus := gitStatusFn
	origRev := gitRevParseFn
	origCfg := cfg
	origYes := flagYes
	origEnv := flagDeployEnv
	t.Cleanup(func() {
		gitStatusFn = origStatus
		gitRevParseFn = origRev
		cfg = origCfg
		flagYes = origYes
		flagDeployEnv = origEnv
	})

	cfg = &config.Config{
		Project: config.Project{Root: "."},
		Deploy: config.Deploy{
			Repo:             "owner/repo",
			Workflow:         ".github/workflows/deploy.yaml",
			DefaultRef:       "main",
			EnvRefs:          map[string]string{"prod": "main"},
			RequireClean:     false,
			RequireProtected: false,
			RequireGreen:     false,
		},
	}
	gitStatusFn = func(ctx context.Context) (string, error) {
		return "", nil
	}
	gitRevParseFn = func(ctx context.Context, args ...string) (string, error) {
		if len(args) > 0 && args[0] == "--abbrev-ref" {
			return "feature/awesome", nil
		}
		return "abc123", nil
	}
	flagDeployEnv = "prod"
	flagYes = true

	plan, err := buildDeployContext(context.Background(), &githubClient{})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if plan.Ref != "main" {
		t.Fatalf("expected ref main, got %s", plan.Ref)
	}
	if plan.Branch != "feature/awesome" {
		t.Fatalf("expected branch feature/awesome, got %s", plan.Branch)
	}
}

func TestEnsureCommitGreen(t *testing.T) {
	handler := func(state string) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			w.Header().Set("Content-Type", "application/json")
			payload := map[string]any{
				"state": state,
				"statuses": []map[string]string{
					{"context": "build", "state": state, "description": "unit tests"},
				},
			}
			_ = json.NewEncoder(w).Encode(payload)
		}
	}

	t.Run("fails when state is not success", func(t *testing.T) {
		srv := httptest.NewServer(handler("failure"))
		defer srv.Close()

		gh := &githubClient{
			token:      "test",
			httpClient: srv.Client(),
			baseURL:    srv.URL,
		}

		err := ensureCommitGreen(context.Background(), gh, "owner/repo", "abc123")
		if err == nil {
			t.Fatalf("expected failure when state is not success")
		}
	})

	t.Run("passes when state is success", func(t *testing.T) {
		srv := httptest.NewServer(handler("success"))
		defer srv.Close()

		gh := &githubClient{
			token:      "test",
			httpClient: srv.Client(),
			baseURL:    srv.URL,
		}

		if err := ensureCommitGreen(context.Background(), gh, "owner/repo", "abc123"); err != nil {
			t.Fatalf("expected success, got error: %v", err)
		}
	})
}

func TestAwaitWorkflow(t *testing.T) {
	var calls int
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if !strings.Contains(r.URL.Path, "/runs") {
			http.NotFound(w, r)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		var runs []workflowRun
		if calls > 0 {
			runs = append(runs, workflowRun{
				ID:         42,
				HTMLURL:    "https://example.com/run/42",
				Status:     "completed",
				Conclusion: "success",
				HeadBranch: "main",
				HeadSHA:    "abc123",
				CreatedAt:  time.Now(),
			})
		}
		payload := workflowRunsResponse{WorkflowRuns: runs}
		_ = json.NewEncoder(w).Encode(payload)
		calls++
	}))
	defer srv.Close()

	gh := &githubClient{
		token:      "test",
		httpClient: srv.Client(),
		baseURL:    srv.URL,
	}

	plan := &deployContext{
		Repo:         "owner/repo",
		WorkflowSlug: "deploy.yaml",
		Ref:          "main",
		Branch:       "main",
		SHA:          "abc123",
		PollInterval: 10 * time.Millisecond,
		PollTimeout:  200 * time.Millisecond,
		StartedAt:    time.Now().Add(-time.Second),
	}

	run, err := awaitWorkflow(context.Background(), gh, plan)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if run == nil || run.ID != 42 {
		t.Fatalf("expected run ID 42, got %#v", run)
	}
}
