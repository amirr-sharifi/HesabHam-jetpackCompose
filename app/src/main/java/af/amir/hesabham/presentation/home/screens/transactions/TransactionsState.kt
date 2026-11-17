package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.R
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.model.TransactionDayUiModel
import af.amir.hesabham.presentation.core.model.UiText

data class TransactionsState(
    val transactions: List<TransactionDayUiModel> = emptyList(),
    val isLoading: Boolean = true,

    val selectedFilterType: FilterBottomSheetTypes = FilterBottomSheetTypes.ALL,
    val formattedStartDate: String = "",
    val formattedEndDate: String = "",
    val startDate: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),
    val endDate: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),

    val tempFormattedStartDate: String = "",
    val tempFormattedEndDate: String = "",
    val tempStartDate: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),
    val tempEndDate: AppDateTime = AppDateTime(AppDate(0, 0, 0), 0, 0),

)



enum class FilterBottomSheetTypes(val title: UiText) {
    ALL(UiText.Resource(R.string.all)),
    EXPENSE(UiText.Resource(R.string.expense)),
    INCOME(UiText.Resource(R.string.income)),
    TRANSFER(UiText.Resource(R.string.transfer)),
}