# Azure DevOps Setup for Carvana Document Scanner SDK

## Prerequisites

1. Access to Azure DevOps project: https://dev.azure.com/NikithaGullapalli/CarvanaDocumentScannerSDK
2. Personal Access Token (PAT) with Package read/write permissions

## Step 1: Create Azure Artifacts Feed

1. Go to your Azure DevOps project
2. Navigate to Artifacts
3. Click "Create Feed"
4. Name it: `CarvanaSDK`
5. Visibility: Select based on your organization needs
6. Click "Create"

## Step 2: Get Personal Access Token (PAT)

1. Click on your profile → Personal access tokens
2. New Token
3. Name: `SDK Publishing`
4. Scopes: 
   - Packaging (Read, write, & manage)
5. Create and save the token securely

## Step 3: Configure Local Credentials

Create `~/.gradle/gradle.properties`:
```properties
azure.username=NikithaGullapalli
azure.password=YOUR_PERSONAL_ACCESS_TOKEN
```

## Step 4: Publish the SDK

```bash
# Clean build
./gradlew clean

# Build the library
./gradlew :composeApp:assembleRelease

# Publish to Azure Artifacts
./gradlew :composeApp:publish
```

## Step 5: Using the SDK in Other Apps

### Add Azure Artifacts Repository

In the consuming app's `build.gradle`:

```gradle
repositories {
    google()
    mavenCentral()
    
    maven {
        url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/_packaging/CarvanaSDK/maven/v1")
        credentials {
            username = "NikithaGullapalli"
            password = "YOUR_PAT_TOKEN"  // Use token with read permissions
        }
    }
}

dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
}
```

### Alternative: Using gradle.properties

In `~/.gradle/gradle.properties`:
```properties
azureArtifactsUsername=NikithaGullapalli
azureArtifactsPassword=YOUR_PAT_TOKEN
```

In `build.gradle`:
```gradle
maven {
    url = uri("https://pkgs.dev.azure.com/NikithaGullapalli/_packaging/CarvanaSDK/maven/v1")
    credentials {
        username = project.findProperty("azureArtifactsUsername") as String
        password = project.findProperty("azureArtifactsPassword") as String
    }
}
```

## Azure Pipeline for Automated Publishing

Create `azure-pipelines.yml`:

```yaml
trigger:
  tags:
    include:
    - v*

pool:
  vmImage: 'ubuntu-latest'

steps:
- task: Gradle@2
  inputs:
    gradleWrapperFile: 'gradlew'
    tasks: 'clean :composeApp:assembleRelease :composeApp:publish'
    publishJUnitResults: false
  env:
    AZURE_ARTIFACTS_USER: $(System.CollectionId)
    AZURE_ARTIFACTS_PAT: $(System.AccessToken)
```

## Version Management

To publish a new version:
1. Update version in `build.gradle.kts`
2. Commit and push
3. Create a tag: `git tag v1.0.1 && git push origin v1.0.1`

## Troubleshooting

### Authentication Failed
- Ensure PAT has Package write permissions
- Check token hasn't expired
- Verify feed name matches

### Build Fails
- Run `./gradlew clean` first
- Check that you changed from `androidApplication` to `androidLibrary`

### Can't Find Package
- Ensure feed URL is correct
- Check consuming app has read permissions
- Verify package was published successfully

## SDK Integration Example

```kotlin
// In your app
import com.carvana.carvana.SDKEntryActivity

class YourActivity : AppCompatActivity() {
    
    fun launchScanner() {
        val intent = Intent(this, SDKEntryActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val documentPath = data?.getStringExtra(SDKEntryActivity.EXTRA_DOCUMENT_PATH)
            // Use the document
        }
    }
}
```