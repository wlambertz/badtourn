package cmd

import (
    "bytes"
    "encoding/json"
    "errors"
    "fmt"
    "log/slog"
    "net/http"
    "os"
    "strings"
    "time"

    "github.com/spf13/cobra"
)

var (
    flagDeployEnv   string
    flagDeployNotes string
)

type workflowDispatch struct {
    Ref    string                 `json:"ref"`
    Inputs map[string]string      `json:"inputs,omitempty"`
}

var deployCmd = &cobra.Command{
    Use:   "deploy",
    Short: "Trigger a deploy via GitHub Actions",
    RunE: func(cmd *cobra.Command, args []string) error {
        token := strings.TrimSpace(os.Getenv("GITHUB_TOKEN"))
        if token == "" {
            return errors.New("GITHUB_TOKEN env var is required for deploy")
        }
        // Use current branch for ref, fallback to main
        ref := os.Getenv("GITHUB_REF_NAME")
        if ref == "" { ref = "main" }

        // Workflow file path from config; GitHub API expects file name
        wf := cfg.Docker.Workflow
        if wf == "" { return errors.New("no workflow configured") }
        // Extract basename (e.g., Tournamentmgmt-docker.yaml)
        lastSlash := strings.LastIndexAny(wf, "/\\")
        if lastSlash >= 0 { wf = wf[lastSlash+1:] }

        ownerRepo := "wlambertz/rallyon" // repository owner/name
        api := fmt.Sprintf("https://api.github.com/repos/%s/actions/workflows/%s/dispatches", ownerRepo, wf)
        body := workflowDispatch{Ref: ref, Inputs: map[string]string{"env": flagDeployEnv, "notes": flagDeployNotes}}
        payload, _ := json.Marshal(body)

        slog.Info("deploy", "workflow", wf, "ref", ref, "env", flagDeployEnv, "dryRun", flagDryRun)
        if flagDryRun {
            return nil
        }

        req, err := http.NewRequest(http.MethodPost, api, bytes.NewReader(payload))
        if err != nil { return err }
        req.Header.Set("Accept", "application/vnd.github+json")
        req.Header.Set("Authorization", "Bearer "+token)
        req.Header.Set("X-GitHub-Api-Version", "2022-11-28")
        req.Header.Set("Content-Type", "application/json")

        httpClient := &http.Client{ Timeout: 15 * time.Second }
        resp, err := httpClient.Do(req)
        if err != nil { return err }
        defer resp.Body.Close()
        if resp.StatusCode != http.StatusNoContent {
            return fmt.Errorf("workflow dispatch failed: %s", resp.Status)
        }
        slog.Info("deploy triggered", "status", resp.Status)
        return nil
    },
}

func init() {
    deployCmd.Flags().StringVar(&flagDeployEnv, "env", "dev", "deployment environment (e.g., dev, prod)")
    deployCmd.Flags().StringVar(&flagDeployNotes, "notes", "", "optional notes for the deployment")
    rootCmd.AddCommand(deployCmd)
}


