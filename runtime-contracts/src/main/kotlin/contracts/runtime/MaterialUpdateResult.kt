package contracts.runtime

sealed interface MaterialUpdateResult {
    object UpToDate : MaterialUpdateResult
    data class Updated(val targetVersion: String) : MaterialUpdateResult
    data class Error(val message: String) : MaterialUpdateResult
}