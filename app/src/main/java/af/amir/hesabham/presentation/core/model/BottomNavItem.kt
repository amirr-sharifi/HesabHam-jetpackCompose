package af.amir.hesabham.presentation.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Dashboard : BottomNavItem(
        route = "dashboard",
        icon = Icons.Rounded.Home,
        selectedIcon = Icons.Filled.Home
    )

    object Transactions : BottomNavItem(
        route = "transactions",
        icon = Icons.Rounded.ReceiptLong,
        selectedIcon = Icons.Filled.ReceiptLong
    )

    object Reports : BottomNavItem(
        route = "stats",
        icon = Icons.Rounded.BarChart,
        selectedIcon = Icons.Filled.BarChart
    )

    object Settings : BottomNavItem(
        route = "settings",
        icon = Icons.Rounded.Settings,
        selectedIcon = Icons.Filled.Settings
    )
}
