package contracts.plantuml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlantUmlClassifierTest {

    private val classifier = PlantUmlClassifier()

    @Test
    fun `borrowed vocabulary wins over translatable labels`() {
        val block =
            PlantUmlBlock(
                raw = """@startuml
rectangle "pipeline" as P
@enduml""",
                borrowedVocabulary = setOf("pipeline"),
            )
        assertThat(classifier.classify(block)).isEqualTo(PlantUmlStrategy.BORROW)
    }

    @Test
    fun `translatable labels classify as TRANSLATE`() {
        val block = PlantUmlBlock("@startuml\nclass \"Utilisateur\"\n@enduml")
        assertThat(classifier.classify(block)).isEqualTo(PlantUmlStrategy.TRANSLATE)
    }

    @Test
    fun `only semantic identity classifies as PRESERVE`() {
        val block = PlantUmlBlock("@startuml\nU --> Foo.Bar\n@enduml")
        assertThat(classifier.classify(block)).isEqualTo(PlantUmlStrategy.PRESERVE)
    }

    @Test
    fun `empty block classifies as PRESERVE`() {
        assertThat(classifier.classify(PlantUmlBlock(""))).isEqualTo(PlantUmlStrategy.PRESERVE)
    }
}
