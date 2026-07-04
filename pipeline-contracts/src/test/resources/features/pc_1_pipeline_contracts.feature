@pc_1 @pipeline-contracts
Feature: Pipeline Contracts — release notes generation contract invariants

  The pipeline-contracts N0 module defines the contract for generating release notes
  from git conventional commits. Any N2 borough implementing this contract must
  respect these invariants: commit structure, renderer format isolation,
  config boundaries, and generator composition.

  Background:
    Given a repository with conventional commits

  Scenario: ConventionalCommit data class carries type, scope, message, hash, date
    When a ConventionalCommit of type "feat" with scope "api" and message "add endpoint" is created
    Then the commit type is "feat"
    And the commit scope is "api"
    And the commit message is "add endpoint"
    And the commit hash is not empty
    And the commit date is not empty

  Scenario: ConventionalCommit supports null scope for unscoped commits
    When a ConventionalCommit of type "fix" with no scope and message "null pointer fix" is created
    Then the commit type is "fix"
    And the commit scope is absent
    And the commit message is "null pointer fix"

  Scenario: ReleaseNotesConfig provides default categories for all commit types
    When a default ReleaseNotesConfig is created
    Then the config has 7 categories
    And the category "feat" maps to "Nouveautés"
    And the category "fix" maps to "Corrections"
    And the default renderer type is "asciidoc"

  Scenario: ReleaseNotesConfig supports custom from/to tags
    When a ReleaseNotesConfig is created with fromTag "v1.0.0" and toTag "v1.1.0"
    Then the fromTag is "v1.0.0"
    And the toTag is "v1.1.0"

  Scenario: ReleaseNotesConfig supports custom renderer type
    When a ReleaseNotesConfig is created with rendererType "markdown"
    Then the renderer type is "markdown"

  Scenario: GitLogParser contract exposes parse, detectVersion, detectFromTag
    When a GitLogParser implementation returns 2 commits between "v1.0.0" and "v2.0.0"
    Then the parser returns 2 commits
    And the first commit type is "feat"

  Scenario: GitLogParser detects version from VERSION file
    When a GitLogParser implementation detects version "2.0.0" from project directory
    Then the detected version is "2.0.0"

  Scenario: ReleaseNotesRenderer contract requires format property
    When a ReleaseNotesRenderer implementation declares format "asciidoc"
    Then the renderer format is "asciidoc"

  Scenario: ReleaseNotesRenderer renders commits to a string
    When a ReleaseNotesRenderer renders 2 commits for version "1.0.0"
    Then the rendered string contains "Release 1.0.0"
    And the rendered string contains the first commit message

  Scenario: ReleaseNotesRenderer writes to file
    When a ReleaseNotesRenderer writes 1 commit to a file
    Then the file exists
    And the file contains the commit message

  Scenario: ReleaseNotesGenerator composes parser and renderer
    When a ReleaseNotesGenerator generates release notes for version "2.0.0"
    Then the output file exists
    And the output file contains "Release 2.0.0"
    And the output file contains all commit messages