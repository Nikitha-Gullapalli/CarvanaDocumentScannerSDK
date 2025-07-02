# How to Publish Carvana Document Scanner SDK

## Option 1: Maven Central (Recommended for Public SDK)

### Step 1: Prepare build.gradle for publishing

Create a new file `composeApp/publish.gradle.kts`:

```kotlin
apply plugin: 'maven-publish'
apply plugin: 'signing'

group = 'com.carvana'
version = '1.0.0'

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        release(MavenPublication) {
            groupId = 'com.carvana'
            artifactId = 'document-scanner-sdk'
            version = '1.0.0'
            
            afterEvaluate {
                from components.release
            }
            
            pom {
                name = 'Carvana Document Scanner SDK'
                description = 'SDK for scanning and uploading documents'
                url = 'https://github.com/carvana/document-scanner-sdk'
                
                licenses {
                    license {
                        name = 'The Apache License, Version 2.0'
                        url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                
                developers {
                    developer {
                        id = 'carvana'
                        name = 'Carvana'
                        email = 'dev@carvana.com'
                    }
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatype"
            url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            credentials {
                username = project.findProperty("ossrhUsername")
                password = project.findProperty("ossrhPassword")
            }
        }
    }
}

signing {
    sign publishing.publications.release
}
```

### Step 2: Register on Sonatype
1. Create account at https://issues.sonatype.org
2. Create a ticket to claim your groupId (com.carvana)
3. Wait for approval

### Step 3: Publish
```bash
./gradlew publishReleasePublicationToSonatypeRepository
```

## Option 2: GitHub Packages (Good for Private/Internal)

### Step 1: Add to build.gradle

```kotlin
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/carvana/document-scanner-sdk")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        gpr(MavenPublication) {
            groupId = 'com.carvana'
            artifactId = 'document-scanner-sdk'
            version = '1.0.0'
            
            afterEvaluate {
                from components.release
            }
        }
    }
}
```

### Step 2: Publish
```bash
./gradlew publishGprPublicationToGitHubPackagesRepository
```

### Step 3: External apps add dependency
```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/carvana/document-scanner-sdk")
        credentials {
            username = githubProperties['gpr.usr']
            password = githubProperties['gpr.key']
        }
    }
}

dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
}
```

## Option 3: Local Maven (For Testing)

### Step 1: Publish to local
```bash
./gradlew publishToMavenLocal
```

### Step 2: External apps use
```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.carvana:document-scanner-sdk:1.0.0'
}
```

## Option 4: AAR File (Simple but Manual)

### Step 1: Build AAR
```bash
./gradlew :composeApp:assembleRelease
```

The AAR will be in: `composeApp/build/outputs/aar/composeApp-release.aar`

### Step 2: External apps use AAR
1. Copy AAR to their project: `app/libs/carvana-scanner-sdk.aar`
2. Add to build.gradle:
```gradle
dependencies {
    implementation files('libs/carvana-scanner-sdk.aar')
    
    // They also need your dependencies
    implementation 'com.google.mlkit:text-recognition:16.0.0'
    // ... other dependencies
}
```

## Option 5: JitPack (Easiest for GitHub projects)

### Step 1: Push to GitHub

### Step 2: Create a release/tag

### Step 3: External apps use
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.carvana:document-scanner-sdk:1.0.0'
}
```

## Preparing Your SDK for Publishing

### 1. Create a proper library module
Currently your SDK is mixed with the demo app. You should separate them:

```
CarvanaDocumentScannerSDK/
├── sdk/                    # Library module (publish this)
│   ├── src/
│   │   ├── androidMain/
│   │   ├── commonMain/
│   │   └── iosMain/
│   └── build.gradle.kts
├── demo/                   # Demo app (don't publish)
│   ├── src/
│   └── build.gradle.kts
└── settings.gradle.kts
```

### 2. Configure library build.gradle
```kotlin
plugins {
    id("com.android.library")  // Not application!
    kotlin("multiplatform")
}

android {
    namespace = "com.carvana.scanner.sdk"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
    }
}
```

### 3. Expose only public APIs
- Mark internal classes as `internal`
- Only expose what external apps need

## Quick Start: JitPack (Easiest)

1. Push your code to GitHub
2. Go to https://jitpack.io
3. Enter your GitHub URL
4. Click "Get it"
5. Follow the instructions

External apps can then use:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.YourGitHubUsername:CarvanaDocumentScannerSDK:Tag'
}
```

## Which Option to Choose?

- **Maven Central**: Professional, public SDK
- **GitHub Packages**: Good for private/company SDKs  
- **JitPack**: Easiest, good for open source
- **AAR file**: Quick testing, not scalable
- **Local Maven**: Development testing only