package contracts.ci

/**
 * GBL-013 US-9 — job-scoped portability audit.
 *
 * [CiPortabilityAudit] is textual: it cannot tell a migrated gate job from a
 * host shard that legitimately keeps `setup-java`. This companion splits the
 * workflow into jobs and applies R3/R4/R5 **only to the jobs that delegate to
 * `ci/run.sh`** (the portable contract). A job without `ci/run.sh` is a host job
 * and is deliberately ignored (shards, e2e, coverage, unit-ollama).
 *
 * Pure: the workflow text is the only input.
 */
object CiPortabilityJobsAudit {
    private val JOB_START = Regex("""^  ([A-Za-z0-9_-]+):[ \t]*$""")
    private val CONTAINER = Regex("""(?m)^[ \t]+container:[ \t]*(?:\{|$)""")
    private val DELEGATION =
        Regex("""(?m)^[ \t]*(?:-[ \t]+)?run:[ \t]*["']?(?:\./)?ci/[A-Za-z0-9._/-]+""")

    data class JobAudit(
        val name: String,
        val contract: Boolean,
        val findings: List<PortabilityFinding>,
    )

    data class UnitAudit(
        val source: String,
        val jobs: List<JobAudit>,
    ) {
        val contractJobs: List<JobAudit> get() = jobs.filter { it.contract }
        val migrated: Boolean get() = contractJobs.isNotEmpty()
        val findings: List<PortabilityFinding> get() = jobs.flatMap { it.findings }
        val portable: Boolean get() = migrated && findings.isEmpty()
    }

    /** Audits [workflowText] as a unit named [source]. */
    fun audit(
        workflowText: String,
        source: String,
    ): UnitAudit {
        val jobs =
            jobBlocks(workflowText).map { (name, block) ->
                val contract = DELEGATION.containsMatchIn(block)
                val findings =
                    if (!contract) {
                        emptyList()
                    } else {
                        buildList {
                            addAll(CiPortabilityAudit.findings(block, "$source#$name"))
                            if (!CONTAINER.containsMatchIn(block)) {
                                add(
                                    PortabilityFinding.MissingRuntime(
                                        source = "$source#$name",
                                        line = 0,
                                        evidence = "ci/run.sh job without container",
                                    ),
                                )
                            }
                        }
                    }
                JobAudit(name = name, contract = contract, findings = findings)
            }
        return UnitAudit(source = source, jobs = jobs)
    }

    /**
     * Splits the `jobs:` section into `(name, block)` pairs. Job keys are the
     * two-space-indented keys; the section ends at the next top-level key.
     */
    private fun jobBlocks(text: String): List<Pair<String, String>> {
        val lines = text.lines()
        val jobsIndex = lines.indexOfFirst { it == "jobs:" || it.startsWith("jobs:") }
        if (jobsIndex < 0) return emptyList()

        val jobLines = mutableListOf<String>()
        for (index in jobsIndex + 1 until lines.size) {
            val raw = lines[index]
            if (raw.isBlank()) continue
            val indent = raw.takeWhile { it == ' ' || it == '\t' }.length
            if (indent == 0) break
            jobLines += raw
        }

        val starts =
            jobLines.withIndex().filter { (_, line) ->
                JOB_START.matches(line)
            }
        return starts.mapIndexed { position, (start, line) ->
            val end = starts.getOrNull(position + 1)?.index ?: jobLines.size
            val name = JOB_START.matchEntire(line)!!.groupValues[1]
            name to jobLines.subList(start, end).joinToString("\n")
        }
    }
}
