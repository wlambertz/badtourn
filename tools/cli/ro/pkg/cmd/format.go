package cmd

import (
	"context"
	"fmt"
	"log/slog"
	"strings"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var flagFormatCheck bool

var formatCmd = &cobra.Command{
	Use:   "format",
	Short: "Format documentation and config assets with Prettier",
	Long:  "Run the repo's Prettier formatter (write mode by default) against Markdown, MDX, JSON, YAML, and other text-based assets.",
	RunE: func(cmd *cobra.Command, args []string) error {
		ctx := cmd.Context()
		if ctx == nil {
			ctx = context.Background()
		}
		npm, err := findNPMExecutable()
		if err != nil {
			return fmt.Errorf("npm is required for formatting: %w", err)
		}
		script := "format"
		action := "formatting"
		if flagFormatCheck {
			script = "format:check"
			action = "checking formatting"
		}
		cmdline := []string{npm, "run", script}
		slog.Info("exec", "cmd", strings.Join(cmdline, " "), "cwd", cfg.Project.Root)
		code, _, _, err := execx.Run(ctx, execx.RunOptions{
			Cmd:           cmdline,
			Cwd:           cfg.Project.Root,
			Interactive:   false,
			DryRun:        flagDryRun,
			CaptureOutput: false,
		})
		if err != nil {
			return fmt.Errorf("error %s (exit=%d): %w", action, code, err)
		}
		return nil
	},
}

func init() {
	formatCmd.Flags().BoolVar(&flagFormatCheck, "check", false, "run in verification mode (no writes)")
	rootCmd.AddCommand(formatCmd)
}
