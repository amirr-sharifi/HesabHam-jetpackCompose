package af.amir.hesabham.presentation.home.screens.settings

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import af.amir.hesabham.presentation.core.model.UiText
import java.util.Locale


data class SettingsUiState(
    val currencies: List<SettingsScreenOption<CurrencyUiModel>> = emptyList(),
    val languages: List<SettingsScreenOption<Locale>> = languageList,
    val calendars: List<SettingsScreenOption<AppCalendar>> = calendarList,
    val isDarkMode: Boolean = false,
    val activeBottomSheet: BottomSheetType? = null
)


data class SettingsScreenOption<T>(
    val data : T,
    val title : UiText
)

val calendarList = AppCalendar.entries.map {
    val title = when (it) {
        AppCalendar.Gregorian -> UiText.Resource(R.string.Gregorian)
        AppCalendar.Jalali -> UiText.Resource(R.string.jalali)
    }
    SettingsScreenOption(
        it,
        title
    )
}

private val languageList = AppLocale.entries.map {
    val title = when(it){
        AppLocale.English -> "English"
        AppLocale.Persian -> "فارسی"
    }
    SettingsScreenOption(
        it.locale,
        UiText.Dynamic(title)
    )
}





sealed interface BottomSheetType {
    data object Currency : BottomSheetType
    data object Language : BottomSheetType
    data object Calendar : BottomSheetType
}


sealed interface SettingsAction {
    data class SelectCurrency(val currency: CurrencyUiModel) : SettingsAction
    data class SelectLanguage(val language: Locale) : SettingsAction
    data class SelectCalendar(val calendar: AppCalendar) : SettingsAction
    data class ToggleDarkMode(val isEnabled: Boolean) : SettingsAction
    data class OpenBottomSheet(val type: BottomSheetType) : SettingsAction
    data object DismissBottomSheet : SettingsAction
    data object OnBackClicked : SettingsAction
}


sealed interface SettingsUiEffect {
    data object NavigateBack : SettingsUiEffect
}