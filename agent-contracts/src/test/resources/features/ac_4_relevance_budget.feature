@ac_4
Feature: RelevanceBudget — dynamic cosine similarity budget allocation

  RelevanceBudget replaces the fixed ChannelBudget (40/30/20/10) with a
  dynamic allocation based on cosine similarity between the intention and
  each channel's content.

  Background:
    Given an empty channel map

  Scenario: Empty channels return empty budget
    When computing budget for "any intention"
    Then the budget has 0 channels

  Scenario: Single channel receives all tokens
    Given channel "EAGER" with content "java formation content"
    When computing budget for "formation java"
    Then the budget has 1 channel
    And the channel "EAGER" has a positive score

  Scenario: Assemble produces formatted sections
    Given a budget with 2 channels
    When assembling output
    Then the assembled output contains "RAG"
    And the assembled output contains "EAGER"

  Scenario: Token allocation returns correct counts by name
    Given a budget with 2 channels
    Then the token allocation contains "RAG"
    And the token allocation contains "EAGER"
