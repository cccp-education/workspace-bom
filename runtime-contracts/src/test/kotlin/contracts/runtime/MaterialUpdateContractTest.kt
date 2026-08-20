package contracts.runtime

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MaterialUpdateContractTest {

    @Test
    fun `creates material update contract with all fields`() {
        val contract = MaterialUpdateContract(
            remoteUrl = "https://github.com/cccp-education/formation-fpa",
            currentVersion = "v1.0",
            latestVersion = "v1.1",
        )
        assertThat(contract.remoteUrl).isEqualTo("https://github.com/cccp-education/formation-fpa")
        assertThat(contract.currentVersion).isEqualTo("v1.0")
        assertThat(contract.latestVersion).isEqualTo("v1.1")
    }

    @Test
    fun `creates material update contract with null versions`() {
        val contract = MaterialUpdateContract(
            remoteUrl = "https://github.com/cccp-education/formation-fpa",
        )
        assertThat(contract.currentVersion).isNull()
        assertThat(contract.latestVersion).isNull()
    }

    @Test
    fun `remoteUrl must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            MaterialUpdateContract(remoteUrl = "")
        }
        assertThat(ex.message).contains("remoteUrl")
    }
}

class MaterialUpdateResultTest {

    @Test
    fun `up to date result`() {
        val result = MaterialUpdateResult.UpToDate
        assertThat(result).isInstanceOf(MaterialUpdateResult::class.java)
    }

    @Test
    fun `updated result with version`() {
        val result = MaterialUpdateResult.Updated(targetVersion = "v1.1")
        assertThat(result.targetVersion).isEqualTo("v1.1")
    }

    @Test
    fun `error result with message`() {
        val result = MaterialUpdateResult.Error(message = "network failure")
        assertThat(result.message).isEqualTo("network failure")
    }
}

class MaterialUpdateResolverTest {

    @Test
    fun `fetchLatest returns latest version via fake implementation`() {
        val resolver = FakeMaterialUpdateResolver(latestVersion = "v2.0")
        val latest = resolver.fetchLatest("https://github.com/test/repo")
        assertThat(latest).isEqualTo("v2.0")
    }

    @Test
    fun `fetchLatest returns null when no version found`() {
        val resolver = FakeMaterialUpdateResolver(latestVersion = null)
        val latest = resolver.fetchLatest("https://github.com/test/repo")
        assertThat(latest).isNull()
    }

    @Test
    fun `pull returns UpToDate when target equals current`() {
        val resolver = FakeMaterialUpdateResolver(latestVersion = "v1.0")
        val result = resolver.pull("https://github.com/test/repo", "v1.0")
        assertThat(result).isInstanceOf(MaterialUpdateResult.UpToDate::class.java)
    }

    @Test
    fun `pull returns Updated when target differs from current`() {
        val resolver = FakeMaterialUpdateResolver(latestVersion = "v1.0")
        val result = resolver.pull("https://github.com/test/repo", "v1.1")
        assertThat(result).isInstanceOf(MaterialUpdateResult.Updated::class.java)
        assertThat((result as MaterialUpdateResult.Updated).targetVersion).isEqualTo("v1.1")
    }

    @Test
    fun `pull returns Error on failure`() {
        val resolver = FakeMaterialUpdateResolver(latestVersion = null, shouldFail = true)
        val result = resolver.pull("https://github.com/test/repo", "v1.1")
        assertThat(result).isInstanceOf(MaterialUpdateResult.Error::class.java)
    }
}

private class FakeMaterialUpdateResolver(
    private val latestVersion: String?,
    private val shouldFail: Boolean = false,
) : MaterialUpdateResolver {
    override fun fetchLatest(remoteUrl: String): String? = latestVersion

    override fun pull(remoteUrl: String, targetVersion: String): MaterialUpdateResult {
        if (shouldFail) return MaterialUpdateResult.Error(message = "pull failed")
        if (latestVersion == targetVersion) return MaterialUpdateResult.UpToDate
        return MaterialUpdateResult.Updated(targetVersion = targetVersion)
    }
}