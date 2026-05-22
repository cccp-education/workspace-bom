@ac_3
Feature: GradleTaskTool — destructive command blacklist and timeout

  The koog bridge to another borough's Gradle task must:
  - block destructive tasks (clean, delete, rm, --refresh-dependencies)
  - allow business tasks (generateSPD, generateQuiz, transformToPdf)
  - validate timeout (between 1 and 600s)
  - have a default timeout of 300s

  Background:
    Given the GradleTaskTool

  Scenario: Clean task is blocked by blacklist
    When validating Args with project ":training-plugin" and task "clean"
    Then validation fails
    And the error message contains "blacklisted"

  Scenario: CleanBuild task is blocked
    When validating Args with project ":training-plugin" and task "cleanBuild"
    Then validation fails
    And the error message contains "blacklisted"

  Scenario: Delete task is blocked
    When validating Args with project ":test" and task "deleteAll"
    Then validation fails
    And the error message contains "blacklisted"

  Scenario: Refresh-dependencies task is blocked
    When validating Args with project ":test" and task "--refresh-dependencies"
    Then validation fails
    And the error message contains "blacklisted"

  Scenario: GenerateSPD task is allowed
    When validating Args with project ":training-plugin" and task "generateSPD"
    Then validation succeeds

  Scenario: GenerateQuiz task is allowed
    When validating Args with project ":quizz-benchmark-plugin" and task "generateQuiz"
    Then validation succeeds

  Scenario: TransformToPdf task is allowed
    When validating Args with project ":codex-plugin" and task "transformToPdf"
    Then validation succeeds

  Scenario: CollectCompositeContext task is allowed
    When validating Args with project ":codebase-plugin" and task "collectCompositeContext"
    Then validation succeeds

  Scenario: Default timeout is 300s
    Given a default Args
    Then the timeout is 300s

  Scenario: Timeout 0 is rejected
    When validating Args with timeout 0
    Then validation fails
    And the error message contains "Timeout"

  Scenario: Timeout 601 is rejected
    When validating Args with timeout 601
    Then validation fails
    And the error message contains "Timeout"

  Scenario: Timeout 300 is accepted
    When validating Args with timeout 300
    Then validation succeeds
