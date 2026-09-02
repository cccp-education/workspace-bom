@version-catalog
Feature: workspace-catalog — single source of truth for cross-borough plugin versions

  MEM-CAT-5 — the published version catalog (education.cccp:workspace-catalog)
  must carry the versions of the 12 education.cccp plugins so that consumers
  read them via typed accessors (ws.versions.*) instead of hardcoding them.
  Hygiene: no ghost entries (never-published plugins) and no hardcoded
  education.cccp coordinates in the platform script.

  Background:
    Given the published workspace catalog toml

  Scenario: Catalog is resolvable with all education cccp plugins
    Then the catalog contains version entries for the 12 resolvable plugins
    And every plugin version is non-blank and semver-like
    And no ghost plugin entry exists in the catalog

  Scenario: Typed accessor reads a plugin version from the catalog
    When the consumer reads version "planner-plugin" through the typed accessor
    Then the accessor returns a published semver "0.0.2"
    And the version is resolvable from mavenLocal or Central

  Scenario: Hygiene — platform script has no hardcoded education cccp coordinates
    When the platform script is inspected for hardcoded coordinates
    Then no api dependency hardcodes an education cccp group coordinate