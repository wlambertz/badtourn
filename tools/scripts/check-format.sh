#!/usr/bin/env bash
set -euo pipefail

if [[ "${SKIP_FORMAT_CHECK:-}" == "1" ]]; then
  echo "Skipping docs formatting check because SKIP_FORMAT_CHECK=1"
  exit 0
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "npm is not installed; skipping docs formatting check" >&2
  exit 0
fi

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$REPO_ROOT"

echo "Running npm run lint (Prettier check)..."
npm run lint
