package cmd

import (
	"context"
	"reflect"
	"slices"
	"testing"

	"github.com/wlambertz/rallyon/tools/cli/ro/pkg/config"
)

func TestDockerImageRef(t *testing.T) {
	tests := []struct {
		name     string
		registry string
		repo     string
		want     string
	}{
		{"relative repo", "ghcr.io", "wlambertz/tournamentmgmt", "ghcr.io/wlambertz/tournamentmgmt"},
		{"absolute repo", "ghcr.io", "registry.example.com/app", "registry.example.com/app"},
		{"no registry", "", "custom/app", "custom/app"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := dockerImageRef(tt.registry, tt.repo); got != tt.want {
				t.Fatalf("got %q want %q", got, tt.want)
			}
		})
	}
}

func TestSanitizeTag(t *testing.T) {
	if sanitizeTag("Feature/Branch") != "feature-branch" {
		t.Fatalf("unexpected sanitize result")
	}
	if sanitizeTag("") != "latest" {
		t.Fatalf("empty should default to latest")
	}
}

func TestCollectDockerTagsManualOnly(t *testing.T) {
	flagDockerBranchTag = false
	flagDockerShaTag = false
	flagDockerLatestTag = false
	flagDockerManualTags = []string{"custom"}
	defer func() {
		flagDockerBranchTag = true
		flagDockerShaTag = true
		flagDockerLatestTag = false
		flagDockerManualTags = nil
	}()
	tags, err := collectDockerTags(context.Background(), "ghcr.io/acme/service")
	if err != nil {
		t.Fatalf("unexpected err: %v", err)
	}
	if len(tags) != 1 || tags[0] != "ghcr.io/acme/service:custom" {
		t.Fatalf("unexpected tags: %#v", tags)
	}
}

func TestCollectDockerTagsDefaults(t *testing.T) {
	origBranch := gitRevParseFn
	defer func() { gitRevParseFn = origBranch }()
	gitRevParseFn = func(ctx context.Context, args ...string) (string, error) {
		if len(args) > 0 && args[0] == "--abbrev-ref" {
			return "Feature/Cool", nil
		}
		return "abc1234", nil
	}
	flagDockerBranchTag = true
	flagDockerShaTag = true
	flagDockerLatestTag = false
	flagDockerManualTags = nil
	tags, err := collectDockerTags(context.Background(), "ghcr.io/acme/service")
	if err != nil {
		t.Fatalf("unexpected err: %v", err)
	}
	slices.Sort(tags)
	expect := []string{
		"ghcr.io/acme/service:feature-cool",
		"ghcr.io/acme/service:sha-abc1234",
	}
	slices.Sort(expect)
	if !reflect.DeepEqual(tags, expect) {
		t.Fatalf("tags mismatch:\n got %v\nwant %v", tags, expect)
	}
}

func TestComposeBaseArgs(t *testing.T) {
	cfg = &config.Config{
		Docker: config.Docker{
			ComposeFile: "docker-compose.yml",
		},
	}
	flagComposeFile = ""
	flagComposeEnvFile = ""
	args := composeBaseArgs()
	if !reflect.DeepEqual(args, []string{"docker", "compose", "-f", "docker-compose.yml"}) {
		t.Fatalf("unexpected args: %v", args)
	}
	flagComposeFile = "compose.override.yml"
	flagComposeEnvFile = ".env.dev"
	args = composeBaseArgs()
	if !reflect.DeepEqual(args, []string{"docker", "compose", "-f", "compose.override.yml", "--env-file", ".env.dev"}) {
		t.Fatalf("unexpected args: %v", args)
	}
}
