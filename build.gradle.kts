import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint.gradle)
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

group = "io.github.koalaplot"
version = "0.11.0-dev3"

detekt {
    source.setFrom("src")
    parallel = true
    config.setFrom("$rootDir/detekt.yml")
    buildUponDefaultConfig = true
}

ktlint {
    version.set("1.8.0")
}

dependencies {
    ktlintRuleset(libs.ktlint.compose)
}

val libs2 = libs

subprojects {
    apply(
        plugin = libs2.plugins.ktlint.gradle
            .get()
            .pluginId,
    )
}
