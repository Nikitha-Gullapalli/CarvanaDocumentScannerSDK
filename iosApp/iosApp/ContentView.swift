import UIKit
import SwiftUI
import CarvanaDocumentScannerSDK

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        StartCarvanaDocumentScannerSDKViewControllerKt.StartCarvanaDocumentScannerSDKViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



