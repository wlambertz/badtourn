package cmd

import (
	"fmt"
	"log/slog"
	"os"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/logx"
)

var (
	flagVerbose bool
	flagJSON    bool
	flagDryRun  bool
	flagYes     bool
	cfg         *config.Config
)

// rootCmd is the base command for the CLI.
var rootCmd = &cobra.Command{
	Use:   "ro",
	Short: "RallyOn developer CLI",
	Long:  "RallyOn developer CLI to streamline builds, tests, deployments, and common workflows.",
	PersistentPreRunE: func(cmd *cobra.Command, args []string) error {
		// Initialize logging first
		logger := logx.New(logx.Options{JSON: flagJSON, Verbose: flagVerbose})
		slog.SetDefault(logger)

		// Bind flags to viper keys so they can override file/env
		_ = viper.BindPFlag("output.verbose", cmd.Flags().Lookup("verbose"))
		_ = viper.BindPFlag("output.json", cmd.Flags().Lookup("json"))

		// Load merged config
		var err error
		cfg, err = config.Load("")
		if err != nil {
			return fmt.Errorf("config error: %w", err)
		}
		// Reconfigure logger if config flips verbosity/json and flags not set
		if !flagVerbose && cfg.Output.Verbose || !flagJSON && cfg.Output.JSON {
			slog.SetDefault(logx.New(logx.Options{JSON: cfg.Output.JSON, Verbose: cfg.Output.Verbose}))
		}
		flagYes = viper.GetBool("yes")
		return nil
	},
}

// Execute runs the CLI.
func Execute() error {
	if err := rootCmd.Execute(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		return err
	}
	return nil
}

func init() {
	rootCmd.PersistentFlags().BoolVar(&flagVerbose, "verbose", false, "enable verbose output")
	rootCmd.PersistentFlags().BoolVar(&flagJSON, "json", false, "emit JSON-formatted output")
	rootCmd.PersistentFlags().BoolVar(&flagDryRun, "dry-run", false, "show actions without executing")
	rootCmd.PersistentFlags().BoolVar(&flagYes, "yes", false, "auto-confirm prompts and bypass interactive checks")
	// Ensure viper env prefix is active even if no config file exists
	viper.SetEnvPrefix("RO")
	viper.AutomaticEnv()
	_ = viper.BindPFlag("yes", rootCmd.PersistentFlags().Lookup("yes"))
}
