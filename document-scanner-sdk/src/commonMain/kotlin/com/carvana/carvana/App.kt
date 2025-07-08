package com.carvana.carvana

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carvana.document_scanner_sdk.generated.resources.Res
import com.carvana.document_scanner_sdk.generated.resources.ic_close_blue_accent
import com.carvana.document_scanner_sdk.generated.resources.ic_ico_docupload
import com.carvana.carvana.interfaces.SDKCallbackManager
import com.carvana.carvana.extensions.SetIconButton
import com.carvana.carvana.extensions.setBulletsText
import com.carvana.carvana.extensions.setButton
import com.carvana.carvana.extensions.setIcon16dp
import com.carvana.carvana.extensions.setImageBoxWithContent
import com.carvana.carvana.extensions.setTextAppearanceBody5
import com.carvana.carvana.extensions.setTextAppearanceHeadline6
import com.carvana.carvana.extensions.setTextButton
import com.carvana.carvana.interfaces.DocumentScanner
import com.carvana.carvana.interfaces.DocumentUploader
import com.carvana.carvana.interfaces.ScanResult
import com.carvana.carvana.interfaces.UploadResult
import com.carvana.carvana.interfaces.logger
import com.carvana.carvana.resources.Primary
import com.carvana.carvana.resources.Strings
import com.carvana.carvana.resources.Strings.ON_CLOSE_CLICK
import com.carvana.carvana.resources.Strings.PLEASE_SCAN
import com.carvana.carvana.resources.Strings.SCAN_DOCUMENTS
import com.carvana.carvana.resources.Strings.SETTING_SHOW_CONTENT_TRUE
import com.carvana.carvana.resources.Strings.TAG
import com.carvana.carvana.resources.Strings.TAKE_A_PHOTO
import com.carvana.carvana.resources.Strings.TIPS_GOOD_PHOTO
import com.carvana.carvana.resources.Strings.UPLOAD_FROM_LIBRARY


@Composable
fun App(
    onExit: (() -> Unit)? = null,
    documentScanner: DocumentScanner,
    documentUploader: DocumentUploader
) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CardScanScreen(
                showContent,
                onCloseClick = {
                    SDKCallbackManager.handleExit()
                },
                onTakePhotoClick = {
                    scanDocument(documentScanner)
                },
                onUploadClick = {
                    uploadDocument(documentUploader)
                },
                onCloseScreen = {
                    if (onExit != null) {
                        logger.d(TAG, "$ON_CLOSE_CLICK $onExit")
                        onExit()
                    } else {
                        logger.d(TAG, "$ON_CLOSE_CLICK $onExit $SETTING_SHOW_CONTENT_TRUE")
                        showContent = true
                    }
                }
            )
        }
    }
}

@Composable
fun CardScanScreen(
    showContent: Boolean,
    onCloseClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onUploadClick: () -> Unit,
    onCloseScreen: () -> Unit
) {
    if (showContent) {
        showCardScanScreenContent(
            onCloseClick,
            onTakePhotoClick,
            onUploadClick
        )
    } else {
        onCloseScreen()
    }
}

@Composable
fun showCardScanScreenContent(
    onCloseClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onUploadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ContentSection(
            modifier = Modifier.weight(0.85f),
            onCloseClick = onCloseClick
        )

        ActionButtonsSection(
            modifier = Modifier.weight(0.15f),
            onTakePhotoClick = onTakePhotoClick,
            onUploadClick = onUploadClick
        )
    }
}

@Composable
private fun ContentSection(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        SetIconButton(
            Res.drawable.ic_close_blue_accent,
            onCloseClick,
        )
        setTextAppearanceHeadline6(text = SCAN_DOCUMENTS)
        setTextAppearanceBody5(
            text = PLEASE_SCAN,
            modifier = Modifier.padding(top = 16.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        setImageBoxWithContent { setIcon16dp(Res.drawable.ic_ico_docupload) }
        setTextAppearanceBody5(
            text = TIPS_GOOD_PHOTO,
            modifier = Modifier.padding(top = 16.dp),
            color = Primary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        val tips = listOf(
            Strings.PLACE_DOCUMENTS_ON_SOLID,
            Strings.SHOOT_IN_GOOD_LIGHT,
            Strings.SHOW_ALL_FOUR_CORNERS
        )
        setBulletsText(tips)
    }
}

@Composable
private fun ActionButtonsSection(
    modifier: Modifier = Modifier,
    onTakePhotoClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        setButton(text = TAKE_A_PHOTO, onClick = onTakePhotoClick)
        setTextButton(text = UPLOAD_FROM_LIBRARY, onClick = onUploadClick)
    }
}


fun uploadDocument(documentUploader: DocumentUploader) {
    documentUploader.uploadDocument { result ->
        when (result) {
            is UploadResult.Success -> {
                logger.d("uploadDocument", "Text recognized: ${result.recognizedText}")
                SDKCallbackManager.handleSuccess(result.recognizedText)
            }

            is UploadResult.Failure -> {
                logger.e("uploadDocument", "Failed: ${result.message}")
                SDKCallbackManager.handleFailure(result.message)
            }
        }
    }
}

fun scanDocument(documentScanner: DocumentScanner) {
    documentScanner.scanDocument { result ->
        when (result) {
            is ScanResult.Success -> {
                logger.d("scanDocument", "Text: ${result.recognizedText}, PDF: ${result.pdfPath}")
                SDKCallbackManager.handleSuccess(result.pdfPath)
            }

            is ScanResult.Failure -> {
                logger.e("scanDocument", "Failed: ${result.message}")
                SDKCallbackManager.handleFailure(result.message)
            }
        }
    }
}
