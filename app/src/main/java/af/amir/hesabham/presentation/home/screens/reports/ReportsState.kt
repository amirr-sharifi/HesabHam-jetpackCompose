package af.amir.hesabham.presentation.home.screens.reports

import af.amir.hesabham.R
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.tab_switch.TabSwitchItem
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.core.model.UiIcon
import af.amir.hesabham.presentation.core.model.UiText
import androidx.compose.ui.graphics.Color
import java.math.BigDecimal

data class ReportsState(
    val isLoading : Boolean = true,
    val timePeriodTabItems: List<TabSwitchItem> = defaultTabs,
    val selectedTabIndex: Int = ReportsTimePeriod.Month.ordinal,
    val customTimePeriodStart: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),
    val customTimePeriodEnd: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),
    val customFormattedTimePeriodStart: String ="",
    val customFormattedTimePeriodEnd: String = "",

    val timePeriodBalance: BigDecimal = BigDecimal.ZERO,
    val timePeriodIncome: BigDecimal = BigDecimal.ZERO,
    val timePeriodExpense: BigDecimal = BigDecimal.ZERO,

    val categories: List<CategoryReportItem> = emptyList(),
    val selectedCategoryIndex: Int? = null,

    val topExpenses: List<TransactionWithDetailUiModel> = emptyList(),
    val topIncomes: List<TransactionWithDetailUiModel> = emptyList(),

    )

internal val defaultTabs =
    ReportsTimePeriod.entries.map {
        TabSwitchItem(it.title, null)
    }

enum class ReportsTimePeriod(val title: UiText) {
    WEEK(UiText.Resource(R.string.week)), Month(UiText.Resource(R.string.month)),
    YEAR(UiText.Resource(R.string.year)), CUSTOM(UiText.Resource(R.string.custom)),
}


data class CategoryReportItem(
    val id: String,
    val name: UiText,
    val icon: UiIcon,
    val color: Color,
    val amount: BigDecimal,
    val percentage: Float,
    val transactionCount: Int,
    val dailyAverage: BigDecimal,
)
