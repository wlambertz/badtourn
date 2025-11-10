# RallyOn Developer CLI (ro)

## Build

- Windows PowerShell

```
cd tools/cli/ro
go mod tidy
go build -o ..\..\..\bin\ro.exe .
```

## Usage

- Show help

```
ro --help
```

- Config

```
ro config show --json
```

- Build / Test / Run

```
ro build [--fast|--ci]
ro test
ro run service tournamentmgmt --env dev --port 8080
```

- Docker

```
ro docker build --branch-tag --sha-tag --push
ro docker build --tag release --latest
ro docker compose up --profile dev
ro docker compose down --profile dev
ro docker compose logs api
```

- Git helpers

```shell
ro git status
ro git branch --verbose
ro git rebase --onto origin/main
ro git commit --type feat --summary "add feature"
```

- Deploy

```shell
# requires GITHUB_TOKEN in env
ro deploy --env dev --dry-run   # safe preview
ro deploy --env prod --yes --wait=false --input release=1.2.3
ro deploy --env dev --check-only --json  # CI preflight
```

- Deploy defaults live under `deploy.*` in `ro.yaml` (repo, workflow slug, ref mapping, safety gates). Override with `RO_DEPLOY_*` env vars when needed.
- Docs

```shell
ro docs generate --wiki  # writes docs/cli-reference.md (+ wiki/CLI.md when --wiki)
```
- Scaffold

```shell
ro scaffold module registration
ro scaffold module scheduling --package com.rallyon.tournament.scheduling
ro scaffold module scoring --dry-run
```
- Version

```shell
ro version
```

- Packaging / Release

```shell
cd tools/cli/ro
goreleaser build --snapshot --clean  # local multi-platform artifacts (dist/)
goreleaser release --clean --skip=publish  # dry-run release
```

CI publishes tagged releases via `.github/workflows/ro-release.yml` (tags `ro/v*`).

## Shell completion

Cobra provides the `ro completion` command. See `ro completion --help` and follow your shell instructions.
