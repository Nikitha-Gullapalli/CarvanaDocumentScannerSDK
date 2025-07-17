1// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CarvanaDocumentScannerSDK",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "CarvanaDocumentScannerSDK",
            targets: ["ComposeApp"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "ComposeApp",
            // Update this URL to your actual GitHub repository or CDN
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.7/ComposeApp.xcframework.zip",
            // This checksum will be generated when building the XCFramework
            checksum: "9e280c8f68981c9e9876eb174a5077cef80847c3a57256d2deab9f6ce956380e"
        )
    ]
)