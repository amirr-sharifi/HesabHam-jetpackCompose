package af.amir.hesabham.presentation.core.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppGradient {
    val primary = Brush.linearGradient(
        colors = listOf(
            Color(0xFF5F9CFF),
            Color(0xFF9B59B6),
            Color(0xFFFF7A7A),
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}