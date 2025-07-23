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
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.13/ComposeApp.xcframework.zip",
            checksum: "bcf8b4c6bb43aa6330caef56fb3a277dc1235ef8ae0b1a638fcb5b236081f808"
        )
    ]
)