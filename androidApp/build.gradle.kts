import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    dependencies {
        implementation(project(":shared"))
        implementation(libs.androidx.activity)
        implementation(libs.androidx.activity.compose)
        implementation(libs.kotlinx.coroutines.android)
    }
}

android {
    namespace = "io.github.koalaplot.sample.android"
    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
    compileSdk = libs.versions.android.compileSdk
        .get()
        .toInt()

    defaultConfig {
        applicationId = "io.github.koalaplot.sample.android"
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()
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
