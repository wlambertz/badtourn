package cmd

import (
	"bytes"
	"fmt"
	"os"
	"path/filepath"

	"github.com/spf13/cobra"
)

var docsCmd = &cobra.Command{
	Use:   "docs",
	Short: "Documentation utilities",
}

var docsGenerateCmd = &cobra.Command{
	Use:   "generate",
	Short: "Generate CLI reference markdown",
	RunE: func(cmd *cobra.Command, args []string) error {
		var buf bytes.Buffer
		buf.WriteString("# RallyOn CLI Reference\n\n")
		writeCmd(&buf, rootCmd)
		outDir := filepath.Join(cfg.Project.Root, "docs")
		_ = os.MkdirAll(outDir, 0755)
		outFile := filepath.Join(outDir, "dev-cli.md")
		return os.WriteFile(outFile, buf.Bytes(), 0644)
	},
}

func writeCmd(buf *bytes.Buffer, c *cobra.Command) {
	buf.WriteString(fmt.Sprintf("## %s\n\n", c.CommandPath()))
	if c.Short != "" {
		buf.WriteString(c.Short + "\n\n")
	}
	buf.WriteString("```\n")
	buf.WriteString(c.UsageString())
	buf.WriteString("```\n\n")
	for _, child := range c.Commands() {
		if !child.IsAvailableCommand() || child.Hidden {
			continue
		}
		writeCmd(buf, child)
	}
}

func init() {
	docsCmd.AddCommand(docsGenerateCmd)
	rootCmd.AddCommand(docsCmd)
}
