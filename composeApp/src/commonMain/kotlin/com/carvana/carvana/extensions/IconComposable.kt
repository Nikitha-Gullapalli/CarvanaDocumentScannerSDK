package  com.carvana.carvana.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun setIcon16dp(resource: DrawableResource) {
    Icon(
        painter = painterResource(resource),
        contentDescription = resource.toString(),
    )
}

@Composable
fun setIcon(resource: DrawableResource, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(resource),
        contentDescription = resource.toString(),
        modifier = modifier
    )
}

@Composable
fun SetIconButton(
    resource: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            setIcon16dp(resource)
        }
    }
}
