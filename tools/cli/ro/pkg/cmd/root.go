package cmd

import (
	"fmt"
	"log/slog"
	"os"
	"strings"
	"time"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/logx"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/telemetry"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/version"
)

var (
	flagVerbose    bool
	flagJSON       bool
	flagDryRun     bool
	flagYes        bool
	flagQuiet      bool
	cfg            *config.Config
	currentCommand string
)

// rootCmd is the base command for the CLI.
var rootCmd = &cobra.Command{
	Use:   "ro",
	Short: "RallyOn developer CLI",
	Long:  "RallyOn developer CLI to streamline builds, tests, deployments, and common workflows.",
	PersistentPreRunE: func(cmd *cobra.Command, args []string) error {
		// Bind flags to viper keys so they can override file/env
		_ = viper.BindPFlag("output.verbose", cmd.Flags().Lookup("verbose"))
		_ = viper.BindPFlag("output.json", cmd.Flags().Lookup("json"))

		// Load merged config
		var err error
		cfg, err = config.Load("")
		if err != nil {
			return fmt.Errorf("config error: %w", err)
		}

		quiet := flagQuiet || cfg.Output.Quiet
		verbose := flagVerbose
		if !flagVerbose {
			verbose = cfg.Output.Verbose
		}
		jsonOutput := flagJSON
		if !flagJSON {
			jsonOutput = cfg.Output.JSON
		}

		logger := logx.New(logx.Options{
			JSON:    jsonOutput,
			Verbose: verbose && !quiet,
			Writer:  os.Stdout,
			Quiet:   quiet,
		})
		slog.SetDefault(logger)

		flagYes = viper.GetBool("yes")
		deployDefaultWait = cfg.Deploy.DefaultWait
		telemetry.Init(&cfg.Telemetry)
		currentCommand = cmd.CommandPath()
		return nil
	},
}

// Execute runs the CLI.
func Execute() error {
	start := time.Now()
	err := rootCmd.Execute()
	duration := time.Since(start)
	if telemetry.Enabled() {
		cmdName := strings.TrimSpace(currentCommand)
		if cmdName == "" {
			cmdName = strings.Join(os.Args[1:], " ")
			if cmdName == "" {
				cmdName = rootCmd.Use
			}
		}
		telemetry.Track(telemetry.Event{
			Command:  cmdName,
			Duration: duration,
			ExitCode: exitCodeFromError(err),
			Success:  err == nil,
		})
	}
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
	}
	return err
}

func init() {
	rootCmd.PersistentFlags().BoolVar(&flagVerbose, "verbose", false, "enable verbose output")
	rootCmd.PersistentFlags().BoolVar(&flagJSON, "json", false, "emit JSON-formatted output")
	rootCmd.PersistentFlags().BoolVar(&flagDryRun, "dry-run", false, "show actions without executing")
	rootCmd.PersistentFlags().BoolVar(&flagYes, "yes", false, "auto-confirm prompts and bypass interactive checks")
	rootCmd.PersistentFlags().BoolVar(&flagQuiet, "quiet", false, "suppress info logs (errors only)")
	// Ensure viper env prefix is active even if no config file exists
	viper.SetEnvPrefix("RO")
	viper.AutomaticEnv()
	_ = viper.BindPFlag("yes", rootCmd.PersistentFlags().Lookup("yes"))

	rootCmd.AddCommand(&cobra.Command{
		Use:   "version",
		Short: "Show CLI version information",
		Run: func(cmd *cobra.Command, args []string) {
			fmt.Printf("ro %s (%s) %s\n", version.Version, version.Commit, version.Date)
		},
	})
}

func exitCodeFromError(err error) int {
	if err == nil {
		return 0
	}
	return 1
}

func logWriter(quiet bool) func([]byte) (int, error) {
	if !quiet {
		return nil
	}
	return nil
}
