@ac_2
Feature: AgentTool — transverse agent tool contract

  Any agent tool implements AgentTool<TInput, TOutput> with:
  - a unique name
  - a readable description
  - an async execute() method
  - a validate() method returning Result

  Background:
    Given the GradleTaskTool

  Scenario: An AgentTool has a name and description
    Then the tool name is "gradle_task"
    And the tool description is not empty

  Scenario: An AgentTool validates correct input
    When validating Args with project ":training-plugin" and task "generateSPD"
    Then validation succeeds

  Scenario: An AgentTool rejects empty project
    When validating Args with project "" and task "generateSPD"
    Then validation fails
    And the error message contains "blank"

  Scenario: An AgentTool rejects empty task
    When validating Args with project ":training-plugin" and task ""
    Then validation fails
    And the error message contains "blank"

  Scenario: An AgentTool implements the AgentTool interface
    Then GradleTaskTool is an instance of AgentTool
