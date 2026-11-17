package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppCurrency

class SetAppCurrencyUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(
        currency: AppCurrency,
    ) {
        settingsRepository.setCurrency(currency)

    }

}