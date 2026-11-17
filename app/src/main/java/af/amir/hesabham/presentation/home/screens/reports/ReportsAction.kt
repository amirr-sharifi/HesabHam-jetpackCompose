package af.amir.hesabham.presentation.home.screens.reports

import af.amir.hesabham.framework.datetime.model.AppDateTime

sealed interface ReportsAction {
    data class OnTabIndexChanged(val index : Int) : ReportsAction
    data class SelectCategory(val newIndex: Int?) : ReportsAction
    data class OnCustomRangeStartPicked(val dateTime: AppDateTime) : ReportsAction
    data class OnCustomRangeEndPicked(val dateTime: AppDateTime) : ReportsAction
}