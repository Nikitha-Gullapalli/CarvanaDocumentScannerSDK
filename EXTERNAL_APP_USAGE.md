# How External Apps Use Carvana Document Scanner SDK

## Integration Steps

### 1. Add SDK Dependency
External apps would add your SDK to their project (once published):
```gradle
dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
}
```

### 2. Launch the SDK

```kotlin
class ExternalAppActivity : AppCompatActivity() {
    
    private val SCAN_REQUEST_CODE = 1001
    
    // Button click to launch scanner
    fun onScanDocumentClick() {
        // Option 1: Direct launch
        val intent = Intent(this, SDKEntryActivity::class.java)
        startActivityForResult(intent, SCAN_REQUEST_CODE)
        
        // Option 2: Using action (if SDK is separate APK)
        val intent = Intent("com.carvana.scanner.SCAN_DOCUMENT")
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, SCAN_REQUEST_CODE)
        }
    }
    
    // Handle the result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SCAN_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    // Success! Get the document
                    val documentPath = data?.getStringExtra(SDKEntryActivity.EXTRA_DOCUMENT_PATH)
                    val documentType = data?.getStringExtra(SDKEntryActivity.EXTRA_DOCUMENT_TYPE)
                    
                    // Now they can upload to their server
                    uploadToServer(documentPath)
                }
                RESULT_CANCELED -> {
                    // User cancelled or error occurred
                    val error = data?.getStringExtra(SDKEntryActivity.EXTRA_ERROR_MESSAGE)
                    if (error != null) {
                        showError(error)
                    }
                }
            }
        }
    }
}
```

## What Happens

1. External app launches `SDKEntryActivity`
2. Your `App.kt` UI is displayed with "Take Photo" and "Upload" buttons
3. User interacts with YOUR UI (not the external app's UI)
4. When complete, results are returned to the external app
5. External app receives the document path and can process it

## The Flow

```
External App                Your SDK
    │                          │
    ├─── Launch SDK ──────────>│
    │                          │
    │                    ┌─────┴─────┐
    │                    │  App.kt   │
    │                    │           │
    │                    │ [📷] [📁] │
    │                    └─────┬─────┘
    │                          │
    │                     User clicks
    │                          │
    │                    ┌─────┴─────┐
    │                    │  Camera   │
    │                    │    or     │
    │                    │  Upload   │
    │                    └─────┬─────┘
    │                          │
    │<──── Return Result ──────┤
    │      (document path)     │
    │                          │
    └── Process Document       │
```

## Key Points

- App.kt is your UI entry point (kept in commonMain for cross-platform)
- SDKEntryActivity is just a thin Android wrapper to display App.kt
- External apps never see your internal implementation
- Results are returned via standard Android Activity results