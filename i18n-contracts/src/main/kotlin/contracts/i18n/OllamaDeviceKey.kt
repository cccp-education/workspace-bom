package contracts.i18n

data class OllamaDeviceKey(
    val keyName: String,
    val privateKey: String
) {
    init {
        require(keyName.isNotBlank()) { "keyName must not be blank" }
        require(privateKey.isNotBlank()) { "privateKey must not be blank" }
    }

    override fun toString(): String =
        "OllamaDeviceKey(keyName=$keyName, privateKey=ssh-ed25519***[${privateKey.length} chars])"
}
