package af.amir.hesabham.domain.repo

import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.settings.AppCurrency
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val localeFlow: Flow<AppLocale>
    val calendarFlow: Flow<AppCalendar>
    val currencyFlow: Flow<AppCurrency>
    val isOnboardingCompleted: Flow<Boolean>
    val observeDarkMode : Flow<Boolean>

    suspend fun setLocale(locale: AppLocale)
    suspend fun setCalendar(calendar: AppCalendar)
    suspend fun setCurrency(currency: AppCurrency)
    suspend fun setOnboardingCompleted()
    suspend fun setDarkMode(darkMode : Boolean)

}