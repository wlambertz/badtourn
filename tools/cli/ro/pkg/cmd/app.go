package cmd

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"sort"
	"strings"

	"github.com/spf13/cobra"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/execx"
)

var appCmd = &cobra.Command{
	Use:   "app",
	Short: "Work with application shells (e.g. organizer portal)",
}

func newAppScriptCommand(use, description, scriptKey string) *cobra.Command {
	return &cobra.Command{
		Use:   fmt.Sprintf("%s <name>", use),
		Short: description,
		Args:  cobra.ExactArgs(1),
		RunE: func(cmd *cobra.Command, args []string) error {
			return runAppScript(args[0], scriptKey)
		},
	}
}

func runAppScript(appName, scriptKey string) error {
	if len(cfg.Apps) == 0 {
		return errors.New("no apps configured in ro.yaml (add an apps section)")
	}

	appCfg, ok := cfg.Apps[appName]
	if !ok {
		return fmt.Errorf("unknown app %q; available: %s", appName, strings.Join(availableAppNames(), ", "))
	}

	cmdline, err := resolveAppScriptCommand(appCfg, scriptKey)
	if err != nil {
		return err
	}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	slog.Info("exec", "cmd", cmdline, "cwd", appCfg.Path)
	code, _, _, err := execx.Run(ctx, execx.RunOptions{
		Cmd:           cmdline,
		Cwd:           appCfg.Path,
		Timeout:       0,
		Interactive:   true,
		DryRun:        flagDryRun,
		CaptureOutput: false,
	})
	if err != nil {
		return fmt.Errorf("app %s %s failed (exit=%d): %w", appName, scriptKey, code, err)
	}

	return nil
}

func resolveAppScriptCommand(appCfg config.App, scriptKey string) ([]string, error) {
	script, ok := appCfg.Scripts[scriptKey]
	if !ok {
		available := make([]string, 0, len(appCfg.Scripts))
		for k := range appCfg.Scripts {
			available = append(available, k)
		}
		sort.Strings(available)
		return nil, fmt.Errorf("app %s does not define script %q (available: %s)", appCfg.Path, scriptKey, strings.Join(available, ", "))
	}
	if len(script) == 0 {
		return nil, fmt.Errorf("app %s script %q has no command defined", appCfg.Path, scriptKey)
	}

	cmdline := append([]string(nil), script...)
	if cmdline[0] == "npm" {
		npm, err := findNPMExecutable()
		if err != nil {
			return nil, err
		}
		cmdline[0] = npm
	}
	return cmdline, nil
}

func availableAppNames() []string {
	names := make([]string, 0, len(cfg.Apps))
	for name := range cfg.Apps {
		names = append(names, name)
	}
	sort.Strings(names)
	return names
}

func init() {
	appCmd.AddCommand(newAppScriptCommand("install", "Install npm dependencies for the app", "install"))
	appCmd.AddCommand(newAppScriptCommand("start", "Start the local development server", "start"))
	appCmd.AddCommand(newAppScriptCommand("lint", "Run the app linter", "lint"))
	appCmd.AddCommand(newAppScriptCommand("test", "Run the default app unit tests", "test"))
	appCmd.AddCommand(newAppScriptCommand("test-ci", "Run the CI headless test target", "test:ci"))
	rootCmd.AddCommand(appCmd)
}
