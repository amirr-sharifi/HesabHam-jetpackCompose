package af.amir.hesabham.presentation.core.components.bottomwavesscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun BottomWaves(
    modifier: Modifier = Modifier
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height


        val backWavePath = Path().apply {
            moveTo(0f, height * 0.85f)
            cubicTo(
                x1 = width * 0.25f, y1 = height * 0.95f,
                x2 = width * 0.6f, y2 = height * 0.82f,
                x3 = width, y3 = height * 0.85f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = backWavePath,
            color = primaryColor.copy(alpha = 0.08f)
        )


        val frontWavePath = Path().apply {
            moveTo(0f, height * 0.92f)
            cubicTo(
                x1 = width * 0.35f, y1 = height * 0.8f,
                x2 = width * 0.75f, y2 = height * 0.98f,
                x3 = width, y3 = height * 0.85f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = frontWavePath,
            color = primaryColor.copy(alpha = 0.12f)
        )
    }
}





