# iOS Integration Guide

## Current Issue
The error you're seeing occurs because iOS Swift Package Manager is trying to access Azure DevOps Maven repository, which doesn't work.

## Immediate Solution

### Option 1: Local Framework Integration (Recommended for now)

1. **Remove incorrect SPM dependency**:
   - In Xcode: Project → Package Dependencies
   - Delete any Azure DevOps repository reference

2. **Add as local dependency**:
   ```swift
   // In your iOS project, add the ComposeApp framework manually
   // Copy from: ../document-scanner-sdk/build/xcode-frameworks/Debug/iphonesimulator17.5/ComposeApp.framework
   ```

3. **Build script** (add to your iOS project):
   ```bash
   cd ../document-scanner-sdk
   ./gradlew :document-scanner-sdk:embedAndSignAppleFrameworkForXcode
   ```

### Option 2: Manual Framework Distribution

1. **Build the framework**:
   ```bash
   cd /path/to/CarvanaDocumentScannerSDK-2
   ./gradlew :document-scanner-sdk:assembleComposeAppReleaseFrameworkIosArm64
   ./gradlew :document-scanner-sdk:assembleComposeAppReleaseFrameworkIosSimulatorArm64
   ./gradlew :document-scanner-sdk:assembleComposeAppReleaseFrameworkIosX64
   ```

2. **Manually add frameworks** to your Xcode project

### Option 3: Future - Proper Swift Package (Requires Setup)

For proper distribution, we need to:
1. Create XCFramework
2. Upload to GitHub releases or CDN
3. Update Package.swift with correct URL and checksum

## Quick Fix Right Now

**Just remove the Azure DevOps URL from your Swift Package Manager dependencies** and the error will disappear. Then use local framework integration like the demo app does.