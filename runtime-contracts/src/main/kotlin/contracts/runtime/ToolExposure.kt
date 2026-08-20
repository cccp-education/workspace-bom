package contracts.runtime

// Tool exposure contract — documents which tools are available to the learner.
//
// The learner does NOT use vibecoding tools (read_file/write_file/exec_shell)
// directly. They use bureau-gradle which encapsulates the public boroughs.
//
// Guardrail: only foundry/public boroughs are invocable by the learner.
// The physical boundary foundry/public vs foundry/private is the signal.
// No per-role subset (APPRENANT/FORMATEUR/ADMIN was over-engineering).
//
// This data class formalizes the rule as a contract — no logic, pure data.
// The actual enforcement lives in the runtime implementation (pilot N4).
data class ToolExposure(
    val publicBoroughsRoot: String,
    val rule: String = "Only foundry/public/* boroughs are invocable by the learner",
) {
    init {
        require(publicBoroughsRoot.isNotBlank()) { "publicBoroughsRoot must be non-blank" }
    }
}