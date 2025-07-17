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
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.11/ComposeApp.xcframework.zip",
            checksum: "e6d703c5280118382144e8e52caeada95d96f842301005d032bc54cbff9f2f33"
        )
    ]
)