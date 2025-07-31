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
            checksum: "a5ce47b8586de6463dc8e5aa080c2fe177dad6c00653847ca0622f07439d8d4b"
        )
    ]
)