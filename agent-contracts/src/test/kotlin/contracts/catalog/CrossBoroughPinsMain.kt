package contracts.catalog

import java.io.File
import kotlin.system.exitProcess

/**
 * MEM-CI-CONSISTENCY (S-226) — on-demand cross-borough pin report.
 *
 * One implementation ([CrossBoroughPins]), unit-tested, executed at session
 * closure over the sibling builds:
 *
 * ```
 * ./gradlew -p agent-contracts verifyCrossBoroughPinsReport \
 *     -PpinsRoot=/abs/path/to/foundry/public
 * ```
 *
 * Exit code `1` when a violation exists, so the command can gate a script.
 */
object CrossBoroughPinsMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val root = File(args.getOrElse(0) { "../.." }).absoluteFile.normalize()
        val catalog = File(args.getOrElse(1) { "../gradle/libs.versions.toml" }).absoluteFile.normalize()
        check(root.isDirectory) { "verifyCrossBoroughPins: répertoire introuvable: $root" }
        check(catalog.isFile) { "verifyCrossBoroughPins: catalogue introuvable: $catalog" }

        val result = CrossBoroughPins.scan(root, catalog)
        if (!result.ok) {
            System.err.println(
                "Cross-borough pin consistency failed (MEM-CI-CONSISTENCY US-3/US-5):\n" +
                    result.violations.joinToString("\n") { "  • $it" },
            )
            exitProcess(1)
        }
        println(
            "verifyCrossBoroughPins OK — ${result.scannedCatalogs} sibling version catalog(s) and " +
                "${result.scannedBuildScripts} build script(s) within catalogue ${result.publishedBom} " +
                "(${result.skippedOwn} self-produced pin(s) skipped)",
        )
    }
}
