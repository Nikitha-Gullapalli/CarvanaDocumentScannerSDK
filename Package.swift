// swift-tools-version: 5.9
import PackageDescription

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
            checksum: "b3fe0d0f07121a037c5e1179a89cf590b0b82ff158d31f16f9f81c0e1d84ea9b"
        )
    ]
)