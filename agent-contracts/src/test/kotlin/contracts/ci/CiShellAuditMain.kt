package contracts.ci

import java.io.File
import kotlin.system.exitProcess

/**
 * MEM-CI-GATE-2.5 (S-341) — on-demand audit of the workspace CI.
 *
 * The guard [CiExitCodeGuardTest] protects this repository only: it runs in CI
 * with no sibling checkout. The audit is deliberately kept in **one**
 * implementation (no copy in `office`/`capsule`/`magic-stick`, no split-brain):
 * it is exposed as a report task, run at session closure over the whole
 * workspace:
 *
 * ```
 * ./gradlew ciShellGatesReport -PciRoot=/abs/path/to/workspace
 * ```
 *
 * Exit code `1` when a finding exists, so the command can gate a script.
 */
object CiShellAuditMain {
    private val SKIPPED_DIRECTORIES = setOf("build", ".gradle", "node_modules", "out", ".git")

    @JvmStatic
    fun main(args: Array<String>) {
        val root = File(args.firstOrNull() ?: "..").absoluteFile.normalize()
        check(root.isDirectory) { "ciShellGates: répertoire introuvable: $root" }
        val marker = "${File.separator}.github${File.separator}"
        val files =
            root
                .walkTopDown()
                .onEnter { it.name !in SKIPPED_DIRECTORIES }
                .filter { it.isFile && (it.extension == "yml" || it.extension == "yaml") }
                .filter { it.path.contains(marker) }
                .sortedBy { it.path }
                .toList()
        val findings =
            files.flatMap { file ->
                CiShellAudit.findings(file.readText(), file.relativeTo(root).invariantSeparatorsPath)
            }
        println("ciShellGates: ${files.size} fichier(s) CI audité(s) sous $root")
        findings.forEach { finding ->
            println("  ✗ ${finding::class.simpleName} ${finding.source}:${finding.line} — ${finding.command.take(110)}")
        }
        println("ciShellGates: ${findings.size} finding(s)")
        if (findings.isNotEmpty()) exitProcess(1)
    }
}
