package af.amir.hesabham.presentation.home

import af.amir.hesabham.presentation.core.model.BottomNavItem
import af.amir.hesabham.presentation.home.components.ExpenseBottomBar
import af.amir.hesabham.presentation.home.screens.dashboard.DashboardScreen
import af.amir.hesabham.presentation.home.screens.settings.SettingsScreen
import af.amir.hesabham.presentation.home.screens.reports.ReportsScreen
import af.amir.hesabham.presentation.home.screens.transactions.TransactionsScreen
import af.amir.hesabham.presentation.navigation.RootNavigationRoutes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun HesabHamScreen(
    onNavigateTo: (route: String) -> Unit,
    onNavigateToAddTransaction: (transactionId: String?) -> Unit,
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            ExpenseBottomBar(
                items = listOf(
                    BottomNavItem.Dashboard,
                    BottomNavItem.Transactions,
                    BottomNavItem.Reports,
                    BottomNavItem.Settings
                ),
                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    ?: BottomNavItem.Dashboard.route,
                onNavigate = { item ->
                    if (navController.currentBackStackEntry?.destination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onFabClick = { onNavigateToAddTransaction(null) }
            )
        }
    ) { padding ->


        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTransactionsScreen = {
                        navController.navigate(BottomNavItem.Transactions.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            restoreState = true
                        }
                    },
                    onNavigateToAccountsScreen = { onNavigateTo(RootNavigationRoutes.Accounts.route)},
                    onNavigateToAddTransaction = onNavigateToAddTransaction,
                    onNavigateToReportsScreen = {
                        navController.navigate(BottomNavItem.Reports.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }},
                )
            }
            composable(BottomNavItem.Transactions.route) { TransactionsScreen(onNavigateToAddTransaction) }
            composable(BottomNavItem.Reports.route) { ReportsScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
        }
    }


}
