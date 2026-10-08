package contracts.plantuml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlantUmlSyntaxValidatorTest {

    @Test
    fun `is a functional interface usable as a lambda`() {
        val validator: PlantUmlSyntaxValidator = { code ->
            if (code.contains("@enduml")) SyntaxValidationResult.Valid
            else SyntaxValidationResult.Invalid("Missing @enduml", "")
        }
        assertThat(validator.validate("@startuml\n@enduml")).isEqualTo(SyntaxValidationResult.Valid)
        assertThat(validator.validate("@startuml")).isInstanceOf(SyntaxValidationResult.Invalid::class.java)
    }

    @Test
    fun `can be implemented by an anonymous object`() {
        val validator =
            object : PlantUmlSyntaxValidator {
                override fun validate(plantumlCode: String) = SyntaxValidationResult.Invalid("stub", "")
            }
        assertThat(validator.validate("@startuml")).isInstanceOf(SyntaxValidationResult.Invalid::class.java)
    }
}
