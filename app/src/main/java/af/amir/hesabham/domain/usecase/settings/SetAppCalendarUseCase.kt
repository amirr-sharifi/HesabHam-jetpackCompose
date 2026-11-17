package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.model.settings.AppCalendar

class SetAppCalendarUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(
        calendar: AppCalendar,
    ) {
        settingsRepository.setCalendar(calendar)
    }


}