package com.carvana.carvana

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy
import platform.Foundation.NSURL

actual fun getCarvanaBrandonRegularFont(): FontFamily {
    val fontData = loadFontData("brandon_regular.otf")
    return FontFamily(Font("brandon_regular.otf", fontData, weight = FontWeight.Normal))
}

actual fun getCarvanaBrandonBoldFont(): FontFamily {
    val fontData = loadFontData("brandon_bold.otf")
    return FontFamily(Font("brandon_bold.otf", fontData, weight = FontWeight.Bold))
}

@OptIn(ExperimentalForeignApi::class)
private fun loadFontData(name: String): ByteArray {
    val path = NSBundle.mainBundle.pathForResource(name, null)
        ?: error("Font $name not found in bundle")
    val url = NSURL.fileURLWithPath(path)
    val nsData = NSData.dataWithContentsOfURL(url) ?: error("Cannot load font data: $name")
    val bytes = ByteArray(nsData.length.toInt())
    bytes.usePinned {
        memcpy(it.addressOf(0), nsData.bytes, nsData.length)
    }
    return bytes
}