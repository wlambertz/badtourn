package cmd

import (
    "fmt"
    "os"

    "github.com/spf13/cobra"
)

var (
    flagVerbose bool
    flagJSON    bool
    flagDryRun  bool
)

// rootCmd is the base command for the CLI.
var rootCmd = &cobra.Command{
    Use:   "ro",
    Short: "RallyOn developer CLI",
    Long:  "RallyOn developer CLI to streamline builds, tests, deployments, and common workflows.",
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
}


