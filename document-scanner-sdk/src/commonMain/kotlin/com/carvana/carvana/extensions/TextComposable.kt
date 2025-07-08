package com.carvana.carvana.extensions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carvana.document_scanner_sdk.generated.resources.Res
import com.carvana.document_scanner_sdk.generated.resources.ic_bullet
import com.carvana.carvana.interfaces.getCarvanaBrandonBoldFont
import com.carvana.carvana.interfaces.getCarvanaBrandonRegularFont
import com.carvana.carvana.resources.DarkNavyBlue
import com.carvana.carvana.resources.StormGray

/**Bold 24sp */
@Composable
fun setTextAppearanceHeadline6(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DarkNavyBlue
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        fontFamily = getCarvanaBrandonBoldFont(),
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall,
        color = color,
        textAlign = TextAlign.Start
    )
}

/**Regular 14sp */
@Composable
fun setTextAppearanceBody5(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = StormGray,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = 14.sp,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = getCarvanaBrandonRegularFont(),
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}

@Composable
fun setBulletsText(tips: List<String>) {
    tips.forEach { tipRes ->
        bulletItemText(text = tipRes)
    }
}

@Composable
fun bulletItemText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        setIcon(resource = Res.drawable.ic_bullet)
        Spacer(modifier = Modifier.width(8.dp))
        setTextAppearanceBody5(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}


