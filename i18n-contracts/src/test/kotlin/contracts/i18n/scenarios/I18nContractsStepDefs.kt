package contracts.i18n.scenarios

import contracts.i18n.I18nConfig
import contracts.i18n.I18nValidationResult
import contracts.i18n.LanguageCatalog
import contracts.i18n.LocaleResolver
import contracts.i18n.MessageBundle
import contracts.i18n.SupportedLanguage
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class I18nContractsStepDefs {

    private var language: SupportedLanguage? = null
    private var validationResult: I18nValidationResult? = null
    private var resolvedLocale: Locale? = null
    private var fallbackLocale: Locale? = null
    private var bundle: MessageBundle = InMemoryMessageBundle(
        messages = mapOf("fr" to mapOf("greeting" to "Bonjour"))
    )
    private var message: String? = null
    private var missingMessage: String? = null

    @Given("an i18n contract environment")
    fun givenAnI18nContractEnvironment() {
        // no-op — the contract is stateless
    }

    @Then("the language catalog exposes {int} supported languages")
    fun thenCatalogExposes(expected: Int) {
        assertEquals(expected, LanguageCatalog.ALL.size)
        assertEquals(expected, LanguageCatalog.supportedCodes().size)
    }

    @Then("the language catalog contains the codes {string}, {string}, {string} and {string}")
    fun thenCatalogContains(first: String, second: String, third: String, fourth: String) {
        val codes = LanguageCatalog.supportedCodes()
        listOf(first, second, third, fourth).forEach { code ->
            assertTrue(code in codes, "catalog is missing the '$code' language")
        }
    }

    @Then("the language {string} is named {string} with native name {string}")
    fun thenLanguageIsNamed(code: String, name: String, nativeName: String) {
        val found = LanguageCatalog.findByCode(code)
            ?: error("catalog has no '$code' language")
        assertEquals(name, found.name)
        assertEquals(nativeName, found.nativeName)
    }

    @Then("the language {string} is unknown")
    fun thenLanguageIsUnknown(code: String) {
        assertNull(LanguageCatalog.findByCode(code))
    }

    @Then("the language {string} is right-to-left")
    fun thenLanguageIsRtl(code: String) {
        val found = LanguageCatalog.findByCode(code) ?: error("catalog has no '$code' language")
        assertTrue(found.rtl, "'$code' must be flagged right-to-left")
    }

    @Then("the language {string} is left-to-right")
    fun thenLanguageIsLtr(code: String) {
        val found = LanguageCatalog.findByCode(code) ?: error("catalog has no '$code' language")
        assertTrue(!found.rtl, "'$code' must not be flagged right-to-left")
    }

    @When("an I18nConfig is created with active {string}, supported {string} and fallback {string}")
    fun whenConfigCreated(active: String, supported: String, fallback: String) {
        validationResult = I18nConfig(
            activeLanguage = active,
            supportedLanguages = supported.split(",").map(String::trim),
            fallbackLanguage = fallback,
        ).validate()
    }

    @Then("the i18n config is valid")
    fun thenConfigIsValid() {
        assertEquals(I18nValidationResult.Valid, validationResult)
    }

    @Then("the i18n config is invalid and mentions {string}")
    fun thenConfigIsInvalidMentioning(fragment: String) {
        val result = validationResult
        assertTrue(result is I18nValidationResult.Invalid, "expected an Invalid result, got $result")
        assertTrue(
            result.message.contains(fragment),
            "invalid message '${result.message}' does not mention '$fragment'",
        )
    }

    @When("a LocaleResolver resolves active {string} among {string} with fallback {string}")
    fun whenLocaleResolved(active: String, supported: String, fallback: String) {
        val config = I18nConfig(
            activeLanguage = active,
            supportedLanguages = supported.split(",").map(String::trim),
            fallbackLanguage = fallback,
        )
        val resolver = ContractLocaleResolver()
        resolvedLocale = resolver.resolve(config)
        fallbackLocale = resolver.resolveWithFallback(config)
    }

    @Then("the resolved locale language is {string}")
    fun thenResolvedLocaleIs(expected: String) {
        assertEquals(expected, resolvedLocale?.language)
    }

    @Then("the resolved fallback locale language is {string}")
    fun thenFallbackLocaleIs(expected: String) {
        assertEquals(expected, fallbackLocale?.language)
    }

    @When("a message bundle is asked for key {string} in locale {string}")
    fun whenBundleAsked(key: String, locale: String) {
        val target = Locale.forLanguageTag(locale)
        message = bundle.get(key, target)
        missingMessage = bundle.get("missing", target)
    }

    @Then("the message is {string}")
    fun thenMessageIs(expected: String) {
        assertEquals(expected, message)
    }

    @Then("the message for key {string} is null")
    fun thenMessageIsNull(key: String) {
        assertNull(missingMessage, "key '$key' must resolve to null")
    }

    private class ContractLocaleResolver : LocaleResolver {
        override fun resolve(config: I18nConfig): Locale =
            Locale.forLanguageTag(config.activeLanguage)

        override fun resolveWithFallback(config: I18nConfig): Locale =
            if (config.activeLanguage in config.supportedLanguages) {
                resolve(config)
            } else {
                Locale.forLanguageTag(config.fallbackLanguage)
            }
    }

    private class InMemoryMessageBundle(
        private val messages: Map<String, Map<String, String>>,
    ) : MessageBundle {
        override fun get(key: String, locale: Locale): String? =
            messages[locale.language]?.get(key)

        override fun keys(): Set<String> =
            messages.values.flatMap { it.keys }.toSet()

        override fun locale(): Locale = Locale.forLanguageTag(messages.keys.first())
    }
}
