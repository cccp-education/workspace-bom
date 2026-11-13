package contracts.ci

import java.io.File
import kotlin.system.exitProcess

/**
 * GBL-013 US-9 — final portability report over the workspace.
 *
 * On-demand (pattern S-341): it reads sibling repositories, so it never runs in
 * a standalone CI checkout. It is the acceptance gate of the EPIC:
 *
 * ```
 * ./gradlew verifyCiPortabilityReport -PciRoot=/abs/path/to/workspace
 * ```
 *
 * Public units must be **portable** (at least one `ci/run.sh` job, no R3/R4/R5
 * finding in the contract jobs) or explicitly declared as an exception
 * (`magic-stick`, specialised ISO image). Private units and the `office` hub are
 * reported as **blocked** (private runtime image / missing Docker Hub secrets):
 * they never make the public gate red.
 */
object CiPortabilityMain {
    private val SKIPPED_DIRECTORIES =
        setOf("build", ".gradle", ".kotlin", "node_modules", "out", ".git", ".ci-mirror")
    private val PUBLIC_EXCEPTIONS = setOf("magic-stick")

    private data class Unit(
        val name: String,
        val channel: String,
        val root: File,
        val workflows: List<File>,
        val contractScript: File?,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val root = File(args.firstOrNull() ?: "..").absoluteFile.normalize()
        check(root.isDirectory) { "verifyCiPortability: répertoire introuvable: $root" }

        val units =
            discover(root.resolve("foundry/public"), "public") +
                discover(root.resolve("foundry/private"), "private") +
                officeUnit(root)

        var failure = false
        var portable = 0
        var exceptions = 0
        var blocked = 0

        println("verifyCiPortability: ${units.size} unité(s) CI sous $root")
        units.forEach { unit ->
            val audits = unit.workflows.map { CiPortabilityJobsAudit.audit(it.readText(), it.name) }
            val migrated = audits.any { it.migrated }
            val findings = audits.flatMap { it.findings }
            val missingScript = migrated && unit.contractScript == null
            val violations = findings + if (missingScript) listOf(missingScriptFinding(unit)) else emptyList()

            when {
                violations.isNotEmpty() -> {
                    failure = true
                    println("  ✗ VIOLATION ${unit.name} (${unit.channel}) — ${violations.size} finding(s)")
                    violations.forEach { finding ->
                        println(
                            "      ${finding::class.simpleName} ${finding.source}:${finding.line} — " +
                                finding.evidence.take(110),
                        )
                    }
                }

                migrated -> {
                    portable++
                    println("  ✅ PORTABLE ${unit.name} (${unit.channel})")
                }

                unit.channel == "public" && unit.name in PUBLIC_EXCEPTIONS -> {
                    exceptions++
                    println("  ⚠️ EXCEPTION ${unit.name} — image spécialisée (ISO live-build/QEMU)")
                }

                unit.channel == "public" -> {
                    failure = true
                    println("  ✗ NOT_MIGRATED ${unit.name} (public) — aucun job ci/run.sh")
                }

                else -> {
                    blocked++
                    println("  ⛔ BLOCKED ${unit.name} (${unit.channel}) — image privée/secrets absents")
                }
            }
        }

        println(
            "verifyCiPortability: $portable portable, $exceptions exception(s), " +
                "$blocked bloquée(s), ${units.size} unité(s)",
        )
        if (failure) exitProcess(1)
    }

    private fun missingScriptFinding(unit: Unit): PortabilityFinding =
        PortabilityFinding.MissingRuntime(
            source = unit.root.relativeTo(unit.root.absoluteFile.parentFile.parentFile).path,
            line = 0,
            evidence = "ci/run.sh job without a ci/run.sh file",
        )

    private fun discover(
        directory: File,
        channel: String,
    ): List<Unit> {
        if (!directory.isDirectory) return emptyList()
        return directory
            .listFiles()
            .orEmpty()
            .filter { it.isDirectory && !it.name.startsWith(".") }
            .sortedBy { it.name }
            .mapNotNull { repo ->
                val workflows =
                    repo
                        .walkTopDown()
                        .onEnter { it.name !in SKIPPED_DIRECTORIES }
                        .filter { file ->
                            file.isFile &&
                                file.extension in setOf("yml", "yaml") &&
                                file.invariantSeparatorsPath().contains("/.github/workflows/")
                        }.sortedBy { it.path }
                        .toList()
                if (workflows.isEmpty()) return@mapNotNull null
                val contractScript =
                    repo
                        .walkTopDown()
                        .onEnter { it.name !in SKIPPED_DIRECTORIES }
                        .firstOrNull { file ->
                            file.isFile && file.invariantSeparatorsPath().endsWith("/ci/run.sh")
                        }
                Unit(repo.name, channel, repo, workflows, contractScript)
            }
    }

    private fun officeUnit(root: File): List<Unit> {
        val workflowsDir = root.resolve("office/.github/workflows")
        if (!workflowsDir.isDirectory) return emptyList()
        val workflows =
            workflowsDir
                .listFiles()
                .orEmpty()
                .filter { it.isFile && it.extension in setOf("yml", "yaml") }
                .sortedBy { it.name }
        if (workflows.isEmpty()) return emptyList()
        val contractScript =
            root.resolve("office/ci/run.sh").takeIf { it.isFile }
        return listOf(Unit("office", "office", root.resolve("office"), workflows, contractScript))
    }

    private fun File.invariantSeparatorsPath(): String = path.replace('\\', '/')
}
