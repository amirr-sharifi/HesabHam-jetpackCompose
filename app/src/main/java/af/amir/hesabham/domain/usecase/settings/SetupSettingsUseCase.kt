package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.domain.model.settings.AppLocale

class SetupSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(
        locale: AppLocale,
        calendar: AppCalendar,
        currency: AppCurrency,
        isOnboardingComplete: Boolean,
    ) {
        settingsRepository.apply {
            setLocale(locale)
            setCurrency(currency)
            setCalendar(calendar)
            if (isOnboardingComplete)
                setOnboardingCompleted()
        }
    }

}