package af.amir.hesabham.presentation.core.theme.shapes

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation


fun menuBarShape(
    fabRadiusPx: Float,
    cornerRadiusPx: Float,
    notchScale: Float = 1.4f,
    notchDepthScale: Float = 0.8f
) = GenericShape { size, _ ->

    val centerX = size.width / 2
    val notchRadius = fabRadiusPx * notchScale
    val notchDepth = fabRadiusPx * notchDepthScale

    addRoundRect(
        RoundRect(
            rect = Rect(0f, 0f, size.width, size.height),
            topLeft = CornerRadius(cornerRadiusPx),
            topRight = CornerRadius(cornerRadiusPx),
            bottomLeft = CornerRadius(cornerRadiusPx),
            bottomRight = CornerRadius(cornerRadiusPx)
        )
    )

    val notchPath = Path().apply {
        moveTo(centerX - notchRadius, 0f)

        cubicTo(
            centerX - notchRadius / 2,
            0f,
            centerX - notchRadius / 2,
            notchDepth,
            centerX,
            notchDepth
        )

        cubicTo(
            centerX + notchRadius / 2,
            notchDepth,
            centerX + notchRadius / 2,
            0f,
            centerX + notchRadius,
            0f
        )

        close()
    }

    op(this, notchPath, PathOperation.Difference)
}

