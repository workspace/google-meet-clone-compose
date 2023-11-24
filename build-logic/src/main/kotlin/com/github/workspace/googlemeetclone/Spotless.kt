package com.github.workspace.googlemeetclone

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureSpotless() {
    with(pluginManager) {
        apply("com.diffplug.spotless")
    }

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude(
                "**/build/**/*.kt",                  // Build directory
            )
            ktlint("1.0.1")
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_max-line-length" to "disabled",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                    )
                )
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            licenseHeaderFile(
                rootProject.file("spotless/copyright.kts"),
                "(^(?![\\/ ]\\*).*$)"
            )
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}