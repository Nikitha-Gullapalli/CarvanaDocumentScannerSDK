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
            checksum: "6fb441f58e85746672eba1dfcea1acd69fc131aca8a509c1a68c76c0f69f3e68"
        )
    ]
)