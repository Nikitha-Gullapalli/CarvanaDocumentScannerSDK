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
    
    // XCFramework configuration for distribution
    val xcframeworkName = "ComposeApp"
    
    task("buildXCFramework") {
        group = "build"
        description = "Build XCFramework for iOS distribution"
        
        dependsOn(
            "linkReleaseFrameworkIosArm64",
            "linkReleaseFrameworkIosSimulatorArm64"
        )
        
        doLast {
            val buildDir = layout.buildDirectory.asFile.get()
            val xcframeworkDir = File(buildDir, "XCFrameworks/release")
            xcframeworkDir.mkdirs()
            
            val xcframeworkPath = File(xcframeworkDir, "$xcframeworkName.xcframework")
            
            exec {
                commandLine(
                    "xcodebuild", "-create-xcframework",
                    "-framework", "$buildDir/bin/iosArm64/releaseFramework/$xcframeworkName.framework",
                    "-framework", "$buildDir/bin/iosSimulatorArm64/releaseFramework/$xcframeworkName.framework",
                    "-output", xcframeworkPath.absolutePath
                )
            }
            
            println("✅ XCFramework created at: ${xcframeworkPath.absolutePath}")
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

// Publishing configuration for GitHub Packages
group = "com.carvana"
version = "1.0.11"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-")
            credentials {
                username = "Nikitha-Gullapalli"
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("githubToken") as String? ?: ""
            }
        }
    }
}
