package contracts.i18n

data class OllamaConfig(
    val deviceKeys: List<OllamaDeviceKey> = emptyList(),
    val model: String = "gemma4:31b-cloud",
    val portStart: Int = 11437,
    val portEnd: Int = 11465,
    val timeoutSeconds: Long = 300
) {
    init {
        require(model.isNotBlank()) { "model must not be blank" }
        require(portStart >= 11437) { "portStart must be >= 11437" }
        require(portEnd <= 11465) { "portEnd must be <= 11465" }
        require(portStart <= portEnd) { "portStart must be <= portEnd" }
        require(timeoutSeconds > 0) { "timeoutSeconds must be > 0" }
    }

    val isConfigured: Boolean get() = deviceKeys.isNotEmpty()

    val portRange: IntRange get() = portStart..portEnd

    fun baseUrls(): List<String> = portRange.map { port -> "http://localhost:$port" }
}
