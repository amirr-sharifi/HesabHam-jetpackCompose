package af.amir.hesabham.presentation.core.theme.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.pow

class SquircleShape(private val smoothing: Float = 3f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // ترسیم مسیر بر اساس فرمول Squircle (Superellipse)
            val steps = 100
            for (i in 0..steps) {
                val t = 2 * Math.PI * i / steps
                val x = centerX + centerX * kotlin.math.cos(t).pow(2.0 / smoothing).toFloat() * kotlin.math.sign(kotlin.math.cos(t)).toFloat()
                val y = centerY + centerY * kotlin.math.sin(t).pow(2.0 / smoothing).toFloat() * kotlin.math.sign(kotlin.math.sin(t)).toFloat()

                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        })
    }
}