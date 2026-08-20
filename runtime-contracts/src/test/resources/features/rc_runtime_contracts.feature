@runtime @contracts
Feature: Runtime Contracts — formation session runtime contract invariants

  The runtime-contracts N0 module defines the contract for bootstrapping and
  running an agent formation session. Any N4 borough implementing this contract
  must respect these invariants: session bootstrap, BYOK LLM config, learner
  profile memory, interaction protocol, formation runtime port, and material
  update resolution.

  Background:
    Given a formation session environment

  Scenario: ByokLlmConfig ollama local requires baseUrl
    When a ByokLlmConfig is created with provider "OLLAMA_LOCAL" and model "gpt-oss:120b-cloud" without baseUrl
    Then the config creation fails with "baseUrl"

  Scenario: ByokLlmConfig gemini requires apiKeyEnvVar
    When a ByokLlmConfig is created with provider "GEMINI" and model "gemini-pro" without apiKeyEnvVar
    Then the config creation fails with "apiKeyEnvVar"

  Scenario: SessionBootstrap requires non-blank learnerId
    When a SessionBootstrap is created with blank learnerId
    Then the bootstrap creation fails with "learnerId"

  Scenario: SessionBootstrap creates session with English default locale
    When a SessionBootstrap is created with valid learner "learner-001" and formation "formation-fpa"
    Then the bootstrap learnerId is "learner-001"
    And the bootstrap formationId is "formation-fpa"
    And the bootstrap locale is "en"

  Scenario: LearnerProfile defaults are zero and empty
    When a LearnerProfile is created with learnerId "learner-001" and formationId "formation-fpa"
    Then the profile progressionPct is 0.0
    And the profile comprehensionScore is 0.0
    And the profile completedModules is empty
    And the profile weakPoints is empty

  Scenario: LearnerProfile progressionPct must be 0 to 100
    When a LearnerProfile is created with progressionPct 150.0
    Then the profile creation fails with "progressionPct"

  Scenario: SessionMemoryContract save and load profile
    When a LearnerProfile is saved via SessionMemoryContract
    Then the loaded profile matches the saved profile

  Scenario: SessionMemoryContract load returns null when not found
    When a SessionMemoryContract loads a non-existent profile
    Then the loaded profile is null

  Scenario: FormationRuntimePort start execute end lifecycle
    When a FormationRuntimePort starts a session for learner "learner-001"
    And executes a turn with prompt "explain module 1"
    And ends the session
    Then the session summary has 1 turn
    And the session summary learnerId is "learner-001"

  Scenario: MaterialUpdateResolver pull returns UpToDate when target equals current
    When a MaterialUpdateResolver pulls version "v1.0" and latest is "v1.0"
    Then the result is UpToDate

  Scenario: MaterialUpdateResolver pull returns Updated when target differs
    When a MaterialUpdateResolver pulls version "v1.1" and latest is "v1.0"
    Then the result is Updated with version "v1.1"