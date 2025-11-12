package cmd

import (
	"fmt"
	"os/exec"
	"runtime"
)

func findNPMExecutable() (string, error) {
	candidates := []string{"npm"}
	if runtime.GOOS == "windows" {
		candidates = []string{"npm.cmd", "npm.exe", "npm"}
	}
	for _, c := range candidates {
		if _, err := exec.LookPath(c); err == nil {
			return c, nil
		}
	}
	return "", fmt.Errorf("npm was not found on PATH")
}
