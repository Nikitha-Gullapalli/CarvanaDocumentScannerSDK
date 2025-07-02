package com.carvana.carvana

import Strings
import Strings.PLEASE_SCAN
import Strings.SCAN_DOCUMENTS
import Strings.TAG
import Strings.TAKE_A_PHOTO
import Strings.TIPS_GOOD_PHOTO
import Strings.UPLOAD_FROM_LIBRARY
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
import carvanadocumentscannersdk.composeapp.generated.resources.Res
import carvanadocumentscannersdk.composeapp.generated.resources.ic_close_blue_accent
import carvanadocumentscannersdk.composeapp.generated.resources.ic_ico_docupload
import com.carvana.carvana.interfaces.SDKCallbackManager
import com.carvana.carvana.extensions.scanDocument
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
import com.carvana.carvana.interfaces.logger
import com.carvana.carvana.resources.Primary
import com.carvana.carvana.extensions.uploadDocument


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
                onTakePhotoClick = { scanDocument(documentScanner) },
                onUploadClick = { uploadDocument(documentUploader) },
                onCloseScreen = {
                    if (onExit != null) {
                        logger.d(TAG, "onCloseScreen: $onExit")
                        onExit()
                    } else {
                        logger.d(TAG, "onCloseScreen: $onExit so setting showContent to true")
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
