# Carvana Document Scanner SDK

A cross-platform document scanning and processing SDK built with Kotlin Multiplatform for Android and iOS applications.

## Features

- 📷 Document scanning with camera
- 📁 File upload from device storage
- 🔍 OCR text extraction
- 📄 PDF generation
- ✅ File validation (max 10MB)

## Supported Formats

**Input**: JPEG, PNG, GIF, HEIC, PDF, TXT, DOC, DOCX  
**Output**: PDF (scanned), original format (uploaded)

---

## Android Setup

### 1. Add Repository

Add to your project-level `build.gradle.kts`:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK")
            credentials {
                username = "your-github-username"
                password = "your-github-token" // Personal Access Token with read:packages scope
            }
        }
    }
}
```

### 2. Add Dependency

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.carvana:document-scanner-sdk-android:1.0.16")
    implementation("androidx.activity:activity-ktx:1.8.0")
}
```

### 3. Add Permissions

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

### 4. Basic Implementation

```kotlin
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.carvana.carvana.StartCarvanaDocumentScannerSDKActivity

class MainActivity : AppCompatActivity() {
    
    private val documentScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                val documentPath = result.data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_DOCUMENT_PATH
                )
                val documentType = result.data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_DOCUMENT_TYPE
                )
                
                // Process your document
                processDocument(documentPath, documentType)
            }
            RESULT_CANCELED -> {
                val errorMessage = result.data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_ERROR_MESSAGE
                )
                // Handle error or cancellation
            }
        }
    }
    
    private fun openDocumentScanner() {
        val intent = Intent(this, StartCarvanaDocumentScannerSDKActivity::class.java)
        documentScannerLauncher.launch(intent)
    }
    
    private fun processDocument(documentPath: String?, documentType: String?) {
        documentPath?.let { path ->
            val file = File(path)
            when (documentType) {
                "scanned" -> {
                    // Handle scanned PDF document
                    Log.d("SDK", "Scanned PDF: $path")
                }
                "uploaded" -> {
                    // Handle uploaded document
                    Log.d("SDK", "Uploaded file: $path")
                }
            }
        }
    }
}
```

---

## iOS Setup

### 1. Add Dependency

#### Swift Package Manager
In Xcode:
1. Go to **File → Add Package Dependencies**
2. Enter repository URL: `https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK`
3. Select version **1.0.16** or "Up to Next Major Version"
4. Add **CarvanaDocumentScannerSDK** to your target

**Alternative - Package.swift:**
```swift
dependencies: [
    .package(url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK", from: "1.0.16")
]
```

### 2. Add Permissions

Add to `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>Camera access needed to scan documents</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Photo library access needed to upload documents</string>
```

### 3. Basic Implementation

```swift
import UIKit
import CarvanaDocumentScannerSDK

class ViewController: UIViewController {
    
    @IBAction func scanDocumentTapped(_ sender: UIButton) {
        let scannerVC = StartCarvanaDocumentScannerSDKViewControllerKt
            .StartCarvanaDocumentScannerSDKViewController()
        
        scannerVC.modalPresentationStyle = .fullScreen
        present(scannerVC, animated: true)
    }
    
    // Handle results via callback
    override func viewDidLoad() {
        super.viewDidLoad()
        // SDK will handle results internally and dismiss automatically
    }
}
```

### 4. Callbacks and Results

The SDK provides callbacks for handling scan results and errors:

```swift
// Set up delegate callbacks before presenting scanner
scannerVC.onDocumentScanned = { [weak self] documentPath, documentType in
    DispatchQueue.main.async {
        self?.handleDocumentResult(path: documentPath, type: documentType)
    }
}

scannerVC.onError = { [weak self] errorMessage in
    DispatchQueue.main.async {
        self?.handleError(message: errorMessage)
    }
}

private func handleDocumentResult(path: String, type: String) {
    switch type {
    case "scanned":
        print("Scanned PDF: \(path)")
    case "uploaded":
        print("Uploaded file: \(path)")
    default:
        break
    }
}

private func handleError(message: String) {
    print("SDK Error: \(message)")
}
```

---

## API Reference

### Android Result Data

| Key | Type | Description |
|-----|------|-------------|
| `EXTRA_DOCUMENT_PATH` | String | Path to processed document |
| `EXTRA_DOCUMENT_TYPE` | String | "scanned" or "uploaded" |
| `EXTRA_ERROR_MESSAGE` | String | Error message if failed |

### Result Codes

- `RESULT_OK`: Success
- `RESULT_CANCELED`: User cancelled or error

---

## Support

- **Issues**: [GitHub Issues](https://github.com/carvana/document-scanner-sdk/issues)
- **Email**: sdk-support@carvana.com

---

**© 2024 Carvana. All rights reserved.**

---

## 🔄 Release Process

### Android Release Steps

#### 1. Update Version
**File:** `document-scanner-sdk/build.gradle.kts`
```kotlin
group = "com.carvana"
version = "1.0.16"  // Increment version
```

#### 2. Publish to GitHub Packages
```bash
# Set GitHub token (required for publishing)
export GITHUB_TOKEN=your_github_token

# Publish Android SDK
./gradlew :document-scanner-sdk:publish
```

#### 3. Verify Publication
Check published packages at: `https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK/packages`

---

### iOS Release Steps

#### 1. Update Version
**File:** `document-scanner-sdk/build.gradle.kts`
```kotlin
version = "1.0.16"  // Keep consistent with Android
```

#### 2. Build XCFramework
```bash
# Clean previous builds
./gradlew clean

# Build XCFramework with updated naming
./gradlew buildXCFramework

# Create zip file for distribution
cd document-scanner-sdk/build/XCFrameworks/release/
zip -r CarvanaDocumentScannerSDK.xcframework.zip CarvanaDocumentScannerSDK.xcframework/

# Get checksum for Package.swift
shasum -a 256 CarvanaDocumentScannerSDK.xcframework.zip
```

#### 3. Create GitHub Release
```bash
# Create and push git tag
git tag v1.0.16
git push origin v1.0.16

# Create GitHub release (manual or via API)
# Upload CarvanaDocumentScannerSDK.xcframework.zip to the release
```

#### 4. Update Package.swift
**File:** `Package.swift`
```swift
.binaryTarget(
    name: "CarvanaDocumentScannerSDK",
    url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK/releases/download/v1.0.16/CarvanaDocumentScannerSDK.xcframework.zip",
    checksum: "NEW_CHECKSUM_FROM_STEP_2"
)
```

#### 5. Update Checksum
```bash
# Copy checksum from step 2 and update Package.swift
# Commit and push Package.swift changes
git add Package.swift
git commit -m "Update Package.swift for v1.0.16"
git push origin master
```

---

### Complete Release Checklist

- [ ] Update version in `build.gradle.kts`
- [ ] Build and publish Android SDK
- [ ] Build iOS XCFramework
- [ ] Create GitHub release with tag
- [ ] Upload XCFramework to release
- [ ] Calculate and update checksum in Package.swift
- [ ] Commit and push Package.swift
- [ ] Verify both Android and iOS can consume new version

---

### Consumer Updates

**Android:**
```kotlin
dependencies {
    implementation("com.carvana:document-scanner-sdk-android:1.0.16")
}
```

**iOS:**
Xcode will automatically detect new versions when using Swift Package Manager.

---

## Troubleshooting

### Android
- **GitHub Packages authentication**: Ensure your GitHub token has `read:packages` scope\n- **AAR metadata missing**: Use the `-android-release` variant: `implementation(\"com.carvana:document-scanner-sdk-android-release:1.0.16\")`
- **Build errors**: Clean project with `./gradlew clean`
- **MLKit issues**: Ensure Google Play Services are updated

### iOS
- **Package resolution**: Try **File → Packages → Reset Package Caches** in Xcode
- **Framework not found**: Verify the GitHub release exists with the XCFramework attached
- **Runtime crashes**: Check Info.plist permissions are added
