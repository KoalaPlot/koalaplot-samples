import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
version = "0.6.3"

kotlin {
    jvmToolchain(17)
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    androidTarget()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(libs.koalaplot.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.collections.immutable)
            }
        }

        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        named("androidMain") {
            dependencies {
                implementation(libs.androidx.activity)
                implementation(libs.androidx.activity.compose)
            }
        }

        named("jsMain") {
            dependencies {
            }
        }
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
        targetSdk = 33
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "17"
    }

    detekt {
        source.setFrom("src")
        parallel = true
        config.setFrom("$rootDir/detekt.yml")
        buildUponDefaultConfig = true
    }
}

afterEvaluate { // https://discuss.kotlinlang.org/t/disabling-androidandroidtestrelease-source-set-in-gradle-kotlin-dsl-script/21448
    project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()
        ?.let { ext ->
            ext.sourceSets.removeAll { sourceSet ->
                setOf(
                    //"androidAndroidTestRelease",
                    "androidTestFixtures",
                    "androidTestFixturesDebug",
                    "androidTestFixturesRelease",
                ).contains(sourceSet.name)
            }
        }
}
