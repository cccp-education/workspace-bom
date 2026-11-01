package contracts.ci.scenarios

import io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

/**
 * MEM-CI-GATE (suite S-338) — Runner Cucumber dédié `@ci-shell-gates`.
 *
 * Scénarios : faux vert historique S-335 détecté / errexit neutralise le rapport
 * terminal / gate masqué par `|| true` / CI du dépôt propre (pattern S-082).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "contracts.ci.scenarios")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:build/reports/cucumber-ci-shell-gates.html, json:build/reports/cucumber-ci-shell-gates.json",
)
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ci-shell-gates")
class CiShellGatesCucumberRunner
