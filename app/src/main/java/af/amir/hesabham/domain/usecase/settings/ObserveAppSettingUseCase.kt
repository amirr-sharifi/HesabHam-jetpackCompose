package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.model.settings.AppSetting
import af.amir.hesabham.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveAppSettingUseCase(
    private val settingsRepo: SettingsRepository) {

    operator fun invoke() : Flow<AppSetting> {
        return combine(
            settingsRepo.localeFlow,
            settingsRepo.calendarFlow,
            settingsRepo.currencyFlow,
            settingsRepo.isOnboardingCompleted,
            settingsRepo.observeDarkMode
        ) { locale, calendar, currency, obCompleted,darkMode ->
            AppSetting(
                isOnboardingCompleted = obCompleted,
                currency = currency,
                locale = locale,
                calendar = calendar,
                darkMode = darkMode
            )
        }
    }
}