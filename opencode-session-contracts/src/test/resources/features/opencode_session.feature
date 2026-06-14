@session @opencode-session-contracts
Feature: Opencode Session Contracts — thin-client protocol invariants

  The opencode-session-contracts N0 module defines the contract for communication
  between a thin client (opencode UI) and a Gradle engine (codebase VibecodingTask).
  Any N3 bridge implementing this contract must respect these invariants:
  prompt validation, response status lifecycle, context injection, tool call tracing,
  and token usage tracking.

  Scenario: SessionPrompt requires non-blank prompt
    When a SessionPrompt is created with prompt "Generate a README for the project"
    Then the prompt is "Generate a README for the project"
    And the sessionId is a valid UUID
    And maxActions defaults to 10

  Scenario: SessionPrompt rejects blank prompt
    When a SessionPrompt is created with prompt ""
    Then an IllegalArgumentException is thrown with message containing "blank"

  Scenario: SessionPrompt supports full context injection
    When a SessionPrompt is created with prompt "Fix compilation" and context containing eager rules "NE JAMAIS commit sans permission" and RAG chunks "chunk1,chunk2" and graph relations "codebase → runner" and backlog items "EPIC X-4,EPIC Y-3"
    Then the context eagerRules is "NE JAMAIS commit sans permission"
    And the context ragChunks contains 2 items
    And the context graphRelations is "codebase → runner"
    And the context backlogItems contains 2 items

  Scenario: SessionResponse lifecycle — COMPLETED status
    When a SessionResponse is created with output "BUILD SUCCESSFUL" and status COMPLETED
    Then the response output is "BUILD SUCCESSFUL"
    And the response status is COMPLETED
    And the response toolCalls is empty

  Scenario: SessionResponse lifecycle — ERROR status with tool calls
    When a SessionResponse is created with output "Compilation failed" and status ERROR and 1 tool call "exec_gradle" with result "BUILD FAILED"
    Then the response status is ERROR
    And the response toolCalls contains 1 record
    And the first tool call name is "exec_gradle"
    And the first tool call result is "BUILD FAILED"

  Scenario: SessionResponse lifecycle — IN_PROGRESS to COMPLETED transition
    When a SessionResponse is created with output "Step 3/5..." and status IN_PROGRESS
    Then the response status is IN_PROGRESS
    When a SessionResponse is created with output "All steps done" and status COMPLETED
    Then the response status is COMPLETED

  Scenario: TokenUsage tracks prompt and completion tokens
    When a TokenUsage is created with promptTokens 1500 and completionTokens 800
    Then the totalTokens is 2300
    And the cost is absent

  Scenario: TokenUsage supports optional cost tracking
    When a TokenUsage is created with promptTokens 1000 and completionTokens 500 and cost 0.015
    Then the totalTokens is 1500
    And the cost is 0.015
