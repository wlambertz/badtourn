package cmd

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"runtime"

	"github.com/spf13/cobra"
)

var completionInstallCmd = &cobra.Command{
	Use:   "completion install",
	Short: "Install shell completion script for the current shell",
	RunE: func(cmd *cobra.Command, args []string) error {
		shell := detectShell()
		switch shell {
		case "bash":
			return installCompletion("bash", filepath.Join(os.Getenv("HOME"), ".bash_completion"))
		case "zsh":
			dir := filepath.Join(os.Getenv("HOME"), ".oh-my-zsh", "completions")
			_ = os.MkdirAll(dir, 0o755)
			return installCompletion("zsh", filepath.Join(dir, "_ro"))
		case "fish":
			dir := filepath.Join(os.Getenv("HOME"), ".config", "fish", "completions")
			_ = os.MkdirAll(dir, 0o755)
			return installCompletion("fish", filepath.Join(dir, "ro.fish"))
		case "powershell":
			dir := filepath.Join(os.Getenv("HOME"), "Documents", "WindowsPowerShell", "Modules", "RoCompletion")
			_ = os.MkdirAll(dir, 0o755)
			return installCompletion("powershell", filepath.Join(dir, "ro.ps1"))
		default:
			return fmt.Errorf("unsupported shell: %s", shell)
		}
	},
}

func init() {
	rootCmd.AddCommand(completionInstallCmd)
}

func installCompletion(shell, path string) error {
	f, err := os.Create(path)
	if err != nil {
		return err
	}
	defer f.Close()
	cmd := exec.Command("ro", "completion", shell)
	cmd.Stdout = f
	if err := cmd.Run(); err != nil {
		return err
	}
	fmt.Printf("Installed %s completion to %s\n", shell, path)
	return nil
}

func detectShell() string {
	if runtime.GOOS == "windows" {
		return "powershell"
	}
	shell := os.Getenv("SHELL")
	if shell == "" {
		return "bash"
	}
	return filepath.Base(shell)
}
