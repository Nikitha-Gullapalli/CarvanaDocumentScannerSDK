# Carvana Document Scanner SDK

A powerful cross-platform document scanning and processing SDK built with Kotlin Multiplatform, providing seamless document capture, OCR, and file upload capabilities for Android and iOS applications.

## Features

- 📷 **Document Scanning**: Camera-based document capture with MLKit integration
- 📁 **File Upload**: Support for importing documents from device storage
- 🔍 **OCR Processing**: Extract text from scanned and uploaded documents
- 📄 **PDF Generation**: Convert scanned documents to PDF format
- ✅ **File Validation**: Built-in validation for file size, format, and quality
- 🎨 **Native UI**: Platform-specific user interfaces for optimal user experience
- 🔒 **Secure Processing**: Local file processing with secure temporary storage

## Supported File Formats

### Input Formats
- **Images**: JPEG, PNG, GIF, HEIC
- **Documents**: PDF, TXT, DOC, DOCX
- **Maximum file size**: 10MB

### Output Formats
- PDF (for scanned documents)
- Extracted text content
- Original file format (for uploads)

## Installation

### Android

#### 1. Add SDK Dependency

Add the SDK dependency to your app's `build.gradle` file:

```kotlin
dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
    
    // Required dependencies (if not already included)
    implementation 'androidx.activity:activity-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.core:core-ktx:1.12.0'
}
```

#### 2. Gradle Configuration

Ensure your `build.gradle` (app level) has the following configurations:

```kotlin
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 24
        targetSdk 34
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

### iOS

Add the SDK to your project using Swift Package Manager:

1. In Xcode, go to **File > Add Package Dependencies**
2. Enter the repository URL: `https://github.com/carvana/document-scanner-sdk`
3. Select the latest version and add to your target

Alternatively, using CocoaPods:

```ruby
pod 'CarvanaDocumentScannerSDK', '~> 1.0.0'
```

## Quick Start

### Android Integration

#### 1. Required Imports

```kotlin
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.carvana.carvana.StartCarvanaDocumentScannerSDKActivity
import java.io.File
```

#### 2. Basic Implementation

```kotlin
class MainActivity : AppCompatActivity() {
    
    private val documentScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleScannerResult(result.resultCode, result.data)
    }
    
    private fun launchDocumentScanner() {
        val intent = Intent(this, StartCarvanaDocumentScannerSDKActivity::class.java)
        documentScannerLauncher.launch(intent)
    }
    
    private fun handleScannerResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val documentPath = data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_DOCUMENT_PATH
                )
                val documentType = data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_DOCUMENT_TYPE
                )
                
                // Process the document
                processDocument(documentPath, documentType)
            }
            RESULT_CANCELED -> {
                val errorMessage = data?.getStringExtra(
                    StartCarvanaDocumentScannerSDKActivity.EXTRA_ERROR_MESSAGE
                )
                handleError(errorMessage)
            }
        }
    }
}
```

#### 3. Result Handling

```kotlin
private fun processDocument(documentPath: String?, documentType: String?) {
    documentPath?.let { path ->
        val file = File(path)
        if (file.exists()) {
            when (documentType) {
                "scanned" -> {
                    // Handle scanned PDF document
                    displayPdfDocument(file)
                }
                "uploaded" -> {
                    // Handle uploaded document
                    processUploadedFile(file)
                }
            }
        }
    }
}

private fun handleError(errorMessage: String?) {
    val message = errorMessage ?: "Document scanning was cancelled"
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
```

### iOS Integration

#### 1. Required Imports

```swift
import UIKit
import ComposeApp
import Foundation
```

#### 2. Basic Implementation

```swift
import UIKit
import ComposeApp

class ViewController: UIViewController {
    
    @IBAction func scanDocumentTapped(_ sender: UIButton) {
        presentDocumentScanner()
    }
    
    private func presentDocumentScanner() {
        let scannerVC = StartCarvanaDocumentScannerSDKViewControllerKt
            .StartCarvanaDocumentScannerSDKViewController()
        
        scannerVC.modalPresentationStyle = .fullScreen
        present(scannerVC, animated: true)
    }
}
```

#### 3. Handle Results (Future Enhancement)

```swift
// Note: Callback handling will be enhanced in future versions
private func handleScanSuccess(documentPath: String, documentType: String) {
    print("Document processed: \(documentPath)")
    
    switch documentType {
    case "scanned":
        loadPdfDocument(path: documentPath)
    case "uploaded":
        processUploadedDocument(path: documentPath)
    default:
        break
    }
}
```

## API Reference

### Android

#### Entry Point

**`StartCarvanaDocumentScannerSDKActivity`**

The main activity that hosts the document scanner interface.

#### Intent Extras

| Key | Type | Description |
|-----|------|-------------|
| `EXTRA_DOCUMENT_PATH` | String | Path to the processed document file |
| `EXTRA_DOCUMENT_TYPE` | String | Type of document: "scanned" or "uploaded" |
| `EXTRA_ERROR_MESSAGE` | String | Error message when processing fails |

#### Result Codes

| Code | Description |
|------|-------------|
| `RESULT_OK` | Document processed successfully |
| `RESULT_CANCELED` | User cancelled or error occurred |

### iOS

#### Entry Point

**`StartCarvanaDocumentScannerSDKViewController()`**

Returns a UIViewController that can be presented modally.

## Configuration

### Android Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<!-- Required for camera access -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Required for file access -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- Camera hardware requirement -->
<uses-feature 
    android:name="android.hardware.camera" 
    android:required="true" />
```

### iOS Permissions

Add these usage descriptions to your `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to scan documents</string>

<key>NSPhotoLibraryUsageDescription</key>
<string>This app needs photo library access to upload documents</string>
```

## Advanced Usage

### File Validation

The SDK automatically validates uploaded files for:

- **File size**: Maximum 10MB
- **Supported formats**: JPEG, PNG, PDF, TXT, DOC, DOCX
- **File integrity**: Ensures files are not corrupted

### Error Handling

Common error scenarios and how to handle them:

```kotlin
// Additional imports for dialog handling
import androidx.appcompat.app.AlertDialog
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

private fun handleError(errorMessage: String?) {
    when {
        errorMessage?.contains("File size exceeds") == true -> {
            showDialog("File Too Large", "Please select a file smaller than 10MB")
        }
        errorMessage?.contains("not supported") == true -> {
            showDialog("Unsupported Format", "Please select a PDF, image, or text file")
        }
        errorMessage?.contains("Camera permission") == true -> {
            requestCameraPermission()
        }
        else -> {
            showDialog("Error", errorMessage ?: "An unexpected error occurred")
        }
    }
}
```

### Custom File Processing

After receiving a document, you can implement custom processing:

```kotlin
// Additional imports for file processing
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileInputStream
import java.io.IOException

private fun processDocument(documentPath: String?, documentType: String?) {
    documentPath?.let { path ->
        val file = File(path)
        
        when (documentType) {
            "scanned" -> {
                // PDF document from camera scan
                val pdfFile = file
                extractTextFromPdf(pdfFile)
                uploadToServer(pdfFile)
            }
            "uploaded" -> {
                // User-selected file
                when (file.extension.lowercase()) {
                    "pdf" -> processPdfFile(file)
                    "txt" -> processTextFile(file)
                    "jpg", "png" -> processImageFile(file)
                }
            }
        }
    }
}
```

## Sample Applications

Check out our sample applications for complete implementation examples:

- **Android Sample**: [/samples/android](./samples/android)
- **iOS Sample**: [/samples/ios](./samples/ios)

## Troubleshooting

### Common Issues

#### Android

**Issue**: Camera not working
- **Solution**: Ensure camera permissions are granted and device has camera hardware

**Issue**: File picker not showing
- **Solution**: Check storage permissions and ensure device has file manager app

**Issue**: Large files causing crashes
- **Solution**: Files are automatically validated for size limits (10MB max)

#### iOS

**Issue**: Scanner view not displaying
- **Solution**: Ensure proper modal presentation and view controller lifecycle

**Issue**: File selection not working
- **Solution**: Check photo library and files app permissions

### Performance Tips

1. **File Size**: Keep uploaded files under 5MB for optimal performance
2. **Image Quality**: Use medium quality for faster processing
3. **Memory Management**: Process files immediately and clean up temporary files
4. **Background Processing**: Handle file processing on background threads

## Version History

### v1.0.0
- Initial release
- Document scanning with MLKit
- File upload support
- PDF generation
- Cross-platform Kotlin Multiplatform implementation

## Support

For technical support and questions:

- **Documentation**: [Full API Documentation](./docs/api)
- **Issues**: [GitHub Issues](https://github.com/carvana/document-scanner-sdk/issues)
- **Email**: sdk-support@carvana.com

## License

This SDK is proprietary software owned by Carvana. See [LICENSE](./LICENSE) for details.

---

**Made with ❤️ by the Carvana Engineering Team**