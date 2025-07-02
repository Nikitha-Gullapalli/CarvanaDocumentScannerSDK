#!/bin/bash

echo "Initializing Git repository..."
git init

echo "Adding Azure DevOps remote..."
git remote add origin https://dev.azure.com/NikithaGullapalli/CarvanaDocumentScannerSDK/_git/CarvanaDocumentScannerSDK

echo "Adding all files..."
git add .

echo "Creating initial commit..."
git commit -m "Initial commit: Carvana Document Scanner SDK

- Document scanning with ML Kit
- Document upload functionality  
- OCR text extraction
- PDF generation
- Cross-platform architecture with Kotlin Multiplatform"

echo "Pushing to Azure DevOps..."
git push -u origin main

echo "Done! Your code is now on Azure DevOps."
echo "Next steps:"
echo "1. Create an Azure Artifacts feed called 'CarvanaSDK'"
echo "2. Get a Personal Access Token (PAT)"
echo "3. Run: ./gradlew :composeApp:publish"