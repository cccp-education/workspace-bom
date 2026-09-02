package contracts.catalog.scenarios

import io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

/**
 * MEM-CAT-5 — Runner Cucumber dédié `@version-catalog`.
 *
 * Scénarios : catalog résolvable / accessor typé / hygiène pas-de-hardcode.
 * Exclut @wip et @integration (pattern BOM).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "contracts.catalog.scenarios")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:build/reports/cucumber-catalog.html, json:build/reports/cucumber-catalog.json"
)
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@version-catalog")
class VersionCatalogCucumberRunner