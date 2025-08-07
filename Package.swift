// swift-tools-version: 5.9
import PackageDescription

// Updated: 2025-07-29 to fix CDN caching issue

let package = Package(
    name: "CarvanaDocumentScannerSDK",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CarvanaDocumentScannerSDK",
            targets: ["CarvanaDocumentScannerSDK"]
        )
    ],
    targets: [
        .binaryTarget(
            name: "CarvanaDocumentScannerSDK",
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK/releases/download/v1.0.17/CarvanaDocumentScannerSDK.xcframework.zip",
            checksum: "7b6fe8dd32910b02282ebe64cfd0d2d5929a7e3095bf1789ef1d856f3a6e4a3c"
        )
    ]
)