// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CarvanaDocumentScannerSDK",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CarvanaDocumentScannerSDK",
            targets: ["ComposeApp"]
        )
    ],
    targets: [
        .binaryTarget(
            name: "ComposeApp",
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.10/ComposeApp.xcframework.zip",
            checksum: "34c9418f8b86ec5b8cd4e69d9cc59227a3520c4111cafd7019bfb41a8cab8045"
        )
    ]
)