package contracts.ci

/**
 * GBL-013 US-1 — portability finding of the CI contract.
 *
 * The CI logic belongs to `ci/run.sh <job>` (a single, strict exit code), not to
 * the pipeline YAML. [CiPortabilityAudit] is pure: the workflow text is the
 * contract.
 */
sealed interface PortabilityFinding {
    val source: String
    val line: Int
    val evidence: String

    /** R3 — a multi-command `run: |` block invokes a gate without delegating to `ci/`. */
    data class LogicOutsideContract(
        override val source: String,
        override val line: Int,
        override val evidence: String,
    ) : PortabilityFinding

    /** R4 — a container image is not pinned by digest. */
    data class UnpinnedImage(
        override val source: String,
        override val line: Int,
        override val evidence: String,
    ) : PortabilityFinding

    /** R5 — a toolchain setup action duplicates what the runtime image provides. */
    data class RedundantToolchainSetup(
        override val source: String,
        override val line: Int,
        override val evidence: String,
    ) : PortabilityFinding
}

/**
 * GBL-013 US-1 — the three portability rules of the CI contract.
 *
 * Complements [CiShellAudit] (R1 `NoErrexit`, R2 `SwallowedGate`: the exit code
 * must be able to fail) with:
 *
 *  - **R3** [PortabilityFinding.LogicOutsideContract] — a `run: |` block with two
 *    commands or more that invokes a gate must delegate to `ci/` (the adapter
 *    carries no business logic).
 *  - **R4** [PortabilityFinding.UnpinnedImage] — every `image:` reference must be
 *    pinned by digest (`@sha256:<64 hex>`); a moving tag is not reproducible.
 *  - **R5** [PortabilityFinding.RedundantToolchainSetup] — `actions/setup-java`,
 *    `actions/setup-node` and `actions/setup-python` duplicate the runtime image;
 *    a migrated adapter must not install the toolchain again.
 *
 * The audit is scoped to the files the caller submits: a repository opts in when
 * it adopts the `ci/` contract (US-3 for the pilot), so an unmigrated workflow is
 * never reported by accident.
 */
object CiPortabilityAudit {
    private val IMAGE = Regex("""^[ \t]*image:[ \t]*["']?([^"'\s#]+)""", RegexOption.MULTILINE)
    private val DIGEST = Regex("""@sha256:[0-9a-fA-F]{64}$""")
    private val CI_DELEGATION = Regex("""^(\./)?ci/[A-Za-z0-9._/-]+$""")
    private val TOOLCHAIN_ACTION =
        Regex(
            """^[ \t]*(?:-[ \t]+)?uses:[ \t]*["']?(actions/setup-(?:java|node|python))@""",
            RegexOption.MULTILINE,
        )

    /** Every portability defect of [text] (a workflow or a composite action). */
    fun findings(
        text: String,
        source: String,
    ): List<PortabilityFinding> {
        val logic = logicOutsideContract(text, source)
        val images = unpinnedImages(text, source)
        val setup = redundantToolchainSetup(text, source)
        return logic + images + setup
    }

    private fun logicOutsideContract(
        text: String,
        source: String,
    ): List<PortabilityFinding.LogicOutsideContract> =
        CiShellAudit.blocks(text).mapNotNull { block ->
            val delegates = block.commands.any { CI_DELEGATION.matches(it.trim()) }
            val invokesGate = block.commands.any { CiShellAudit.isGate(it) }
            if (block.commands.size >= 2 && invokesGate && !delegates) {
                PortabilityFinding.LogicOutsideContract(source, block.line, block.commands.last())
            } else {
                null
            }
        }

    private fun unpinnedImages(
        text: String,
        source: String,
    ): List<PortabilityFinding.UnpinnedImage> =
        IMAGE.findAll(text).mapNotNull { match ->
            val reference = match.groupValues[1]
            if (DIGEST.containsMatchIn(reference)) {
                null
            } else {
                PortabilityFinding.UnpinnedImage(source, lineOf(text, match.range.first), reference)
            }
        }.toList()

    private fun redundantToolchainSetup(
        text: String,
        source: String,
    ): List<PortabilityFinding.RedundantToolchainSetup> =
        TOOLCHAIN_ACTION.findAll(text).map { match ->
            PortabilityFinding.RedundantToolchainSetup(
                source,
                lineOf(text, match.range.first),
                match.groupValues[1],
            )
        }.toList()

    private fun lineOf(
        text: String,
        offset: Int,
    ): Int = text.take(offset).count { it == '\n' } + 1
}
