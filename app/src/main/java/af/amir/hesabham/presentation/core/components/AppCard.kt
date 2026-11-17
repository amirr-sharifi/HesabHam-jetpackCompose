package af.amir.hesabham.presentation.core.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit,
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = background),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        content = content
    )
}