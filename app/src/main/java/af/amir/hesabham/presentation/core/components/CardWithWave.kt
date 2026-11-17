package af.amir.hesabham.presentation.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CardWithWave(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    shadow: Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                shadow,
                RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .drawBehind {
                drawCardWaves(
                    backgroundColor = color,
                    waveColor = Color.White.copy(alpha = 0.1f)
                )
            }
            .height(IntrinsicSize.Max)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {


        content()
    }
}


private fun DrawScope.drawCardWaves(backgroundColor: Color, waveColor: Color) {

    val canvasWidth = size.width
    val canvasHeight = size.height
    val cornerRadius = 24.dp.toPx()

    val cardShapePath = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f,
                top = 0f,
                right = canvasWidth,
                bottom = canvasHeight,
                cornerRadius = CornerRadius(cornerRadius)
            )
        )
    }

    clipPath(cardShapePath) {
        drawRect(color = backgroundColor)

        val wavePath = Path().apply {
            moveTo(0f, canvasHeight * 0.8f)
            cubicTo(
                x1 = canvasWidth * 0.15f,
                y1 = canvasHeight * 0.98f,
                x2 = canvasWidth * 0.35f,
                y2 = canvasHeight * 0.98f,
                x3 = canvasWidth * 0.70f,
                y3 = canvasHeight * 0.65f
            )
            cubicTo(
                x1 = canvasWidth * 0.85f, y1 = canvasHeight * 0.50f,
                x2 = canvasWidth * 1.0f, y2 = canvasHeight * 0.60f,
                x3 = canvasWidth, y3 = canvasHeight * 0.60f
            )

            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        drawPath(
            path = wavePath,
            color = waveColor
        )
    }

}