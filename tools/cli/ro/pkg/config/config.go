package config

import (
    "errors"
    "fmt"
    "os"
    "path/filepath"
    "strings"

    "github.com/spf13/viper"
)

type Paths struct {
    ServiceRoot    string `mapstructure:"serviceRoot"`
    AppRoot        string `mapstructure:"appRoot"`
    ThirdPartyRoot string `mapstructure:"thirdPartyRoot"`
    AdminRoot      string `mapstructure:"adminRoot"`
}

type Build struct {
    MavenWrapper  string   `mapstructure:"mavenWrapper"`
    DefaultGoals  []string `mapstructure:"defaultGoals"`
}

type Docker struct {
    Workflow string `mapstructure:"workflow"`
    ImageRepo string `mapstructure:"imageRepo"`
}

type Git struct {
    ConventionalCommits bool `mapstructure:"conventionalCommits"`
}

type Output struct {
    JSON    bool `mapstructure:"json"`
    Verbose bool `mapstructure:"verbose"`
}

type Project struct {
    Name string `mapstructure:"name"`
    Root string `mapstructure:"root"`
}

type Config struct {
    Project Project `mapstructure:"project"`
    Paths   Paths   `mapstructure:"paths"`
    Build   Build   `mapstructure:"build"`
    Docker  Docker  `mapstructure:"docker"`
    Git     Git     `mapstructure:"git"`
    Output  Output  `mapstructure:"output"`
}

// Load merges configuration from ro.yaml, environment, and provides accessors.
func Load(repoRoot string) (*Config, error) {
    v := viper.New()
    v.SetEnvPrefix("RO")
    v.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
    v.AutomaticEnv()

    // Defaults
    v.SetDefault("project.name", "rallyon")
    v.SetDefault("project.root", ".")
    v.SetDefault("paths.serviceRoot", "service/tournamentmgmt")
    v.SetDefault("paths.appRoot", "application")
    v.SetDefault("paths.thirdPartyRoot", "3rd_party")
    v.SetDefault("paths.adminRoot", "admin")
    v.SetDefault("build.mavenWrapper", "./mvnw")
    v.SetDefault("build.defaultGoals", []string{"clean", "verify"})
    v.SetDefault("docker.workflow", ".github/workflows/Tournamentmgmt-docker.yaml")
    v.SetDefault("docker.imageRepo", "rallyon/tournamentmgmt")
    v.SetDefault("git.conventionalCommits", true)
    v.SetDefault("output.json", false)
    v.SetDefault("output.verbose", false)

    // Config file from repo root if provided, else perform upward search from CWD
    if repoRoot == "" {
        start, _ := os.Getwd()
        repoRoot = findRepoRoot(start)
    }
    if repoRoot != "" {
        v.SetConfigName("ro")
        v.SetConfigType("yaml")
        v.AddConfigPath(repoRoot)
        _ = v.ReadInConfig() // ignore if missing; env/defaults still apply
    }

    var cfg Config
    if err := v.Unmarshal(&cfg); err != nil {
        return nil, err
    }

    // Normalize to absolute paths
    if repoRoot == "" {
        repoRoot, _ = os.Getwd()
    }
    toAbs := func(p string) string {
        if p == "" { return p }
        if filepath.IsAbs(p) { return p }
        return filepath.Clean(filepath.Join(repoRoot, p))
    }
    cfg.Project.Root = toAbs(cfg.Project.Root)
    cfg.Paths.ServiceRoot = toAbs(cfg.Paths.ServiceRoot)
    cfg.Paths.AppRoot = toAbs(cfg.Paths.AppRoot)
    cfg.Paths.ThirdPartyRoot = toAbs(cfg.Paths.ThirdPartyRoot)
    cfg.Paths.AdminRoot = toAbs(cfg.Paths.AdminRoot)

    // Platform-specific maven wrapper normalization
    cfg.Build.MavenWrapper = normalizeMavenWrapper(cfg.Build.MavenWrapper)

    // Validate basics
    if _, err := os.Stat(cfg.Paths.ServiceRoot); err != nil {
        return nil, fmt.Errorf("paths.serviceRoot does not exist: %w", err)
    }
    if cfg.Build.MavenWrapper == "" {
        return nil, errors.New("build.mavenWrapper is required")
    }

    return &cfg, nil
}

func findRepoRoot(start string) string {
    dir := start
    for {
        if dir == "" || dir == "/" || dir == "\\" {
            return ""
        }
        if _, err := os.Stat(filepath.Join(dir, ".git")); err == nil {
            return dir
        }
        parent := filepath.Dir(dir)
        if parent == dir { return "" }
        dir = parent
    }
}

func normalizeMavenWrapper(value string) string {
    if value == "" {
        return value
    }
    // On Windows prefer mvnw.cmd if wrapper path points to repo root
    if os.PathSeparator == '\\' {
        base := filepath.Base(value)
        if base == "./mvnw" || base == "mvnw" {
            return "mvnw.cmd"
        }
    }
    return value
}


