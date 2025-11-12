package cmd

import (
	"context"
	"errors"
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
			{"Node.js (npm)", checkNodeNPM},
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
				if flagVerbose {
					if detail := errorDetail(err); detail != "" {
						printDetail(detail)
					}
				}
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
		return newVerboseError(err, string(out))
	}
	output := string(out)
	if !strings.Contains(output, "go1.") {
		return fmt.Errorf("unexpected output: %s", out)
	}
	return nil
}

func checkMavenWrapper(ctx context.Context) error {
	cmd := exec.CommandContext(ctx, "sh", "-c", "cd "+cfg.Paths.ServiceRoot+" && ./mvnw -v")
	if runtime.GOOS == "windows" {
		cmd = exec.CommandContext(ctx, "cmd", "/C", "cd "+cfg.Paths.ServiceRoot+" && mvnw.cmd -v")
	}
	out, err := cmd.CombinedOutput()
	if err != nil {
		return newVerboseError(err, string(out))
	}
	return nil
}

func checkNodeNPM(ctx context.Context) error {
	cmdName, err := findNPMExecutable()
	if err != nil {
		return err
	}
	cmd := exec.CommandContext(ctx, cmdName, "--version")
	out, err := cmd.CombinedOutput()
	if err != nil {
		return newVerboseError(err, string(out))
	}
	version := strings.TrimSpace(string(out))
	if version == "" {
		return fmt.Errorf("npm returned empty version output")
	}
	return nil
}

func checkDocker(ctx context.Context) error {
	cmd := exec.CommandContext(ctx, "docker", "version", "--format", "'{{.Server.Version}}'")
	out, err := cmd.CombinedOutput()
	if err != nil {
		return newVerboseError(err, string(out))
	}
	return nil
}

func checkGitClean(ctx context.Context) error {
	status, err := gitStatusFn(ctx)
	if err != nil {
		return err
	}
	if strings.TrimSpace(status) != "" {
		return newVerboseError(fmt.Errorf("working tree has changes"), status)
	}
	return nil
}

type detailError interface {
	error
	Detail() string
}

type verboseError struct {
	err    error
	detail string
}

func (e *verboseError) Error() string {
	if e == nil || e.err == nil {
		return ""
	}
	return e.err.Error()
}

func (e *verboseError) Unwrap() error {
	if e == nil {
		return nil
	}
	return e.err
}

func (e *verboseError) Detail() string {
	if e == nil {
		return ""
	}
	return e.detail
}

func newVerboseError(err error, detail string) error {
	detail = strings.TrimSpace(detail)
	if detail == "" || err == nil {
		return err
	}
	return &verboseError{err: err, detail: detail}
}

func errorDetail(err error) string {
	if err == nil {
		return ""
	}
	var de detailError
	if errors.As(err, &de) {
		return strings.TrimSpace(de.Detail())
	}
	return ""
}

func printDetail(detail string) {
	if detail == "" {
		return
	}
	for _, line := range strings.Split(detail, "\n") {
		line = strings.TrimSpace(line)
		if line == "" {
			continue
		}
		fmt.Printf("    %s\n", line)
	}
}
