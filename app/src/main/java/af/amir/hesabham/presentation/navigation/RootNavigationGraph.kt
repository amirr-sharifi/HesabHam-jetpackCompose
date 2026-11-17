package af.amir.hesabham.presentation.navigation

import af.amir.hesabham.AppConfigViewModel
import af.amir.hesabham.presentation.accounts.AccountsScreen
import af.amir.hesabham.presentation.add_transaction.AddTransactionScreen
import af.amir.hesabham.presentation.home.HesabHamScreen
import af.amir.hesabham.presentation.onboarding.OnboardingScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    viewModel: AppConfigViewModel,
) {

    NavHost(
        navController,
        startDestination = RootNavigationRoutes.Root.route,
        modifier = Modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        composable(route = RootNavigationRoutes.Root.route) {
            RootDeciderScreen(viewModel, navController)
        }
        composable(route = RootNavigationRoutes.Onboarding.route) {
            OnboardingScreen() {
                navController.navigate(RootNavigationRoutes.HesabHam.route) {
                    popUpTo(RootNavigationRoutes.Onboarding.route) { inclusive = true }
                }
            }
        }

        composable(route = RootNavigationRoutes.HesabHam.route) {
            HesabHamScreen(onNavigateTo = { route ->
                navController.navigate(route)
            }, onNavigateToAddTransaction = { transactionId ->
                navController.navigate(
                    RootNavigationRoutes.AddTransaction.passTransactionId(transactionId)
                )
            })
        }

        composable(
            route = RootNavigationRoutes.AddTransaction.route, arguments =
            listOf(navArgument(
                name = TRANSACTION_ID,
            ) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            AddTransactionScreen(
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = RootNavigationRoutes.Accounts.route) {
            AccountsScreen(onNavigateUp = { navController.navigateUp() },
                onNavigateToAddTransaction = {
                    navController.navigate(
                        RootNavigationRoutes.AddTransaction.passTransactionId(null)
                    )
                })
        }

    }

}

@Composable
private fun RootDeciderScreen(
    viewModel: AppConfigViewModel,
    navController: NavHostController,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) return@LaunchedEffect

        if (state.setting.isOnboardingCompleted) {
            navController.navigate(RootNavigationRoutes.HesabHam.route) {
                popUpTo(RootNavigationRoutes.Root.route) { inclusive = true }
            }
        } else {
            navController.navigate(RootNavigationRoutes.Onboarding.route) {
                popUpTo(RootNavigationRoutes.Root.route) { inclusive = true }
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )


}