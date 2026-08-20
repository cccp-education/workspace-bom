package contracts.runtime

data class MaterialUpdateContract(
    val remoteUrl: String,
    val currentVersion: String? = null,
    val latestVersion: String? = null,
) {
    init {
        require(remoteUrl.isNotBlank()) { "remoteUrl must be non-blank" }
    }
}