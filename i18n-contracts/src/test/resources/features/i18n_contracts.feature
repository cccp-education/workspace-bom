@i18n @contracts
Feature: i18n contracts — cross-borough internationalization invariants

  The i18n-contracts N0 module is the shared vocabulary for every multilingual
  borough (bakery, slider, codex, document, readme, magic-stick). It carries no
  implementation: only the language catalog, the configuration invariant and the
  two ports (LocaleResolver, MessageBundle) that consumers implement. This
  feature pins the contract every consumer can rely on.

  Background:
    Given an i18n contract environment

  Scenario: The catalog exposes the 22 supported languages
    Then the language catalog exposes 22 supported languages
    And the language catalog contains the codes "en", "fr", "ar" and "fa"

  Scenario: A language is looked up by its ISO 639-1 code
    Then the language "fr" is named "French" with native name "Français"
    And the language "ja" is named "Japanese" with native name "日本語"
    And the language "xx" is unknown

  Scenario: Right-to-left languages are flagged
    Then the language "ar" is right-to-left
    And the language "ur" is right-to-left
    And the language "fa" is right-to-left
    And the language "en" is left-to-right
    And the language "fr" is left-to-right

  Scenario: A consistent configuration is valid
    When an I18nConfig is created with active "fr", supported "fr,en" and fallback "en"
    Then the i18n config is valid

  Scenario: An active language outside the supported list is rejected
    When an I18nConfig is created with active "de", supported "fr,en" and fallback "en"
    Then the i18n config is invalid and mentions "de"

  Scenario: A fallback language outside the supported list is rejected
    When an I18nConfig is created with active "fr", supported "fr,en" and fallback "it"
    Then the i18n config is invalid and mentions "it"

  Scenario: The locale resolver cascades to the active language
    When a LocaleResolver resolves active "fr" among "fr,en" with fallback "en"
    Then the resolved locale language is "fr"

  Scenario: The locale resolver falls back when the active language is unsupported
    When a LocaleResolver resolves active "de" among "fr,en" with fallback "en"
    Then the resolved fallback locale language is "en"

  Scenario: A message bundle resolves a key and returns null for a missing key
    When a message bundle is asked for key "greeting" in locale "fr"
    Then the message is "Bonjour"
    And the message for key "missing" is null
