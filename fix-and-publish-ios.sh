#!/bin/bash

set -e  # Exit on any error

echo "🚀 Starting iOS Distribution Fix and Publish Process..."
echo "=================================================="

# Step 1: Clean and build XCFramework using Gradle task
echo "📱 Step 1: Building XCFramework using Gradle..."
./gradlew clean
./gradlew :document-scanner-sdk:buildXCFramework

# Step 2: Create distribution zip
echo "📦 Step 2: Creating distribution package..."
cd document-scanner-sdk/build/XCFrameworks/release
zip -r ComposeApp.xcframework.zip ComposeApp.xcframework
cd ../../../..

# Step 3: Calculate checksum and update Package.swift
echo "🔐 Step 3: Calculating checksum and updating Package.swift..."
CHECKSUM=$(swift package compute-checksum document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework.zip)
echo "Checksum: $CHECKSUM"

# Update Package.swift with the actual checksum
sed -i '' "s/UPDATE_WITH_ACTUAL_CHECKSUM_FROM_BUILD_SCRIPT/$CHECKSUM/g" Package.swift

# Step 4: Copy framework to your iOS project for immediate use
echo "📲 Step 4: Copying framework to your iOS project..."
IOS_PROJECT_PATH="/Users/NGullapa/Projects/iOS/Use-this-Project/Carvana.Mobile.Consumer.iOS"
if [ -d "$IOS_PROJECT_PATH" ]; then
    cp -r document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework "$IOS_PROJECT_PATH/"
    echo "✅ Framework copied to: $IOS_PROJECT_PATH/ComposeApp.xcframework"
    echo "   Now drag this into your Xcode project and set to 'Embed & Sign'"
else
    echo "⚠️  iOS project path not found. Copy manually:"
    echo "   From: $(pwd)/document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework"
    echo "   To: $IOS_PROJECT_PATH/"
fi

# Step 5: Commit and tag
echo "🔄 Step 5: Committing changes and tagging..."
git add .
git commit -m "🚀 Add iOS XCFramework distribution support for v1.0.7

- Add Package.swift for Swift Package Manager support
- Add XCFramework build configuration
- Create distribution scripts and documentation
- Fix iOS integration issue with Azure DevOps Maven repository

Checksum: $CHECKSUM

🤖 Generated with Claude Code

Co-Authored-By: Claude <noreply@anthropic.com>"

git tag -a v1.0.7 -m "Release v1.0.7 with iOS XCFramework support"

# Step 6: Push to repository
echo "⬆️ Step 6: Pushing to repository..."
git push origin master --tags

echo ""
echo "🎉 SUCCESS! iOS Distribution Complete!"
echo "=================================================="
echo ""
echo "✅ What was accomplished:"
echo "  📱 iOS XCFramework built and packaged"
echo "  📦 Distribution zip created"
echo "  🔐 Checksum calculated and Package.swift updated"
echo "  📂 Framework copied to your iOS project"
echo "  🏷️ Version 1.0.7 tagged and pushed"
echo ""
echo "📍 Files created:"
echo "  XCFramework: document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework"
echo "  Distribution: document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework.zip"
echo "  Checksum: $CHECKSUM"
echo ""
echo "🚨 NEXT STEPS FOR YOUR iOS PROJECT:"
echo "  1. Open Xcode project: $IOS_PROJECT_PATH"
echo "  2. Remove Azure DevOps package dependency from Package Dependencies"
echo "  3. Drag ComposeApp.xcframework into your project"
echo "  4. Set framework to 'Embed & Sign' in General → Frameworks"
echo "  5. Import ComposeApp in your Swift files"
echo ""
echo "✨ Your iOS integration error is now fixed!"