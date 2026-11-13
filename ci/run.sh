#!/usr/bin/env bash
#
# GBL-013 US-3 — reference implementation of the CI contract for workspace-bom.
#
# The pipeline YAML is an adapter: it checks out, selects the runtime image, and
# calls this script. The exit code is the contract.
#
# Supported jobs for this repository:
#   fast    — static CI gate (matrix coverage, mirroring the CI job)
#   test    — the N0 contracts (publish the runtime siblings, then check all)
#   publish — publish the BOM/catalog to the local Maven repository
#
# `bdd`, `functional`, `e2e` and `coverage` are not local jobs here: they belong
# to the borough repositories and the cross-borough matrix.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

job="${1:-}"

contracts=(
  agent-contracts
  codebase-contracts
  i18n-contracts
  llm-pool-contracts
  ocr-contracts
  opencode-session-contracts
  pipeline-contracts
  plantuml-contracts
  runtime-contracts
  catalog-contracts
)

workspace_version() {
  sed -n 's/^workspace-bom = "\([^"]*\)"/\1/p' gradle/libs.versions.toml | head -1
}

publish_runtime_siblings() {
  local version
  version="$(workspace_version)"
  for module in i18n-contracts opencode-session-contracts; do
    ( cd "$module" && ./gradlew publishToMavenLocal --no-daemon --console=plain -Pversion="$version" )
  done
}

check_n0_contracts() {
  local registry_root="${RUNNER_TEMP:-/tmp}"
  printf '%s\n' "${contracts[@]}" |
    xargs -I{} -P "${N0_CONCURRENCY:-5}" bash -c '
      set -euo pipefail
      ( cd "$1" && ./gradlew check --no-daemon --console=plain \
          -Dorg.gradle.daemon.registry.base="$2/daemon-registry-$1" )
    ' _ {} "$registry_root"
}

case "$job" in
  fast)
    ./gradlew verifyCiMatrixCoverage --no-daemon --console=plain
    ;;
  test)
    publish_runtime_siblings
    check_n0_contracts
    ;;
  publish)
    ./gradlew publishToMavenLocal --no-daemon --console=plain
    ;;
  *)
    printf 'unsupported job for workspace-bom: %s\n' "${job:-<none>}" >&2
    printf 'supported: fast | test | publish\n' >&2
    exit 2
    ;;
esac
