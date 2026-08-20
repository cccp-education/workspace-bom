package contracts.runtime

interface MaterialUpdateResolver {
    fun fetchLatest(remoteUrl: String): String?
    fun pull(remoteUrl: String, targetVersion: String): MaterialUpdateResult
}