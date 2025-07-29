// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CarvanaDocumentScannerSDK-",
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
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.15/ComposeApp.xcframework.zip",
            checksum: "4a8f54556feba9a368c1ee180f6ddcc4b4ba7620e0d2885096f6a229222c2889"
        )
    ]
)