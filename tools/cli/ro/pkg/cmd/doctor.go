package cmd

import (
	"context"
	"fmt"
	"os/exec"
	"runtime"
	"strings"

	"github.com/spf13/cobra"
)

var doctorCmd = &cobra.Command{
	Use:   "doctor",
	Short: "Run diagnostics to validate local environment",
	RunE: func(cmd *cobra.Command, args []string) error {
		checks := []struct {
			Name string
			Fn   func(context.Context) error
		}{
			{"Go toolchain", checkGoVersion},
			{"Java (Maven wrapper)", checkMavenWrapper},
			{"Docker", checkDocker},
			{"Git", checkGitClean},
		}
		ctx := cmd.Context()
		if ctx == nil {
			ctx = context.Background()
		}
		var failed []string
		for _, c := range checks {
			if err := c.Fn(ctx); err != nil {
				fmt.Printf("✗ %s: %v\n", c.Name, err)
				failed = append(failed, c.Name)
			} else {
				fmt.Printf("✓ %s\n", c.Name)
			}
		}
		if len(failed) > 0 {
			return fmt.Errorf("doctor found issues with: %s", strings.Join(failed, ", "))
		}
		return nil
	},
}

func init() {
	rootCmd.AddCommand(doctorCmd)
}

func checkGoVersion(ctx context.Context) error {
	cmd := exec.CommandContext(ctx, "go", "version")
	out, err := cmd.CombinedOutput()
	if err != nil {
		return err
	}
	if !strings.Contains(string(out), "go1.") {
		return fmt.Errorf("unexpected output: %s", out)
	}
	return nil
}

func checkMavenWrapper(ctx context.Context) error {
	cmd := exec.CommandContext(ctx, "sh", "-c", "cd "+cfg.Paths.ServiceRoot+" && ./mvnw -v")
	if runtime.GOOS == "windows" {
		cmd = exec.CommandContext(ctx, "cmd", "/C", "cd "+cfg.Paths.ServiceRoot+" && mvnw.cmd -v")
	}
	if err := cmd.Run(); err != nil {
		return err
	}
	return nil
}

func checkDocker(ctx context.Context) error {
	cmd := exec.CommandContext(ctx, "docker", "version", "--format", "'{{.Server.Version}}'")
	return cmd.Run()
}

func checkGitClean(ctx context.Context) error {
	status, err := gitStatusFn(ctx)
	if err != nil {
		return err
	}
	if strings.TrimSpace(status) != "" {
		return fmt.Errorf("working tree has changes")
	}
	return nil
}
