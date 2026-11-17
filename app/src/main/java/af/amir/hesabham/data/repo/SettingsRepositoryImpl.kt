package af.amir.hesabham.data.repo

import af.amir.hesabham.data.prefrence.SettingsDataManager
import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.settings.AppCurrency
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsData: SettingsDataManager,
) : SettingsRepository {
    override val localeFlow: Flow<AppLocale>
        get() = settingsData.getLocale()
    override val calendarFlow: Flow<AppCalendar>
        get() = settingsData.getCalendar()
    override val currencyFlow: Flow<AppCurrency>
        get() = settingsData.getCurrency()
    override val isOnboardingCompleted: Flow<Boolean>
        get() = settingsData.isOnboardingIsCompleted()
    override val observeDarkMode: Flow<Boolean>
        get() = settingsData.observeDarkMode()

    override suspend fun setLocale(locale: AppLocale) = settingsData.setLocale(locale)

    override suspend fun setCalendar(calendar: AppCalendar) = settingsData.setCalendar(calendar)

    override suspend fun setCurrency(currency: AppCurrency) = settingsData.setCurrency(currency)

    override suspend fun setOnboardingCompleted() = settingsData.setOnboardingCompleted()


    override suspend fun setDarkMode(darkMode: Boolean) = settingsData.setDarkMode(darkMode)
}