package cmd

import (
    "context"
    "fmt"
    "log/slog"
    "path/filepath"
    "strings"
    "time"

    "github.com/spf13/cobra"
    "github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var (
    flagDockerTag  string
    flagDockerPush bool
    flagComposeProfile string
)

var dockerCmd = &cobra.Command{
    Use:   "docker",
    Short: "Docker workflows: build, push, compose",
}

var dockerBuildCmd = &cobra.Command{
    Use:   "build",
    Short: "Build (and optionally push) Docker image for tournamentmgmt",
    RunE: func(cmd *cobra.Command, args []string) error {
        repo := cfg.Docker.ImageRepo
        tag := flagDockerTag
        if strings.TrimSpace(tag) == "" {
            tag = "latest"
        }
        image := fmt.Sprintf("%s:%s", repo, tag)
        contextDir := filepath.Dir(cfg.Paths.ServiceRoot) // conservative default: parent of module
        buildCmd := []string{"docker", "build", "-t", image, cfg.Paths.ServiceRoot}

        slog.Info("exec", "cmd", buildCmd, "cwd", contextDir)
        ctx, cancel := context.WithTimeout(context.Background(), 1*time.Hour)
        defer cancel()
        code, _, _, err := execx.Run(ctx, execx.RunOptions{Cmd: buildCmd, Cwd: contextDir, Timeout: 1 * time.Hour, DryRun: flagDryRun})
        if err != nil {
            return fmt.Errorf("docker build failed (exit=%d): %w", code, err)
        }
        if flagDockerPush {
            pushCmd := []string{"docker", "push", image}
            slog.Info("exec", "cmd", pushCmd, "cwd", contextDir)
            code, _, _, err = execx.Run(ctx, execx.RunOptions{Cmd: pushCmd, Cwd: contextDir, Timeout: 30 * time.Minute, DryRun: flagDryRun})
            if err != nil {
                return fmt.Errorf("docker push failed (exit=%d): %w", code, err)
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
        composeArgs := []string{"docker", "compose", "up", "-d"}
        if flagComposeProfile != "" {
            composeArgs = append(composeArgs, "--profile", flagComposeProfile)
        }
        ctx, cancel := context.WithTimeout(context.Background(), 30*time.Minute)
        defer cancel()
        slog.Info("exec", "cmd", composeArgs, "cwd", cfg.Project.Root)
        code, _, _, err := execx.Run(ctx, execx.RunOptions{Cmd: composeArgs, Cwd: cfg.Project.Root, Timeout: 30 * time.Minute, DryRun: flagDryRun})
        if err != nil {
            return fmt.Errorf("compose up failed (exit=%d): %w", code, err)
        }
        return nil
    },
}

var dockerComposeDownCmd = &cobra.Command{
    Use:   "down",
    Short: "docker compose down",
    RunE: func(cmd *cobra.Command, args []string) error {
        composeArgs := []string{"docker", "compose", "down"}
        ctx, cancel := context.WithTimeout(context.Background(), 10*time.Minute)
        defer cancel()
        slog.Info("exec", "cmd", composeArgs, "cwd", cfg.Project.Root)
        code, _, _, err := execx.Run(ctx, execx.RunOptions{Cmd: composeArgs, Cwd: cfg.Project.Root, Timeout: 10 * time.Minute, DryRun: flagDryRun})
        if err != nil {
            return fmt.Errorf("compose down failed (exit=%d): %w", code, err)
        }
        return nil
    },
}

func init() {
    dockerBuildCmd.Flags().StringVar(&flagDockerTag, "tag", "", "image tag (default: latest)")
    dockerBuildCmd.Flags().BoolVar(&flagDockerPush, "push", false, "push image after build")
    dockerCmd.AddCommand(dockerBuildCmd)

    dockerComposeUpCmd.Flags().StringVar(&flagComposeProfile, "profile", "", "compose profile to enable")
    dockerComposeCmd.AddCommand(dockerComposeUpCmd)
    dockerComposeCmd.AddCommand(dockerComposeDownCmd)
    dockerCmd.AddCommand(dockerComposeCmd)

    rootCmd.AddCommand(dockerCmd)
}


