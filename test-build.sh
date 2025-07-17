#!/bin/bash

echo "Testing Gradle build..."
./gradlew :document-scanner-sdk:buildXCFramework

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "XCFramework should be at: document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework"
else
    echo "❌ Build failed!"
fi