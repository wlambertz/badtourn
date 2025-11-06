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
ro docker build [--tag <t>] [--push]
ro docker compose up [--profile dev]
ro docker compose down
```
- Deploy
```
# requires GITHUB_TOKEN in env
ro deploy --env dev --dry-run   # safe preview
ro deploy --env prod --yes      # skip prompt for automation
```
- Deploy defaults live under `deploy.*` in `ro.yaml` (repo, workflow slug, ref mapping, safety gates). Override with `RO_DEPLOY_*` env vars when needed.
- Docs
```
ro docs generate  # writes docs/dev-cli.md
```

## Shell completion

Cobra provides the `ro completion` command. See `ro completion --help` and follow your shell instructions.


