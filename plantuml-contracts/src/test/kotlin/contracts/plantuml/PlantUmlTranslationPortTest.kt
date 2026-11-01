package contracts.plantuml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlantUmlTranslationPortTest {
    @Test
    fun `outcome Translated carries the new block`() {
        val translated = PlantUmlBlock("@startuml\nclass \"User\"\n@enduml")
        val outcome = PlantUmlTranslationOutcome.Translated(translated)
        assertThat(outcome.block).isEqualTo(translated)
    }

    @Test
    fun `outcome Preserved carries a reason`() {
        val outcome = PlantUmlTranslationOutcome.Preserved("PRESERVE strategy")
        assertThat(outcome.reason).isEqualTo("PRESERVE strategy")
    }

    @Test
    fun `port can be implemented and invoked`() {
        // MEM-WARN-FREE (S-330) — the port is declared *as the interface* so the
        // test exercises the contract (an anonymous-object type would infer the
        // concrete `Translated` return type and make the type assertion tautological).
        val port: PlantUmlTranslationPort =
            object : PlantUmlTranslationPort {
                override fun translate(request: PlantUmlTranslationRequest) =
                    PlantUmlTranslationOutcome.Translated(
                        request.block.copy(raw = request.block.raw.replace("Utilisateur", "User")),
                    )
            }
        val request =
            PlantUmlTranslationRequest(
                block = PlantUmlBlock("@startuml\nclass \"Utilisateur\"\n@enduml"),
                sourceLanguage = "fr",
                targetLanguage = "en",
            )
        val outcome = port.translate(request)
        assertThat(outcome).isInstanceOf(PlantUmlTranslationOutcome.Translated::class.java)
        // MEM-WARN-FREE (S-330) — `assertThat(...).isInstanceOf(...)` does not smart-cast;
        // `require` states the contract explicitly instead of an unchecked `as`.
        val translated =
            requireNotNull(outcome as? PlantUmlTranslationOutcome.Translated) {
                "expected a Translated outcome, got ${outcome::class.simpleName}"
            }
        assertThat(translated.block.raw).contains("User")
    }

    @Test
    fun `request carries the three fields`() {
        val request =
            PlantUmlTranslationRequest(PlantUmlBlock("@startuml\n@enduml"), "fr", "en")
        assertThat(request.sourceLanguage).isEqualTo("fr")
        assertThat(request.targetLanguage).isEqualTo("en")
    }
}
