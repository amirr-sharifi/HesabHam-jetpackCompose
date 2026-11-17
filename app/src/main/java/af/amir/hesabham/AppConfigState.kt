package af.amir.hesabham

import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.domain.model.settings.AppSetting

data class AppConfigState(
    val isLoading: Boolean = true,
    val setting: AppSetting = defaultAppSetting
)

internal val defaultAppSetting = AppSetting(
    isOnboardingCompleted = false,
    currency = AppCurrency("USD"),
    locale = AppLocale.English,
    calendar = AppCalendar.Gregorian,
    darkMode = false
)
