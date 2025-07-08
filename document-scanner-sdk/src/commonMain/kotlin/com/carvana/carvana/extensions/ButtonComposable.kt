package com.carvana.carvana.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carvana.carvana.resources.CarvanaPrimary

@Composable
fun setTextButton(text: String, onClick: () -> Unit, color: Color = CarvanaPrimary) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun setButton(text: String, onClick: () -> Unit, color: Color = CarvanaPrimary) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().background(color),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColorFor(color)
        )
    ) {
        Text(text = text, color = Color.White)
    }
}
