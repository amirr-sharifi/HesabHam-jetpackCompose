package af.amir.hesabham.presentation.core.components

import af.amir.hesabham.presentation.core.theme.PrimaryButtonColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    title: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold,
    shape: Shape = MaterialTheme.shapes.medium,
    onClick: () -> Unit,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(shape)
            .background(PrimaryButtonColor, shape)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Text(title, color = MaterialTheme.colorScheme.onPrimary, fontWeight = fontWeight)
    }

}