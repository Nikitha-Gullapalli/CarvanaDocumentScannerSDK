import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)  // Changed from androidApplication
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
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
        // Remove applicationId, versionCode, versionName - not needed for libraries
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
    implementation("androidx.compose.animation:animation:1.5.4")
    implementation(libs.firebase.inappmessaging)
    debugImplementation(compose.uiTooling)
}

// Publishing configuration for Azure Artifacts
group = "com.carvana"
version = "1.0.0"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.carvana"
            artifactId = "document-scanner-sdk"
            version = "1.0.0"
            
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    
    repositories {
        maven {
            name = "AzureArtifacts"
            url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/_packaging/CarvanaSDK/maven/v1")
            credentials {
                username = project.findProperty("azure.username") as String? ?: System.getenv("AZURE_ARTIFACTS_USER")
                password = project.findProperty("azure.password") as String? ?: System.getenv("AZURE_ARTIFACTS_PAT")
            }
        }
    }
}
