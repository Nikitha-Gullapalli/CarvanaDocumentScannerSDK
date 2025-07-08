import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // REMOVE FOR PROD: Change androidApplication to androidLibrary
    // For testing: libs.plugins.androidApplication
    // For production: libs.plugins.androidLibrary
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release", "debug")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.constraintlayout)

            //MLKit dependencies
            implementation(libs.text.recognition.v1600)
            implementation(libs.play.services.mlkit.document.scanner)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
        }
    }
}

android {
    namespace = "com.carvana.carvana"
    compileSdkVersion(libs.versions.android.compileSdk.get().toInt())

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // REMOVE FOR PROD: Remove the following 3 lines (applicationId, versionCode, versionName)
//        applicationId = "com.carvana.carvana"
//        versionCode = 1
//        versionName = "1.0"
        // REMOVE FOR PROD: End of lines to remove
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.play.services.mlkit.text.recognition.common)
    implementation(libs.play.services.mlkit.text.recognition)
    implementation(libs.androidx.animation)
    implementation(libs.firebase.inappmessaging)
    debugImplementation(compose.uiTooling)
}

// Publishing configuration for Azure Artifacts
group = "com.carvana"
version = "1.0.2"

publishing {
    repositories {
        maven {
            name = "AzureArtifacts"
            url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/CarvanaDocumentScannerSDK/_packaging/CarvanaDocumentScannerSDK/maven/v1")
            credentials {
                username = ""
                password = System.getenv("AZURE_DEVOPS_TOKEN") ?: project.findProperty("azureDevOpsToken") as String? ?: ""
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
