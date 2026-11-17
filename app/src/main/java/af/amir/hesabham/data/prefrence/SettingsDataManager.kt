package af.amir.hesabham.data.prefrence

import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.settings.AppCurrency
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataManager(private val context: Context) {

    private val Context.settingsData by preferencesDataStore("HesabHam_Settings")

    private val currencyCodeKey = stringPreferencesKey(name = "currency_code")
    private val calendarKey = stringPreferencesKey(name = "calendar")
    private val localeKey = stringPreferencesKey(name = "locale")
    private val onboardingCompletedKey = booleanPreferencesKey(name = "isCompleted")
    private val darkModeKey = booleanPreferencesKey(name = "darkMode")

    fun getLocale(): Flow<AppLocale> {
        return context.settingsData.data.map {
            val localeName = it[localeKey] ?: AppLocale.English.name
            AppLocale.valueOf(localeName)
        }
    }

    suspend fun setLocale(locale: AppLocale) {
        context.settingsData.edit {
            it[localeKey] = locale.name
        }
    }

    fun getCalendar(): Flow<AppCalendar> {
        return context.settingsData.data.map {
            val name = it[calendarKey] ?: AppCalendar.Gregorian.name
            AppCalendar.valueOf(name)
        }
    }

    suspend fun setCalendar(calendar: AppCalendar) {
        context.settingsData.edit {
            it[calendarKey] = calendar.name
        }
    }

    fun getCurrency(): Flow<AppCurrency> {
        return context.settingsData.data.map {
            val code = it[currencyCodeKey] ?: "USD"
            AppCurrency(code)
        }
    }

    suspend fun setCurrency(currency: AppCurrency) {
        context.settingsData.edit {
            it[currencyCodeKey] = currency.code
        }
    }


    fun isOnboardingIsCompleted(): Flow<Boolean> {
        return context.settingsData.data.map {
            it[onboardingCompletedKey] ?: false
        }
    }

    suspend fun setOnboardingCompleted() {
        context.settingsData.edit {
            it[onboardingCompletedKey] = true
        }
    }

    fun observeDarkMode(): Flow<Boolean> {
        return context.settingsData.data.map {
            it[darkModeKey] ?: false
        }
    }

    suspend fun setDarkMode(darkMode: Boolean) {
        context.settingsData.edit {
            it[darkModeKey] = darkMode
        }
    }

}