package af.amir.hesabham.presentation.onboarding.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun SelectableChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    textFontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    onClick: () -> Unit,
) {
    val background =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent

    val borderColor = if (selected) background else MaterialTheme.colorScheme.onSurfaceVariant
    val textColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant


    Row(
        modifier = modifier
            .background(background, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .border(if (selected) 0.dp else 1.dp, borderColor, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text,
            color = textColor,
            fontSize = textFontSize,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Light,
        )
        if (selected) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Rounded.Check, "", tint = textColor)
        }
    }
}