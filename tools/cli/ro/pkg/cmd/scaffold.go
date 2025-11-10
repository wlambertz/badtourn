package cmd

import (
	"fmt"
	"log/slog"
	"os"
	"path/filepath"
	"strings"
	"text/template"

	"github.com/spf13/cobra"
)

var (
	flagScaffoldPackage string
	flagScaffoldBase    string
	flagScaffoldDryRun  bool
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
		name := args[0]
		if strings.TrimSpace(name) == "" {
			return fmt.Errorf("module name is required")
		}
		snake := strings.ToLower(strings.ReplaceAll(name, " ", "-"))
		pascal := strings.Title(strings.ReplaceAll(name, "-", " "))
		pascal = strings.ReplaceAll(pascal, " ", "")
		camel := strings.ToLower(pascal[:1]) + pascal[1:]

		moduleBase := flagScaffoldBase
		if moduleBase == "" {
			moduleBase = filepath.Join(cfg.Paths.ServiceRoot, "src", "main", "java")
		}
		packagePath := flagScaffoldPackage
		if packagePath == "" {
			packagePath = "com.rallyon.tournament." + snake
		}
		packageDir := filepath.Join(moduleBase, strings.ReplaceAll(packagePath, ".", string(os.PathSeparator)))
		files := map[string]string{
			filepath.Join(packageDir, "api", pascal+"Controller.java"):      controllerTemplate,
			filepath.Join(packageDir, "application", pascal+"Service.java"): serviceTemplate,
			filepath.Join(packageDir, "domain", pascal+".java"):             domainTemplate,
			filepath.Join(moduleBase, "resources", "modules", snake+".md"):  readmeTemplate,
		}

		testBase := filepath.Join(cfg.Paths.ServiceRoot, "src", "test", "java")
		testPkgDir := filepath.Join(testBase, strings.ReplaceAll(packagePath, ".", string(os.PathSeparator)), "api")
		files[filepath.Join(testPkgDir, pascal+"ControllerTest.java")] = controllerTestTemplate

		data := templateData{
			ModuleName:   pascal,
			VarName:      camel,
			Package:      packagePath,
			Namespace:    snake,
			PackageSlash: strings.ReplaceAll(packagePath, ".", "/"),
		}

		for path, tmpl := range files {
			if flagScaffoldDryRun {
				fmt.Printf("[dry-run] would write %s\n", path)
				continue
			}
			if err := renderTemplate(path, tmpl, data); err != nil {
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

type templateData struct {
	ModuleName   string
	VarName      string
	Package      string
	Namespace    string
	PackageSlash string
}

func init() {
	scaffoldModuleCmd.Flags().StringVar(&flagScaffoldPackage, "package", "", "base Java package for the module")
	scaffoldModuleCmd.Flags().StringVar(&flagScaffoldBase, "base", "", "base path for Java sources (defaults to service module src/main/java)")
	scaffoldModuleCmd.Flags().BoolVar(&flagScaffoldDryRun, "dry-run", false, "preview files without writing")
	scaffoldCmd.AddCommand(scaffoldModuleCmd)
	rootCmd.AddCommand(scaffoldCmd)
}

func renderTemplate(path string, tmpl string, data templateData) error {
	if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
		return fmt.Errorf("create directory: %w", err)
	}
	if _, err := os.Stat(path); err == nil {
		return fmt.Errorf("file already exists: %s", path)
	}
	t, err := template.New(filepath.Base(path)).Parse(tmpl)
	if err != nil {
		return err
	}
	f, err := os.Create(path)
	if err != nil {
		return err
	}
	defer f.Close()
	return t.Execute(f, data)
}

const controllerTemplate = `package {{ .Package }}.api;

import org.springframework.modulith.ApplicationModule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApplicationModule(displayName = "{{ .ModuleName }}")
@RestController
@RequestMapping("/api/{{ .Namespace }}")
public class {{ .ModuleName }}Controller {

    private final {{ .Package }}.application.{{ .ModuleName }}Service {{ .VarName }}Service;

    public {{ .ModuleName }}Controller({{ .Package }}.application.{{ .ModuleName }}Service {{ .VarName }}Service) {
        this.{{ .VarName }}Service = {{ .VarName }}Service;
    }

    @GetMapping("/health")
    public String health() {
        return {{ .VarName }}Service.health();
    }
}
`

const serviceTemplate = `package {{ .Package }}.application;

import org.springframework.stereotype.Service;

@Service
public class {{ .ModuleName }}Service {

    public String health() {
        return "{{ .ModuleName }} module is alive";
    }
}
`

const domainTemplate = `package {{ .Package }}.domain;

public record {{ .ModuleName }}(String id) {
}
`

const readmeTemplate = `# {{ .ModuleName }} Module

- Responsible for: TODO
- Owners: TODO
- Key flows: TODO
`

const controllerTestTemplate = `package {{ .Package }}.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class {{ .ModuleName }}ControllerTest {

    @Test
    void healthReturnsMessage() {
        {{ .ModuleName }}Controller controller = new {{ .ModuleName }}Controller(new {{ .Package }}.application.{{ .ModuleName }}Service());
        assertThat(controller.health()).contains("alive");
    }
}
`
