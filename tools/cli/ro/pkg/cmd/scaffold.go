package cmd

import (
	"bytes"
	"errors"
	"fmt"
	"log/slog"
	"os"
	"path/filepath"
	"strings"
	"text/template"
	"time"

	"github.com/spf13/cobra"
)

var (
	flagScaffoldPackage  string
	flagScaffoldBase     string
	flagScaffoldDryRun   bool
	flagScaffoldTemplate string
	flagScaffoldForce    bool
)

var scaffoldCmd = &cobra.Command{
	Use:   "scaffold",
	Short: "Generate boilerplate for RallyOn modules",
}

var scaffoldModuleCmd = &cobra.Command{
	Use:   "module <name>",
	Short: "Create a Modulith slice skeleton (controller/service/domain/test)",
	Args:  cobra.ExactArgs(1),
	RunE: func(cmd *cobra.Command, args []string) error {
		name := strings.TrimSpace(args[0])
		if name == "" {
			return errors.New("module name is required")
		}
		data := newTemplateData(name)
		moduleBase := resolveModuleBase()
		packagePath := flagScaffoldPackage
		if packagePath == "" {
			packagePath = fmt.Sprintf("%s.%s", cfg.Scaffold.DefaultPackage, data.Namespace)
		}
		data.Package = packagePath
		setName := flagScaffoldTemplate
		if setName == "" {
			setName = "module"
		}
		templates, err := loadTemplateSet(setName)
		if err != nil {
			return err
		}
		for _, entry := range templates {
			dest := resolveDestination(entry.Path, moduleBase, packagePath, data)
			if flagScaffoldDryRun {
				fmt.Printf("[dry-run] %s -> %s\n", entry.Path, dest)
				continue
			}
			if err := renderTemplate(dest, entry.Content, data); err != nil {
				return err
			}
		}
		if flagScaffoldDryRun {
			slog.Info("scaffold dry-run complete", "module", name)
		} else {
			slog.Info("module scaffolded", "module", name)
		}
		return nil
	},
}

type templateEntry struct {
	Path    string
	Content string
}

type templateData struct {
	ModuleName   string
	VarName      string
	Package      string
	Namespace    string
	PackageSlash string
	GeneratedAt  string
}

func init() {
	scaffoldModuleCmd.Flags().StringVar(&flagScaffoldPackage, "package", "", "base Java package for the module")
	scaffoldModuleCmd.Flags().StringVar(&flagScaffoldBase, "base", "", "base path override (defaults to scaffold.basePath)")
	scaffoldModuleCmd.Flags().StringVar(&flagScaffoldTemplate, "template-set", "module", "template set name (e.g., module, adapter)")
	scaffoldModuleCmd.Flags().BoolVar(&flagScaffoldDryRun, "dry-run", false, "preview files without writing")
	scaffoldModuleCmd.Flags().BoolVar(&flagScaffoldForce, "force", false, "overwrite existing files")
	scaffoldCmd.AddCommand(scaffoldModuleCmd)
	rootCmd.AddCommand(scaffoldCmd)
}

func newTemplateData(name string) templateData {
	kebab := normalizeKebab(name)
	pascal := toPascal(kebab)
	return templateData{
		ModuleName:   pascal,
		VarName:      strings.ToLower(pascal[:1]) + pascal[1:],
		Namespace:    kebab,
		PackageSlash: strings.ReplaceAll(cfg.Scaffold.DefaultPackage, ".", "/"),
		GeneratedAt:  time.Now().Format(time.RFC3339),
	}
}

func resolveModuleBase() string {
	if flagScaffoldBase != "" {
		return flagScaffoldBase
	}
	base := cfg.Scaffold.BasePath
	if base == "" {
		return filepath.Join(cfg.Paths.ServiceRoot, "src")
	}
	return filepath.Join(cfg.Project.Root, base)
}

func loadTemplateSet(name string) ([]templateEntry, error) {
	dir := filepath.Join(cfg.Project.Root, cfg.Scaffold.TemplateDir, name)
	entries := []templateEntry{}
	err := filepath.WalkDir(dir, func(path string, d os.DirEntry, err error) error {
		if err != nil {
			return err
		}
		if d.IsDir() {
			return nil
		}
		content, err := os.ReadFile(path)
		if err != nil {
			return err
		}
		rel, _ := filepath.Rel(dir, path)
		entries = append(entries, templateEntry{Path: rel, Content: string(content)})
		return nil
	})
	if err != nil {
		return nil, err
	}
	if len(entries) == 0 {
		return nil, fmt.Errorf("no templates found in %s", dir)
	}
	return entries, nil
}

func resolveDestination(relPath, base, packagePath string, data templateData) string {
	result := relPath
	result = strings.ReplaceAll(result, "%MODULE_PASCAL%", data.ModuleName)
	result = strings.ReplaceAll(result, "%MODULE_KEBAB%", data.Namespace)
	result = strings.ReplaceAll(result, "%PKG_SLASH%", strings.ReplaceAll(packagePath, ".", string(os.PathSeparator)))
	result = strings.ReplaceAll(result, "%PKG%", strings.ReplaceAll(packagePath, ".", string(os.PathSeparator)))
	return filepath.Join(cfg.Project.Root, result)
}

func renderTemplate(path string, tmpl string, data templateData) error {
	if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
		return fmt.Errorf("create directory: %w", err)
	}
	if _, err := os.Stat(path); err == nil && !flagScaffoldForce {
		return fmt.Errorf("file already exists: %s", path)
	}
	t, err := template.New(filepath.Base(path)).Funcs(template.FuncMap{
		"ToLower": strings.ToLower,
		"ToUpper": strings.ToUpper,
		"Title":   strings.Title,
		"Kebab":   normalizeKebab,
		"Pascal":  toPascal,
	}).Parse(tmpl)
	if err != nil {
		return err
	}
	var buf bytes.Buffer
	if err := t.Execute(&buf, data); err != nil {
		return err
	}
	return os.WriteFile(path, buf.Bytes(), 0o644)
}

func toPascal(value string) string {
	parts := strings.Split(value, "-")
	for i, p := range parts {
		parts[i] = strings.Title(p)
	}
	return strings.Join(parts, "")
}

func normalizeKebab(value string) string {
	value = strings.TrimSpace(value)
	value = strings.ReplaceAll(value, " ", "-")
	value = strings.ReplaceAll(value, "_", "-")
	value = strings.ToLower(value)
	value = strings.ReplaceAll(value, "--", "-")
	return strings.Trim(value, "-")
}
