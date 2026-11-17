package af.amir.hesabham.domain.usecase.settings

data class SettingsUseCase(
    val observe : ObserveAppSettingUseCase,
    val setupSettings : SetupSettingsUseCase,
    val setLocale : SetAppLocaleUseCase,
    val setCurrency : SetAppCurrencyUseCase,
    val setCalendar : SetAppCalendarUseCase,
    val toggleDarkMode : ToggleAppDarkModeUseCase
) {
}