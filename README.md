# Carvana Document Scanner SDK

An SDK for scanning and uploading documents with OCR capabilities.

## Installation

### Step 1: Add Azure Artifacts repository
In your app's `build.gradle`:

```gradle
repositories {
    google()
    mavenCentral()
    
    maven {
        url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/_packaging/CarvanaSDK/maven/v1")
        credentials {
            username = "YOUR_AZURE_USERNAME"
            password = "YOUR_AZURE_PAT"  // Personal Access Token
        }
    }
}
```

### Step 2: Add the dependency
```gradle
dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
}
```

## Usage

### Launch the Scanner

```kotlin
class YourActivity : AppCompatActivity() {
    
    private val SCAN_REQUEST_CODE = 1001
    
    fun launchScanner() {
        val intent = Intent(this, SDKEntryActivity::class.java)
        startActivityForResult(intent, SCAN_REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SCAN_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val documentPath = data?.getStringExtra(SDKEntryActivity.EXTRA_DOCUMENT_PATH)
                    val documentType = data?.getStringExtra(SDKEntryActivity.EXTRA_DOCUMENT_TYPE)
                    
                    // Document successfully scanned/uploaded
                    // Process the document path
                }
                RESULT_CANCELED -> {
                    val error = data?.getStringExtra(SDKEntryActivity.EXTRA_ERROR_MESSAGE)
                    // Handle cancellation or error
                }
            }
        }
    }
}
```

### Add to Manifest

Add this to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

## Features

- Document scanning using camera
- Document upload from device storage
- OCR text extraction
- PDF generation
- Cross-platform UI

## Requirements

- Android API 21+
- Camera permission for scanning

## License

Internal use only - Carvana