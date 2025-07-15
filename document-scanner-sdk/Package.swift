// swift-tools-version:5.5
import PackageDescription

let package = Package(
    name: "CarvanaDocumentScannerSDK",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "CarvanaDocumentScannerSDK",
            targets: ["ComposeApp"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "ComposeApp",
            path: "build/bin/iosArm64/releaseFramework/ComposeApp.framework"
        ),
    ]
)