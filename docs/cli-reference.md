# RallyOn CLI Reference

_Generated on 2025-11-10T12:21:31+01:00_

## Table of Contents

- [ro](#ro)
- [ro build](#ro-build)
- [ro completion](#ro-completion)
- [ro completion bash](#ro-completion-bash)
- [ro completion fish](#ro-completion-fish)
- [ro completion powershell](#ro-completion-powershell)
- [ro completion zsh](#ro-completion-zsh)
- [ro config](#ro-config)
- [ro config show](#ro-config-show)
- [ro deploy](#ro-deploy)
- [ro docker](#ro-docker)
- [ro docker build](#ro-docker-build)
- [ro docker compose](#ro-docker-compose)
- [ro docker compose down](#ro-docker-compose-down)
- [ro docker compose logs](#ro-docker-compose-logs)
- [ro docker compose up](#ro-docker-compose-up)
- [ro docs](#ro-docs)
- [ro docs generate](#ro-docs-generate)
- [ro git](#ro-git)
- [ro git branch](#ro-git-branch)
- [ro git commit](#ro-git-commit)
- [ro git push](#ro-git-push)
- [ro git rebase](#ro-git-rebase)
- [ro git status](#ro-git-status)
- [ro git sync](#ro-git-sync)
- [ro run](#ro-run)
- [ro run service](#ro-run-service)
- [ro scaffold](#ro-scaffold)
- [ro scaffold module](#ro-scaffold-module)
- [ro test](#ro-test)
- [ro version](#ro-version)

## ro

RallyOn developer CLI

RallyOn developer CLI to streamline builds, tests, deployments, and common workflows.

```bash
ro [flags]
```

### Flags for ro

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro build

Build the project or specific services

```bash
ro build [flags]
```

### Flags for ro build

```text
--ci     CI mode (batch, non-interactive)
      --fast   skip tests for faster build
```

### Inherited Flags for ro build

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion

Generate the autocompletion script for the specified shell

Generate the autocompletion script for ro for the specified shell.
See each sub-command's help for details on how to use the generated script.

```bash
ro completion
```

### Inherited Flags for ro completion

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion bash

Generate the autocompletion script for bash

Generate the autocompletion script for the bash shell.

This script depends on the 'bash-completion' package.
If it is not installed already, you can install it via your OS's package manager.

To load completions in your current shell session:

```bash
source <(ro completion bash)
```

To load completions for every new session, execute once:

#### Linux (bash):

```bash
ro completion bash > /etc/bash_completion.d/ro
```

#### macOS (bash):

```bash
ro completion bash > $(brew --prefix)/etc/bash_completion.d/ro
```

You will need to start a new shell for this setup to take effect.

```bash
ro completion bash
```

### Flags for ro completion bash

```text
--no-descriptions   disable completion descriptions
```

### Inherited Flags for ro completion bash

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion fish

Generate the autocompletion script for fish

Generate the autocompletion script for the fish shell.

To load completions in your current shell session:

	ro completion fish | source

To load completions for every new session, execute once:

	ro completion fish > ~/.config/fish/completions/ro.fish

You will need to start a new shell for this setup to take effect.

```bash
ro completion fish [flags]
```

### Flags for ro completion fish

```text
--no-descriptions   disable completion descriptions
```

### Inherited Flags for ro completion fish

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion powershell

Generate the autocompletion script for powershell

Generate the autocompletion script for powershell.

To load completions in your current shell session:

	ro completion powershell | Out-String | Invoke-Expression

To load completions for every new session, add the output of the above command
to your powershell profile.

```bash
ro completion powershell [flags]
```

### Flags for ro completion powershell

```text
--no-descriptions   disable completion descriptions
```

### Inherited Flags for ro completion powershell

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion zsh

Generate the autocompletion script for zsh

Generate the autocompletion script for the zsh shell.

If shell completion is not already enabled in your environment you will need
to enable it.  You can execute the following once:

	echo "autoload -U compinit; compinit" >> ~/.zshrc

To load completions in your current shell session:

	source <(ro completion zsh)

To load completions for every new session, execute once:

#### Linux (zsh):

	ro completion zsh > "${fpath[1]}/_ro"

#### macOS (zsh):

	ro completion zsh > $(brew --prefix)/share/zsh/site-functions/_ro

You will need to start a new shell for this setup to take effect.

```bash
ro completion zsh [flags]
```

### Flags for ro completion zsh

```text
--no-descriptions   disable completion descriptions
```

### Inherited Flags for ro completion zsh

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro config

Configuration commands

```bash
ro config
```

### Inherited Flags for ro config

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro config show

Show merged configuration

```bash
ro config show
```

### Inherited Flags for ro config show

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro deploy

Trigger a deploy via GitHub Actions with safety checks

```bash
ro deploy [flags]
```

### Flags for ro deploy

```text
--check-only      run preflight checks without triggering workflow
      --env string      deployment environment (e.g., dev, prod) (default "dev")
      --input strings   additional workflow input (key=value, repeatable)
      --notes string    optional notes for the deployment
      --wait            wait for workflow completion (overrides deploy.defaultWait) (default true)
```

### Inherited Flags for ro deploy

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker

Docker workflows: build, push, compose

```bash
ro docker
```

### Inherited Flags for ro docker

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker build

Build (and optionally push) Docker image for tournamentmgmt

```bash
ro docker build [flags]
```

### Flags for ro docker build

```text
--branch-tag    include branch-based tag (default true)
      --latest        include latest tag
      --push          push images after build
      --sha-tag       include short SHA tag (default true)
      --tag strings   extra tag(s) to apply (without registry unless fully qualified)
```

### Inherited Flags for ro docker build

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker compose

Compose up/down the local stack

```bash
ro docker compose
```

### Flags for ro docker compose

```text
--env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --profile strings   compose profile(s) to enable
```

### Inherited Flags for ro docker compose

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker compose down

docker compose down

```bash
ro docker compose down
```

### Inherited Flags for ro docker compose down

```text
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docker compose logs

docker compose logs

```bash
ro docker compose logs
```

### Inherited Flags for ro docker compose logs

```text
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docker compose up

docker compose up

```bash
ro docker compose up
```

### Inherited Flags for ro docker compose up

```text
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docs

Documentation utilities

```bash
ro docs [flags]
```

### Inherited Flags for ro docs

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docs generate

Generate CLI reference markdown

```bash
ro docs generate [flags]
```

### Flags for ro docs generate

```text
--commit-message string   commit message when publishing to wiki (overrides
                                docs.wikiCommitMessage)
      --commit-wiki             commit wiki changes after generation
  -h, --help                    help for generate
      --output string           file to write (defaults to docs.output)
      --wiki                    also write to wiki docs file
      --wiki-output string      wiki docs path (defaults to docs.wikiOutput)
```

### Inherited Flags for ro docs generate

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git

Git helper commands (status, branch, rebase, commit)

```bash
ro git
```

### Inherited Flags for ro git

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git branch

Display branch information and ahead/behind summary

```bash
ro git branch [flags]
```

### Flags for ro git branch

```text
--verbose   show verbose branch listing
```

### Inherited Flags for ro git branch

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git commit

Guide conventional commits

```bash
ro git commit [flags]
```

### Flags for ro git commit

```text
--all                     stage tracked files before committing (git add -A)
      --body string             body/description (supports newlines)
      --breaking                mark commit as breaking change (adds '!')
      --breaking-notes string   details for BREAKING CHANGE footer
      --scope string            optional commit scope
      --summary string          short summary line
      --type string             commit type (feat, fix, chore, ...)
      --wip                     mark commit as work in progress
```

### Inherited Flags for ro git commit

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git push

Push current branch to configured remote (with safeguards)

```bash
ro git push [flags]
```

### Flags for ro git push

```text
--branch string   branch to push (defaults to current)
      --force           use --force-with-lease
      --remote string   remote name (defaults to git.defaultRemote)
```

### Inherited Flags for ro git push

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git rebase

Pull with rebase against the specified upstream

```bash
ro git rebase [flags]
```

### Flags for ro git rebase

```text
--autostash     auto-stash local changes during rebase (default true)
      --onto string   upstream (remote/branch) to rebase against (default "origin/main")
```

### Inherited Flags for ro git rebase

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git status

Show git status (short by default)

```bash
ro git status [flags]
```

### Flags for ro git status

```text
--long   show long git status output
```

### Inherited Flags for ro git status

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git sync

Fetch and rebase current branch onto upstream

```bash
ro git sync [flags]
```

### Flags for ro git sync

```text
--autostash       auto stash during rebase (default true)
      --branch string   upstream branch (defaults to git.defaultBranch)
      --remote string   upstream remote (defaults to git.defaultRemote)
```

### Inherited Flags for ro git sync

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro run

Run local services

```bash
ro run
```

### Inherited Flags for ro run

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro run service

Run tournament management service locally

```bash
ro run service tournamentmgmt [flags]
```

### Flags for ro run service

```text
--env string   spring profile (default "dev")
      --port int     server port (default 8080)
```

### Inherited Flags for ro run service

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro scaffold

Generate boilerplate for RallyOn modules

```bash
ro scaffold
```

### Inherited Flags for ro scaffold

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro scaffold module

Create a Modulith slice skeleton (controller/service/domain/test)

```bash
ro scaffold module <name> [flags]
```

### Flags for ro scaffold module

```text
--base string      base path for Java sources (defaults to service module src/main/java)
      --dry-run          preview files without writing
      --package string   base Java package for the module
```

### Inherited Flags for ro scaffold module

```text
--json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro test

Run test suites

```bash
ro test
```

### Inherited Flags for ro test

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro version

Show CLI version information

```bash
ro version
```

### Inherited Flags for ro version

```text
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

