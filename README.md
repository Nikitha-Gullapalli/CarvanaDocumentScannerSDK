# Carvana Document Scanner SDK

A cross-platform document scanning and processing SDK built with Kotlin Multiplatform for Android and iOS applications.

## Publishing Complete
You can now see your published SDK at:
https://dev.azure.com/NikithaGullapalli/CarvanaDocumentScannerSDK/_artifacts/feed/CarvanaDocumentScannerSDK

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
            url = uri("https://maven.pkg.github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-")
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
    implementation("com.carvana:document-scanner-sdk-android-release:1.0.14")
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
2. Enter repository URL: `https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-`
3. Select version **1.0.14** or "Up to Next Major Version"
4. Add **CarvanaDocumentScannerSDK** to your target

**Alternative - Package.swift:**
```swift
dependencies: [
    .package(url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-", from: "1.0.14")
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

## 🔄 Publishing New Versions

### 1. Update Version Numbers

**Android (document-scanner-sdk/build.gradle.kts):**
```kotlin
group = "com.carvana"
version = "1.0.14"  // Increment version
```

**iOS (Package.swift):**
```swift
// Will be updated automatically to point to new release
url: "https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/download/v1.0.14/ComposeApp.xcframework.zip"
```

### 2. Build and Publish

**Android SDK:**
```bash
# Ensure GitHub token is set in gradle.properties
echo "githubToken=your_github_token" >> gradle.properties

# Publish to GitHub Packages
./gradlew :document-scanner-sdk:publish
```

**iOS SDK:**
```bash
# Build XCFramework
./gradlew :document-scanner-sdk:buildXCFramework

# Create GitHub release and upload XCFramework
# (Manual step - create release on GitHub and upload the .zip file)
```

### 3. Create GitHub Release and Upload XCFramework

#### Option A: Command Line (Recommended)

```bash
# Create git tag
git tag v1.0.14
git push origin v1.0.14

# Create GitHub release
curl -X POST \
  -H "Authorization: token YOUR_GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases \
  -d '{
    "tag_name": "v1.0.14",
    "name": "Release v1.0.14",
    "body": "iOS and Android SDK release v1.0.14",
    "draft": false,
    "prerelease": false
  }'

# Upload XCFramework to release (get release_id from previous response)
curl -X POST \
  -H "Authorization: token YOUR_GITHUB_TOKEN" \
  -H "Content-Type: application/zip" \
  --data-binary @"document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework.zip" \
  "https://uploads.github.com/repos/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases/RELEASE_ID/assets?name=ComposeApp.xcframework.zip"
```

#### Option B: Manual Steps

1. **Create Release on GitHub:**
   - Go to: https://github.com/Nikitha-Gullapalli/CarvanaDocumentScannerSDK-/releases
   - Click "Create a new release"
   - Tag version: `v1.0.14`
   - Release title: `Release v1.0.14`
   - Description: `iOS and Android SDK release v1.0.14`
   - Click "Publish release"

2. **Upload XCFramework:**
   - Edit the created release
   - Drag and drop the file: `document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework.zip`
   - Click "Update release"

### 4. Update Package.swift

```bash
# Get checksum of new XCFramework
shasum -a 256 document-scanner-sdk/build/XCFrameworks/release/ComposeApp.xcframework.zip

# Update Package.swift with new version and checksum
```

### 4. Consumer Project Updates

**Android:**
```kotlin
dependencies {
    implementation("com.carvana:document-scanner-sdk-android-release:1.0.14")  
}
```

**iOS:**
Xcode will automatically detect new versions when using Swift Package Manager.

---

## Troubleshooting

### Android
- **GitHub Packages authentication**: Ensure your GitHub token has `read:packages` scope\n- **AAR metadata missing**: Use the `-android-release` variant: `implementation(\"com.carvana:document-scanner-sdk-android-release:1.0.14\")`
- **Build errors**: Clean project with `./gradlew clean`
- **MLKit issues**: Ensure Google Play Services are updated

### iOS
- **Package resolution**: Try **File → Packages → Reset Package Caches** in Xcode
- **Framework not found**: Verify the GitHub release exists with the XCFramework attached
- **Runtime crashes**: Check Info.plist permissions are added
