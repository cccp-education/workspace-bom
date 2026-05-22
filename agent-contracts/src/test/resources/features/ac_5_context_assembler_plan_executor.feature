@ac_5
Feature: ContextAssembler + PlanExecutor — interfaces N1→N2

  ContextAssembler assembles augmented context from intention + workspace root.
  PlanExecutor executes, evaluates, and replans via sealed class AgentState flow.

  Scenario Outline: ContextAssembler assembles context and returns ContextReady
    Given a ContextAssembler with workspace "<workspace>"
    When assembling context for intention "<intention>"
    Then the result is an AgentState.ContextReady
    And the intention matches "<intention>"
    And the composite context contains "<marker>"

    Examples:
      | workspace                           | intention               | marker                 |
      | /home/agent/formations/java         | build formation java    | java                   |
      | /home/agent/workspace               | generate slides         | slides                 |
      | /tmp/workspace                      | produce quizz evaluation| quizz                  |

  Scenario: PlanExecutor executes a Planned state into an Executed state
    Given a PlanExecutor
    And a Planned state with intention "build spring boot training"
    When executing the plan
    Then the result is an AgentState.Executed
    And the execution results contain at least 1 entry

  Scenario: PlanExecutor evaluates an Executed state into an Evaluated state
    Given a PlanExecutor
    And an Executed state with execution results
    When evaluating the execution
    Then the result is an AgentState.Evaluated
    And the evaluation score is between 0 and 1
    And the evaluation feedback is not empty

  Scenario: PlanExecutor replans an Evaluated state and increments counter
    Given a PlanExecutor
    And an Evaluated state with evaluation feedback
    When replanning
    Then the result is an AgentState.Evaluated
    And the replan count is at least 1

  Scenario: PlanExecutor stops replanning after max replans
    Given a PlanExecutor
    And an Evaluated state with replan count 3 and max replans 3
    When replanning
    Then the result is an AgentState.Evaluated
    And the replan count does not increase beyond max replans
