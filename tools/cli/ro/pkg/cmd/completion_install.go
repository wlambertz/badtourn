package cmd

import (
	"fmt"
	"io"
	"os"
	"path/filepath"
	"runtime"

	"github.com/spf13/cobra"
)

var completionInstallCmd = &cobra.Command{
	Use:   "completion install",
	Short: "Install shell completion script for the current shell",
	RunE: func(cmd *cobra.Command, args []string) (err error) {
		shell := detectShell()
		var installPath string

		loader := newLoader(fmt.Sprintf("Installing %s completion", shell))
		if loader != nil {
			loader.Start()
			defer func() {
				if err != nil {
					loader.Stop(fmt.Sprintf("Failed to install %s completion: %v", shell, err))
				} else if installPath != "" {
					loader.Stop(fmt.Sprintf("Installed %s completion to %s", shell, installPath))
				} else {
					loader.Stop("")
				}
			}()
		}

		switch shell {
		case "bash":
			installPath, err = installCompletion("bash", filepath.Join(os.Getenv("HOME"), ".bash_completion"))
		case "zsh":
			dir := filepath.Join(os.Getenv("HOME"), ".oh-my-zsh", "completions")
			_ = os.MkdirAll(dir, 0o755)
			installPath, err = installCompletion("zsh", filepath.Join(dir, "_ro"))
		case "fish":
			dir := filepath.Join(os.Getenv("HOME"), ".config", "fish", "completions")
			_ = os.MkdirAll(dir, 0o755)
			installPath, err = installCompletion("fish", filepath.Join(dir, "ro.fish"))
		case "powershell":
			dir := filepath.Join(os.Getenv("HOME"), "Documents", "WindowsPowerShell", "Modules", "RoCompletion")
			_ = os.MkdirAll(dir, 0o755)
			installPath, err = installCompletion("powershell", filepath.Join(dir, "ro.ps1"))
		default:
			err = fmt.Errorf("unsupported shell: %s", shell)
		}

		if err != nil {
			return err
		}
		if loader == nil {
			fmt.Printf("Installed %s completion to %s\n", shell, installPath)
		}
		return nil
	},
}

func init() {
	rootCmd.AddCommand(completionInstallCmd)
}

func installCompletion(shell, path string) (string, error) {
	if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
		return "", err
	}
	f, err := os.Create(path)
	if err != nil {
		return "", err
	}
	defer f.Close()

	var gen func(io.Writer) error
	switch shell {
	case "bash":
		gen = func(w io.Writer) error { return rootCmd.GenBashCompletionV2(w, true) }
	case "zsh":
		gen = func(w io.Writer) error { return rootCmd.GenZshCompletion(w) }
	case "fish":
		gen = func(w io.Writer) error { return rootCmd.GenFishCompletion(w, true) }
	case "powershell":
		gen = func(w io.Writer) error { return rootCmd.GenPowerShellCompletionWithDesc(w) }
	default:
		return "", fmt.Errorf("unsupported shell for generation: %s", shell)
	}

	if err := gen(f); err != nil {
		return "", err
	}
	return path, nil
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
