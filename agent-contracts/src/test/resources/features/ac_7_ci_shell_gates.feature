@ci-shell-gates
Feature: CI shell exit codes — a step must be able to fail

  A multi-command `run: |` block reports its LAST command's status unless
  errexit is enabled. MEM-CI-GATE (S-335) hid a red `./gradlew check` behind a
  trailing `echo "::endgroup::"` for two sessions. This suite pins the rules of
  the audit that prevents the next one, and checks the repository CI.

  Scenario: The historical false green is detected
    Given a CI step script
      """
      echo "::group::contracts check"
      ( cd "agent-contracts" && ./gradlew check --no-daemon )
      echo "::endgroup::"
      """
    Then the audit reports a missing errexit finding
    And the reported command is the trailing report

  Scenario: The same script is safe once errexit is enabled
    Given a CI step script
      """
      set -eo pipefail
      echo "::group::contracts check"
      ( cd "agent-contracts" && ./gradlew check --no-daemon )
      echo "::endgroup::"
      """
    Then no exit-code finding is reported

  Scenario: A gate swallowed by a fallback is reported
    Given a CI step line "./gradlew check || true"
    Then the audit reports a swallowed gate finding

  Scenario: A cleanup command may use a fallback
    Given a CI step line "docker rm -f ollama || true"
    Then no exit-code finding is reported

  Scenario: The workspace-bom CI files are clean
    When the workspace-bom CI files are audited
    Then no exit-code finding is reported
