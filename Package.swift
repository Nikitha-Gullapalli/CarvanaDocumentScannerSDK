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
    dependencies: [
        .package(url: "https://github.com/google/grpc-binary.git", from: "1.65.1"),
        .package(url: "https://github.com/plaid/plaid-link-ios.git", from: "5.6.1"),
        .package(url: "https://github.com/ReactiveX/RxSwift.git", from: "6.8.0"),
        .package(url: "https://github.com/evgenyneu/keychain-swift.git", from: "24.0.0"),
        .package(url: "https://github.com/persona-id/inquiry-ios-2.git", from: "2.15.2"),
        .package(url: "https://github.com/hyperoslo/Cache.git", from: "6.0.0"),
        .package(url: "https://github.com/airbnb/HorizonCalendar.git", from: "1.0.0")
    ],
    targets: [
        .binaryTarget(
            name: "ComposeApp",
            url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.9/ComposeApp.xcframework.zip",
            checksum: "9e280c8f68981c9e9876eb174a5077cef80847c3a57256d2deab9f6ce956380e"
        )
    ]
)