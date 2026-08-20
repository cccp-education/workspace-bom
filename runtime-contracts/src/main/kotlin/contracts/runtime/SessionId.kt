package contracts.runtime

data class SessionId(val value: String) {
    init {
        require(value.isNotBlank()) { "value must be non-blank" }
    }
}