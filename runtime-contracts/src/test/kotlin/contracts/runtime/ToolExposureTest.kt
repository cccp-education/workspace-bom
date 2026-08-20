package contracts.runtime

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ToolExposureTest {

    @Test
    fun `creates tool exposure with default rule`() {
        val exposure = ToolExposure(publicBoroughsRoot = "foundry/public")
        assertThat(exposure.publicBoroughsRoot).isEqualTo("foundry/public")
        assertThat(exposure.rule).contains("foundry/public")
    }

    @Test
    fun `publicBoroughsRoot must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            ToolExposure(publicBoroughsRoot = "")
        }
        assertThat(ex.message).contains("publicBoroughsRoot")
    }
}