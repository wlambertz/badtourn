package cmd

import (
	"fmt"
	"strings"

	"github.com/spf13/cobra"
)

var telemetryCmd = &cobra.Command{
	Use:   "telemetry",
	Short: "Manage CLI telemetry",
}

var telemetryStatusCmd = &cobra.Command{
	Use:   "status",
	Short: "Show telemetry configuration",
	RunE: func(cmd *cobra.Command, args []string) error {
		status := "disabled"
		if cfg != nil && cfg.Telemetry.Enabled {
			status = "enabled"
		}
		fmt.Printf("Telemetry is %s\n", status)
		if cfg != nil {
			fmt.Printf("Endpoint: %s\n", cfg.Telemetry.Endpoint)
			fmt.Printf("Commands: %s\n", strings.Join(cfg.Telemetry.CollectCommands, ", "))
		}
		return nil
	},
}

var telemetryDisableCmd = &cobra.Command{
	Use:   "disable",
	Short: "Print instructions to disable telemetry",
	RunE: func(cmd *cobra.Command, args []string) error {
		fmt.Println("Set RO_TELEMETRY_ENABLED=false or edit ro.yaml (telemetry.enabled: false).")
		return nil
	},
}

var telemetryEnableCmd = &cobra.Command{
	Use:   "enable",
	Short: "Print instructions to enable telemetry",
	RunE: func(cmd *cobra.Command, args []string) error {
		fmt.Println("Set RO_TELEMETRY_ENABLED=true and configure telemetry.endpoint/clientId in ro.yaml.")
		return nil
	},
}

func init() {
	telemetryCmd.AddCommand(telemetryStatusCmd)
	telemetryCmd.AddCommand(telemetryEnableCmd)
	telemetryCmd.AddCommand(telemetryDisableCmd)
	rootCmd.AddCommand(telemetryCmd)
}
