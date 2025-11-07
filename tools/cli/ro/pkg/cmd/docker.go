package cmd

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"path/filepath"
	"strings"
	"time"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var (
	flagDockerPush       bool
	flagDockerManualTags []string
	flagDockerBranchTag  = true
	flagDockerShaTag     = true
	flagDockerLatestTag  bool

	flagComposeProfiles []string
	flagComposeFile     string
	flagComposeEnvFile  string
)

var dockerCmd = &cobra.Command{
	Use:   "docker",
	Short: "Docker workflows: build, push, compose",
}

var dockerBuildCmd = &cobra.Command{
	Use:   "build",
	Short: "Build (and optionally push) Docker image for tournamentmgmt",
	RunE: func(cmd *cobra.Command, args []string) error {
		ctx, cancel := context.WithTimeout(cmd.Context(), 1*time.Hour)
		defer cancel()
		if ctx == nil {
			ctx = context.Background()
		}

		imageBase := dockerImageRef(cfg.Docker.Registry, cfg.Docker.ImageRepo)
		if imageBase == "" {
			return errors.New("docker.imageRepo is not configured")
		}
		tags, err := collectDockerTags(ctx, imageBase)
		if err != nil {
			return err
		}

		contextDir := cfg.Docker.Context
		if strings.TrimSpace(contextDir) == "" {
			contextDir = cfg.Paths.ServiceRoot
		}
		dockerfile := cfg.Docker.Dockerfile
		if strings.TrimSpace(dockerfile) == "" {
			dockerfile = filepath.Join(contextDir, "Dockerfile")
		}

		buildCmd := []string{"docker", "build", "-f", dockerfile}
		for _, tag := range tags {
			buildCmd = append(buildCmd, "-t", tag)
		}
		buildCmd = append(buildCmd, contextDir)

		slog.Info("docker build", "cmd", buildCmd, "cwd", cfg.Project.Root)
		if err := runDockerCommand(ctx, buildCmd, 1*time.Hour); err != nil {
			return err
		}

		if flagDockerPush {
			pushCtx, pushCancel := context.WithTimeout(context.Background(), 30*time.Minute)
			defer pushCancel()
			for _, tag := range tags {
				pushCmd := []string{"docker", "push", tag}
				slog.Info("docker push", "tag", tag)
				if err := runDockerCommand(pushCtx, pushCmd, 30*time.Minute); err != nil {
					return err
				}
			}
		}
		return nil
	},
}

var dockerComposeCmd = &cobra.Command{
	Use:   "compose",
	Short: "Compose up/down the local stack",
}

var dockerComposeUpCmd = &cobra.Command{
	Use:   "up",
	Short: "docker compose up",
	RunE: func(cmd *cobra.Command, args []string) error {
		ctx, cancel := context.WithTimeout(cmd.Context(), 30*time.Minute)
		defer cancel()
		if ctx == nil {
			ctx = context.Background()
		}
		composeCmd := composeBaseArgs()
		composeCmd = append(composeCmd, "up", "-d")
		composeCmd = append(composeCmd, composeProfileArgs()...)
		slog.Info("docker compose up", "cmd", composeCmd)
		return runDockerCommand(ctx, composeCmd, 30*time.Minute)
	},
}

var dockerComposeDownCmd = &cobra.Command{
	Use:   "down",
	Short: "docker compose down",
	RunE: func(cmd *cobra.Command, args []string) error {
		ctx, cancel := context.WithTimeout(cmd.Context(), 10*time.Minute)
		defer cancel()
		if ctx == nil {
			ctx = context.Background()
		}
		composeCmd := composeBaseArgs()
		composeCmd = append(composeCmd, "down")
		composeCmd = append(composeCmd, composeProfileArgs()...)
		slog.Info("docker compose down", "cmd", composeCmd)
		return runDockerCommand(ctx, composeCmd, 10*time.Minute)
	},
}

var dockerComposeLogsCmd = &cobra.Command{
	Use:   "logs",
	Short: "docker compose logs",
	RunE: func(cmd *cobra.Command, args []string) error {
		ctx := cmd.Context()
		if ctx == nil {
			ctx = context.Background()
		}
		service := ""
		if len(args) > 0 {
			service = args[0]
		}
		composeCmd := composeBaseArgs()
		composeCmd = append(composeCmd, "logs", "--follow")
		composeCmd = append(composeCmd, composeProfileArgs()...)
		if service != "" {
			composeCmd = append(composeCmd, service)
		}
		slog.Info("docker compose logs", "cmd", composeCmd)
		return runDockerCommand(ctx, composeCmd, 0)
	},
}

func init() {
	dockerBuildCmd.Flags().StringSliceVar(&flagDockerManualTags, "tag", nil, "extra tag(s) to apply (without registry unless fully qualified)")
	dockerBuildCmd.Flags().BoolVar(&flagDockerBranchTag, "branch-tag", true, "include branch-based tag")
	dockerBuildCmd.Flags().BoolVar(&flagDockerShaTag, "sha-tag", true, "include short SHA tag")
	dockerBuildCmd.Flags().BoolVar(&flagDockerLatestTag, "latest", false, "include latest tag")
	dockerBuildCmd.Flags().BoolVar(&flagDockerPush, "push", false, "push images after build")
	dockerCmd.AddCommand(dockerBuildCmd)

	dockerComposeCmd.PersistentFlags().StringSliceVar(&flagComposeProfiles, "profile", nil, "compose profile(s) to enable")
	dockerComposeCmd.PersistentFlags().StringVar(&flagComposeFile, "file", "", "compose file to use (defaults to docker.composeFile)")
	dockerComposeCmd.PersistentFlags().StringVar(&flagComposeEnvFile, "env-file", "", "compose env file to load")
	dockerComposeCmd.AddCommand(dockerComposeUpCmd)
	dockerComposeCmd.AddCommand(dockerComposeDownCmd)
	dockerComposeCmd.AddCommand(dockerComposeLogsCmd)
	dockerCmd.AddCommand(dockerComposeCmd)

	rootCmd.AddCommand(dockerCmd)
}

func runDockerCommand(ctx context.Context, args []string, timeout time.Duration) error {
	if ctx == nil {
		ctx = context.Background()
	}
	runCtx := ctx
	var cancel context.CancelFunc
	if timeout > 0 {
		runCtx, cancel = context.WithTimeout(ctx, timeout)
		defer cancel()
	}
	code, _, stderr, err := execx.Run(runCtx, execx.RunOptions{
		Cmd:     args,
		Cwd:     cfg.Project.Root,
		Timeout: timeout,
		DryRun:  flagDryRun,
	})
	if err != nil && !flagDryRun {
		if stderr != "" {
			fmt.Print(stderr)
		}
		return fmt.Errorf("docker command failed (exit=%d): %w", code, err)
	}
	return nil
}

func dockerImageRef(registry string, repo string) string {
	repo = strings.TrimSpace(repo)
	if repo == "" {
		return ""
	}
	firstSlash := strings.IndexRune(repo, '/')
	if firstSlash > 0 {
		hostPart := repo[:firstSlash]
		if strings.Contains(hostPart, ".") || strings.Contains(hostPart, ":") {
			return repo
		}
	}
	registry = strings.TrimSpace(registry)
	if registry == "" {
		return repo
	}
	registry = strings.TrimSuffix(registry, "/")
	return fmt.Sprintf("%s/%s", registry, strings.TrimPrefix(repo, "/"))
}

func collectDockerTags(ctx context.Context, imageBase string) ([]string, error) {
	tagSet := map[string]struct{}{}
	addTag := func(tag string) {
		if strings.TrimSpace(tag) == "" {
			return
		}
		tagSet[tag] = struct{}{}
	}

	if flagDockerBranchTag {
		branch, err := gitRevParseFn(ctx, "--abbrev-ref", "HEAD")
		if err != nil {
			return nil, fmt.Errorf("git branch lookup failed: %w", err)
		}
		addTag(fmt.Sprintf("%s:%s", imageBase, sanitizeTag(branch)))
	}
	if flagDockerShaTag {
		sha, err := gitRevParseFn(ctx, "--short", "HEAD")
		if err != nil {
			return nil, fmt.Errorf("git sha lookup failed: %w", err)
		}
		addTag(fmt.Sprintf("%s:sha-%s", imageBase, sanitizeTag(sha)))
	}
	for _, manual := range flagDockerManualTags {
		manual = strings.TrimSpace(manual)
		if manual == "" {
			continue
		}
		if strings.Contains(manual, ":") {
			addTag(manual)
		} else {
			addTag(fmt.Sprintf("%s:%s", imageBase, sanitizeTag(manual)))
		}
	}

	if flagDockerLatestTag || len(tagSet) == 0 {
		addTag(fmt.Sprintf("%s:latest", imageBase))
	}

	var tags []string
	for tag := range tagSet {
		tags = append(tags, tag)
	}
	return tags, nil
}

func sanitizeTag(value string) string {
	value = strings.TrimSpace(strings.ToLower(value))
	if value == "" {
		return "latest"
	}
	replacer := strings.NewReplacer(
		"/", "-",
		"\\", "-",
		" ", "-",
		":", "-",
		"@", "-",
	)
	value = replacer.Replace(value)
	value = strings.Trim(value, "-")
	if value == "" {
		return "latest"
	}
	return value
}

func composeBaseArgs() []string {
	file := flagComposeFile
	if file == "" {
		file = cfg.Docker.ComposeFile
	}
	args := []string{"docker", "compose"}
	if file != "" {
		args = append(args, "-f", file)
	}
	if flagComposeEnvFile != "" {
		args = append(args, "--env-file", flagComposeEnvFile)
	}
	return args
}

func composeProfileArgs() []string {
	profiles := flagComposeProfiles
	var args []string
	for _, p := range profiles {
		p = strings.TrimSpace(p)
		if p == "" {
			continue
		}
		args = append(args, "--profile", p)
	}
	return args
}
