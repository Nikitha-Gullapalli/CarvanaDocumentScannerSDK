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
            checksum: "ae0d74f606a353c59438a322d1154b230b11831736414cafcf503d4f82f810ab"
        )
    ]
)