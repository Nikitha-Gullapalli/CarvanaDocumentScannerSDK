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
            url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/_packaging/CarvanaDocumentScannerSDK/maven/v1")
        }
    }
}
```

### 2. Add Dependency

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.carvana:document-scanner-sdk:1.0.0")
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

### 1. Add Permissions

Add to `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>Camera access needed to scan documents</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Photo library access needed to upload documents</string>
```

### 2. Basic Implementation

```swift
import UIKit
import ComposeApp

class ViewController: UIViewController {
    
    @IBAction func scanDocumentTapped(_ sender: UIButton) {
        let scannerVC = StartCarvanaDocumentScannerSDKViewControllerKt
            .StartCarvanaDocumentScannerSDKViewController()
        
        scannerVC.modalPresentationStyle = .fullScreen
        present(scannerVC, animated: true)
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