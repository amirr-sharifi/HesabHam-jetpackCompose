package af.amir.hesabham.presentation.home.components

import af.amir.hesabham.presentation.core.model.BottomNavItem
import af.amir.hesabham.presentation.core.theme.shapes.menuBarShape
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun ExpenseBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (BottomNavItem) -> Unit,
    onFabClick: () -> Unit,
) {

    val fabSize = 64.dp
    val density = LocalDensity.current

    val fabRadiusPx = with(density) { fabSize.toPx() / 2 }
    val cornerRadiusPx = with(density) { 24.dp.toPx() }

    val shape = remember {
        menuBarShape(
            fabRadiusPx = fabRadiusPx,
            cornerRadiusPx = cornerRadiusPx
        )
    }

    Box(
        Modifier
            .padding(12.dp)
            .height(56.dp + fabSize / 2)
            .background(Color.Transparent)
    )

    {

        // Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = shape
                )
                .align(Alignment.BottomCenter)
        )

        // FAB
        FloatingActionButton(
            onClick = onFabClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(fabSize)
                .align(Alignment.TopCenter)
                .offset(y = (-fabSize / 2) + 4.dp)
        )
        {
            Icon(Icons.Default.Add, contentDescription = null)
        }

        // Navigation Items
        Row(
            modifier = Modifier
                .height(64.dp)
                .align(Alignment.BottomCenter)
        ) {

            val leftItems = items.take(items.size / 2)
            val rightItems = items.takeLast(items.size / 2)

            leftItems.forEach { item ->
                BottomBarItem(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item) }
                )
            }

            Spacer(Modifier.width(fabSize))

            rightItems.forEach { item ->
                BottomBarItem(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item) }
                )
            }
        }
    }
}


@Composable
private fun RowScope.BottomBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {

    val transition = updateTransition(selected, label = "nav_transition")

    val scale by transition.animateFloat(label = "scale") {
        if (it) 1.15f else 1f
    }

    val tint by transition.animateColor(label = "color") {
        if (it) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant
    }

    IconButton(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        onClick = onClick
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.scale(scale)
        )
    }
}

