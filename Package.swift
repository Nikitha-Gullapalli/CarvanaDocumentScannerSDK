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
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK/releases/download/v1.0.15/ComposeApp.xcframework.zip",
            checksum: "0e5a0fc371a46cf783ebfee6184f97d480f0c90c649a2322ab445b08c09a5181"
        )
    ]
)