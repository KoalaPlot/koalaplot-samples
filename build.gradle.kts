import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.detekt)
    alias(libs.plugins.androidApplication)
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

group = "io.github.koalaplot"
version = "0.8.0"

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }

    jvmToolchain(17)
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    androidTarget()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "koalaplot_samples"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.animation)
            implementation(libs.koalaplot.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
        }

        jsMain.dependencies { }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.koalaplot.sample.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "KoalaPlot"
            packageVersion = "1.0.0"

            windows {
                menu =
                    true // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "251c985b-942c-4f30-ba24-96aa7f9309d1"
            }

            macOS { // Use -Pcompose.desktop.mac.sign=true to sign and notarize.
                bundleID = "io.github.koalaplot.sample.desktop.MainKt"
            }
        }
    }
}

android {
    namespace = "io.github.koalaplot.sample.android"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.koalaplot.sample.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

detekt {
    source.setFrom("src")
    parallel = true
    config.setFrom("$rootDir/detekt.yml")
    buildUponDefaultConfig = true
}
