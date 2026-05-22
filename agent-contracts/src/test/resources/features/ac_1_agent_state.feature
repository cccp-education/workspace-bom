@ac_1
Feature: AgentState — standardized agent state contract

  The agent traverses a sequence of standardized states:
  Initial → ContextReady → Classified → Planned → Executed → Evaluated → Finalized
  Each state carries the current phase, intention, and accumulated data.

  Background:
    Given an intention "Formation Java 17"

  Scenario: Agent starts in Initial with phase BUILD_CONTEXT
    When an AgentState.Initial is created
    Then the state phase is BUILD_CONTEXT
    And the intention is "Formation Java 17"
    And the state has no error
    And the state is not terminal

  Scenario: Agent ContextReady transitions to phase CLASSIFY
    When an AgentState.ContextReady is created
    Then the state phase is CLASSIFY

  Scenario: Classified agent carries classification and model choice
    Given a Classified state with classification "complexe" and model "pro"
    Then the state phase is PLAN
    And the classification is "complexe"
    And the model choice is "pro"

  Scenario: Planned agent carries EPICs
    Given a Planned state with 2 EPICs
    Then the state phase is EXECUTE
    And the plan contains 2 EPICs

  Scenario: Executed agent carries execution results
    Given an Executed state with result "training: EXIT 0"
    Then the state phase is EVALUATE
    And the result for "training" is "EXIT 0"

  Scenario: Evaluated agent carries the score
    Given an Evaluated state with score 5.0
    Then the state phase is FINALIZE
    And the evaluation score is 5.0
    And the state is not terminal

  Scenario: Finalized agent without error is terminal and successful
    Given a Finalized state with output "ok"
    Then the state phase is FINALIZE
    And the state is terminal
    And the state is successful

  Scenario: Finalized agent with error is terminal but not successful
    Given a Finalized state with error "failure"
    Then the state is terminal
    And the state is not successful
