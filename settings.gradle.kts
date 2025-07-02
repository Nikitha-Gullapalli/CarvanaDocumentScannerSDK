rootProject.name = "CarvanaDocumentScannerSDK"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("com.google.mlkit")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("com.google.mlkit")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":openCVLibrary")
//project(":openCVLibrary").projectDir = File("relative/path/to/OpenCV-android-sdk/sdk/java")
