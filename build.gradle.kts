import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:_")
}

group = "io.github.koalaplot"
version = "0.5.2"

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
                implementation("io.github.koalaplot:koalaplot-core:_")
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("io.github.koalaplot:koalaplot-core:_")
                implementation(compose.desktop.currentOs)
            }
        }

        named("androidMain") {
            dependencies {
                implementation("io.github.koalaplot:koalaplot-core:_")
                implementation(AndroidX.activity.compose)
            }
        }

        named("jsMain") {
            dependencies {
                implementation("io.github.koalaplot:koalaplot-core:_")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.koalaplot.sample.desktop.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
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

compose.experimental {
    web.application {}
}

// TODO: remove when https://youtrack.jetbrains.com/issue/KT-50778 fixed
project.tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java).configureEach {
    kotlinOptions.freeCompilerArgs += listOf("-Xir-dce-runtime-diagnostic=log")
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

// Fixes webpack-cli incompatibility by pinning the newest version.
// See https://stackoverflow.com/questions/72731436/kotlin-multiplatform-gradle-task-jsrun-gives-error-webpack-cli-typeerror-c
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
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
