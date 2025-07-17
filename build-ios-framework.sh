#!/bin/bash

# Build iOS XCFramework for Distribution
echo "🔨 Building iOS XCFramework for CarvanaDocumentScannerSDK v1.0.7..."

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build XCFramework using Gradle task (this handles iOS frameworks + XCFramework creation)
echo "📱 Building XCFramework using Gradle..."
./gradlew :document-scanner-sdk:buildXCFramework

# Set paths
BUILD_DIR="document-scanner-sdk/build"
XCFRAMEWORK_DIR="$BUILD_DIR/XCFrameworks/release"

# Create zip for distribution
echo "📦 Creating distribution package..."
cd "$XCFRAMEWORK_DIR"
zip -r ComposeApp.xcframework.zip ComposeApp.xcframework
cd - > /dev/null

# Calculate checksum
CHECKSUM=$(swift package compute-checksum "$XCFRAMEWORK_DIR/ComposeApp.xcframework.zip")

echo ""
echo "✅ iOS XCFramework built successfully!"
echo "📍 Location: $XCFRAMEWORK_DIR/ComposeApp.xcframework"
echo "📦 Distribution package: $XCFRAMEWORK_DIR/ComposeApp.xcframework.zip"
echo "🔐 Checksum: $CHECKSUM"
echo ""
echo "Next steps:"
echo "1. Upload ComposeApp.xcframework.zip to GitHub releases"
echo "2. Update Package.swift with the checksum: $CHECKSUM"
echo "3. Commit and tag version 1.0.7"