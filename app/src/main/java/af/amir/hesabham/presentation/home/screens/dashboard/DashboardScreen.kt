package af.amir.hesabham.presentation.home.screens.dashboard

import af.amir.hesabham.presentation.core.components.AccountDetailBottomSheet
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.components.TransactionDetailsBottomSheet
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.home.screens.dashboard.compenents.RecentTransactionsSection
import af.amir.hesabham.presentation.home.screens.dashboard.compenents.BalanceCardSection
import af.amir.hesabham.presentation.home.screens.dashboard.compenents.ExpenseReportSection
import af.amir.hesabham.presentation.home.screens.dashboard.compenents.MyAccountsSection
import af.amir.hesabham.presentation.home.screens.dashboard.compenents.TopHeaderSection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToAccountsScreen: () -> Unit,
    onNavigateToTransactionsScreen: () -> Unit,
    onNavigateToAddTransaction: (transactionId: String?) -> Unit,
    onNavigateToReportsScreen: () -> Unit,
) {
    val viewModel = koinViewModel<DashboardViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreenStateless(
        state,
        viewModel::onAction,
        onNavigateToAccountsScreen,
        onNavigateToTransactionsScreen,
        onNavigateToAddTransaction,
        onNavigateToReportsScreen
    )
}

@Composable
fun DashboardScreenStateless(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    onNavigateToAccountsScreen: () -> Unit,
    onNavigateToTransactionsScreen: () -> Unit,
    onNavigateToAddTransaction: (transactionId: String?) -> Unit,
    onNavigateToReportsScreen: () -> Unit,
) {

    var tempTransactionForBottomSheet by remember {
        mutableStateOf<TransactionWithDetailUiModel?>(null)
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            TopHeaderSection(
                state.selectedMonthName,
                onPreviousMonth = { onAction(DashboardAction.PreviousMonth) },
                onNextMonth = { onAction(DashboardAction.NextMonth) })
        }
        item {
            BalanceCardSection(
                state.totalBalance,
                state.monthlyBalance,
                state.monthlyIncome,
                state.monthlyExpense
            )
        }
        item {
            MyAccountsSection(state.accounts, onNavigateToAccountsScreen)
        }
        item {
            RecentTransactionsSection(
                transactions = state.recentTransactions,
                onSeeAllClick = onNavigateToTransactionsScreen,
                onTransactionClick = { tempTransactionForBottomSheet = it }
            )
        }
        item {
            ExpenseReportSection(
                state.chartCategories,
                state.chartTotalAmount
            ) { onNavigateToReportsScreen() }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }


    tempTransactionForBottomSheet?.let { transaction ->
        TransactionDetailsBottomSheet(
            transaction,
            showDeleteButton = false,
            onDeleteTransactionClick = {},
            onDismissRequest = { tempTransactionForBottomSheet = null },
            onEditTransactionClick = onNavigateToAddTransaction
        )
    }




}
