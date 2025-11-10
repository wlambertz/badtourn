# RallyOn Developer CLI (`ro`)

## Overview
- A single Go binary that wraps everyday build, test, run, docker, deploy, git, and docs flows.
- Lives in `tools/cli/ro`; ship platform-specific binaries via `go build`.
- Reads defaults from `ro.yaml`, overridable via `RO_*` environment variables or CLI flags.

## Installation
```bash
cd tools/cli/ro
go mod tidy               # once per clone
go build -o ../../bin/ro # linux/mac
# or: go build -o ../../bin/ro.exe # windows
```
Add `bin/` to your `PATH` or call the binary directly (e.g., `./bin/ro --help`).

## Config (`ro.yaml`)
- `project.*`, `paths.*`: repo layout.
- `build.*`: Maven wrapper location and default goals.
- `docker.*`: workflow reference + image repo (used by `ro docker`).
  - `registry` (e.g., `ghcr.io`), `imageRepo` (owner/name), `context`, `dockerfile`.
  - `composeFile`: default file for `docker compose` commands.
- `deploy.*`:
  - `repo`, `workflow`: GitHub owner/repo + workflow file name.
  - `defaultRef`: branch used when no env-specific mapping exists.
  - `envRefs`: map env → required branch (e.g., `prod: main`).
  - `requireClean`, `requireProtected`, `requireGreen`: safety gates before deployment.
  - `pollInterval`, `pollTimeout`: workflow polling cadence.
  - `inputs`: default workflow inputs (merged with CLI `--input key=value` overrides).
  - `defaultWait`: controls whether `ro deploy` waits for workflow completion unless `--wait` is provided.
- `output.*`: default verbosity / JSON logging.
- `docs.*`: `output`, `wikiOutput`, `publishToWiki` toggle for `ro docs generate`.
- Override any value via `RO_<SECTION>_<FIELD>=...` (e.g., `RO_DEPLOY_REQUIREPROTECTED=false`).

## Core Commands
```bash
ro build [--fast|--ci]
ro test
ro run service tournamentmgmt [--env dev --port 8080]
ro docker build --branch-tag --sha-tag --push
ro docker build --tag release --latest
ro docker compose up|down --profile dev
ro docker compose logs api
ro deploy --env dev --dry-run
ro deploy --env prod --yes --wait=false --input version=1.2.3
ro deploy --env dev --check-only --json
ro version
ro scaffold module registration
ro git status|branch|rebase|commit
```
Use `--verbose` for more logging, `--json` for machine-friendly output. `--dry-run` is supported by destructive commands, and `--yes` skips confirmation prompts.

### Git Helpers
- `ro git status` – short status by default; `--long` for full output.
- `ro git branch` – shows branch/ahead-behind; add `--verbose` for `git branch -vv`.
- `ro git rebase --onto origin/main` – runs `git pull --rebase --autostash`.
- `ro git commit` – guides a Conventional Commit (prompts for type/scope/summary, supports `--breaking`, `--wip`, `--all` to stage tracked files).
  - Preview shown before committing; `--yes` bypasses the confirmation prompt.
  - Use `--body`/`--breaking-notes` to supply additional paragraphs.

## Deploy Workflow
1. Set `GITHUB_TOKEN` (PAT with `repo` + `workflow` scopes) in your shell or secret store.
2. Ensure your working tree is clean and on the branch required for the chosen environment.
3. Run:
   ```bash
   ro deploy --env dev --dry-run   # preview
   ro deploy --env prod --yes --wait=false --input release=1.2.3
   ro deploy --env dev --check-only --json  # CI preflight without dispatching
   ```
4. The CLI enforces:
   - Clean git status unless `--yes`.
   - Branch matches `deploy.envRefs[env]` (can be overridden with `--yes`).
   - Branch protection check (`deploy.requireProtected`) via GitHub’s REST API. Disable via config if you use only modern rulesets.
   - Latest commit status is green (`deploy.requireGreen`).
5. Add workflow inputs with `--input key=value`. Defaults come from `deploy.inputs`, plus `env`/`notes`. Later flags override earlier values.
6. Use `--wait=false` (or set `deploy.defaultWait: false`) to dispatch asynchronously. Combine with `--json` for machine-readable logs, which emit `deploy-plan` and `deploy-result` events.
7. `--check-only` runs every preflight without triggering the workflow (ideal for CI gating).

## Troubleshooting
- `GITHUB_TOKEN env var is required`: export the token before running deploy (`export GITHUB_TOKEN=...`).
- `branch protection check forbidden`: ensure the token has `repo` admin scope or set `deploy.requireProtected=false`.
- `branch does not have protection enabled`: add a legacy branch protection rule or disable the check if you rely solely on rulesets.
- `workspace has uncommitted changes`: commit/stash or rerun with `--yes`.

## Future Enhancements
- Git helpers (`ro git ...`), docker tagging parity with CI, docs automation, and scaffolding commands are on deck per the implementation plan.
### Docker Workflow
- `ro docker build`
  - Tags mirror CI defaults: `--branch-tag` and `--sha-tag` (on by default) produce tags like `ghcr.io/org/app:main` and `ghcr.io/org/app:sha-1a2b3c4`.
  - Add manual tags with `--tag <value>`; pass `--latest` to always stamp `:latest`.
  - Use `--push` to push every generated tag after a successful build.
- `ro docker compose`
  - `--profile <name>` can be repeated to match your compose profiles.
  - `--file` overrides the compose file (defaults to `docker.composeFile` in `ro.yaml`).
  - Subcommands: `up`, `down`, `logs` (follows output, optionally for a specific service).

## CLI Reference Generation
- `ro docs generate` emits a full command reference at `docs/cli-reference.md`.
- Pass `--wiki` (or set `docs.publishToWiki: true`) to mirror the output into `wiki/CLI.md`; remember to commit the wiki submodule separately.
- Add `--output <path>` or `--wiki-output <path>` to override the targets when needed.

## Packaging & Releases
- Local snapshot build:
  ```bash
  cd tools/cli/ro
  goreleaser build --snapshot --clean
  ls dist/   # contains tar/zip per platform
  ```
- Dry-run release (no publish):
  ```bash
  goreleaser release --clean --skip=publish
  ```
- CI workflow `.github/workflows/ro-release.yml` runs on tags `ro/v*` and publishes multi-platform artifacts + checksums.
- `ro version` prints `Version (Commit) Date`, populated via GoReleaser `ldflags`.
- Scaffold modules (Modulith slice skeletons):
  ```bash
  ro scaffold module registration
  ro scaffold module payments --package com.rallyon.tournament.payments
  ro scaffold module scoring --dry-run
  ```
