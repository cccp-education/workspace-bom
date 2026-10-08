package contracts.plantuml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SyntaxValidationResultTest {

    @Test
    fun `valid is a singleton object`() {
        assertThat(SyntaxValidationResult.Valid).isSameAs(SyntaxValidationResult.Valid)
    }

    @Test
    fun `invalid carries message and stack trace`() {
        val invalid = SyntaxValidationResult.Invalid("Missing @enduml", "trace")
        assertThat(invalid.errorMessage).isEqualTo("Missing @enduml")
        assertThat(invalid.stackTrace).isEqualTo("trace")
    }

    @Test
    fun `invalid equals by content`() {
        assertThat(SyntaxValidationResult.Invalid("e", "t"))
            .isEqualTo(SyntaxValidationResult.Invalid("e", "t"))
    }
}
