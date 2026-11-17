package af.amir.hesabham.presentation.home.screens.dashboard

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.model.CategoryExpenseSummaryUiModel
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import java.math.BigDecimal

data class DashboardState(
    val selectedMonthName : String = "",
    val totalBalance: BigDecimal = BigDecimal.ZERO,
    val monthlyIncome: BigDecimal = BigDecimal.ZERO,
    val monthlyExpense: BigDecimal = BigDecimal.ZERO,
    val monthlyBalance: BigDecimal = BigDecimal.ZERO,
    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<TransactionWithDetailUiModel> = emptyList(),
    val chartCategories: Map<CategoryExpenseSummaryUiModel, Float> = emptyMap(),
    val chartTotalAmount : BigDecimal = BigDecimal.ZERO
)
