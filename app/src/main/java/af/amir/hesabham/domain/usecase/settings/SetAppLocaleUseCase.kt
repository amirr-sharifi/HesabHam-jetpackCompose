package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale

class SetAppLocaleUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(
        locale : AppLocale,
    ) {
        settingsRepository.apply {
            setLocale(locale)
        }
    }

}