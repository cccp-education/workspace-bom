package contracts.plantuml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlantUmlBlockTest {

    @Test
    fun `extracts quoted labels and ignores technical identifiers`() {
        val block =
            PlantUmlBlock(
                """
                @startuml
                class "Utilisateur" as U
                U --> Foo.Bar
                @enduml
                """.trimIndent(),
            )
        assertThat(block.labels()).containsExactly("Utilisateur")
    }

    @Test
    fun `extracts unquoted directive values - title header footer caption`() {
        val block =
            PlantUmlBlock(
                """
                @startuml
                title Évolution Mensuelle
                header En-tête
                caption Légende
                footer Pied
                @enduml
                """.trimIndent(),
            )
        assertThat(block.labels())
            .containsExactly("Évolution Mensuelle", "En-tête", "Légende", "Pied")
    }

    @Test
    fun `labels are distinct and in first-seen order`() {
        val block =
            PlantUmlBlock(
                """
                @startuml
                class "Acteur"
                class "Acteur"
                @enduml
                """.trimIndent(),
            )
        assertThat(block.labels()).containsExactly("Acteur")
    }

    @Test
    fun `hasTranslatableLabels is false when only semantic identity`() {
        val block = PlantUmlBlock("@startuml\nU --> Foo.Bar\n@enduml")
        assertThat(block.hasTranslatableLabels()).isFalse()
    }

    @Test
    fun `hasBorrowedVocabulary detects a quoted borrowed term`() {
        val block =
            PlantUmlBlock(
                raw = """@startuml
rectangle "pipeline" as P
@enduml""",
                borrowedVocabulary = setOf("pipeline"),
            )
        assertThat(block.hasBorrowedVocabulary()).isTrue()
    }

    @Test
    fun `hasBorrowedVocabulary is false when the term is absent`() {
        val block =
            PlantUmlBlock(
                raw = "@startuml\nrectangle \"autre\"\n@enduml",
                borrowedVocabulary = setOf("pipeline"),
            )
        assertThat(block.hasBorrowedVocabulary()).isFalse()
    }

    @Test
    fun `empty raw has no labels and no borrowed vocabulary`() {
        val block = PlantUmlBlock("")
        assertThat(block.labels()).isEmpty()
        assertThat(block.hasTranslatableLabels()).isFalse()
        assertThat(block.hasBorrowedVocabulary()).isFalse()
    }
}
