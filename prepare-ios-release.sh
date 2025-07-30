#!/bin/bash

# Prepare iOS Release for version 1.0.7
echo "🚀 Preparing iOS Release for CarvanaDocumentScannerSDK v1.0.7"

# Step 1: Build XCFramework
echo "Step 1: Building XCFramework..."
./build-ios-framework.sh

# Step 2: Verify XCFramework was created
XCFRAMEWORK_PATH="document-scanner-sdk/build/XCFrameworks/release/CarvanaDocumentScannerSDK.xcframework"
ZIP_PATH="document-scanner-sdk/build/XCFrameworks/release/CarvanaDocumentScannerSDK.xcframework.zip"

if [ ! -d "$XCFRAMEWORK_PATH" ]; then
    echo "❌ Error: XCFramework not found at $XCFRAMEWORK_PATH"
    exit 1
fi

if [ ! -f "$ZIP_PATH" ]; then
    echo "❌ Error: Distribution zip not found at $ZIP_PATH"
    exit 1
fi

# Step 3: Calculate checksum
CHECKSUM=$(swift package compute-checksum "$ZIP_PATH")
echo "🔐 Calculated checksum: $CHECKSUM"

# Step 4: Update Package.swift with checksum
echo "📝 Updating Package.swift with checksum..."
sed -i '' "s/UPDATE_WITH_ACTUAL_CHECKSUM_FROM_BUILD_SCRIPT/$CHECKSUM/g" Package.swift

# Step 5: Prepare release notes
cat > RELEASE_NOTES.md << EOF
# CarvanaDocumentScannerSDK v1.0.7 

## iOS Distribution Ready 🎉

This release includes proper iOS distribution via Swift Package Manager.

### What's New
- ✅ iOS XCFramework distribution support
- ✅ Swift Package Manager integration
- ✅ Cross-platform support (Android + iOS)

### Installation

#### iOS (Swift Package Manager)
1. In Xcode: File → Add Package Dependencies
2. Enter repository URL: \`https://github.com/carvana/CarvanaDocumentScannerSDK\`
3. Select version 1.0.7

#### Android (Gradle)
\`\`\`kotlin
dependencies {
    implementation("com.carvana:document-scanner-sdk:1.0.7")
}
\`\`\`

### Files in this Release
- \`CarvanaDocumentScannerSDK.xcframework.zip\` - iOS XCFramework for Swift Package Manager
- Checksum: \`$CHECKSUM\`

### Usage
See README.md for detailed integration instructions.
EOF

echo ""
echo "✅ iOS Release preparation complete!"
echo ""
echo "📋 Next steps:"
echo "1. Commit all changes: git add . && git commit -m 'Prepare v1.0.7 iOS release'"
echo "2. Tag the release: git tag -a v1.0.7 -m 'Release version 1.0.7 with iOS support'"
echo "3. Push to repository: git push origin main --tags"
echo "4. Create GitHub release with the following files:"
echo "   - $ZIP_PATH"
echo "   - Include release notes from RELEASE_NOTES.md"
echo ""
echo "🔗 Release assets:"
echo "   📦 XCFramework: $XCFRAMEWORK_PATH" 
echo "   📦 Distribution zip: $ZIP_PATH"
echo "   🔐 Checksum: $CHECKSUM"