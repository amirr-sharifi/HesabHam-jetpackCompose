package af.amir.hesabham.domain.usecase.settings

import af.amir.hesabham.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.first

class ToggleAppDarkModeUseCase(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke() {
        val currentDarkModeState = settingsRepository.observeDarkMode.first()
        settingsRepository.setDarkMode(!currentDarkModeState)
    }


}