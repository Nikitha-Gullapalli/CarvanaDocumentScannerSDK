package com.carvana.carvana.interfaces

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.carvana.carvana.R

actual fun getCarvanaBrandonRegularFont() = FontFamily(Font(R.font.brandon_regular, weight = FontWeight.Normal))

actual fun getCarvanaBrandonBoldFont() = FontFamily(Font(R.font.brandon_bold, weight = FontWeight.Bold))