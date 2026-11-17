package af.amir.hesabham.presentation.home.screens.settings

import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.provider.CurrencyListProvider
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.presentation.core.mapper.toUiModel
import af.amir.hesabham.presentation.core.model.UiText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(
    private val currencyListProvider: CurrencyListProvider,
    private val settingsUseCase: SettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<SettingsUiEffect>()
    val uiEffect: Flow<SettingsUiEffect> = _uiEffect.receiveAsFlow()

    private val settings =
        settingsUseCase.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            loadCurrencies()
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SelectCurrency -> {
                viewModelScope.launch {
                    settingsUseCase.setCurrency(AppCurrency(action.currency.code))
                }
                _uiState.update { it.copy(activeBottomSheet = null) }
            }

            is SettingsAction.SelectLanguage -> {
                viewModelScope.launch {
                    val locale = when (action.language) {
                        Locale("fa") -> AppLocale.Persian
                        else -> AppLocale.English
                    }
                    settingsUseCase.setLocale(locale)
                }
                _uiState.update { it.copy(activeBottomSheet = null) }
            }

            is SettingsAction.SelectCalendar -> {
                viewModelScope.launch {
                    settingsUseCase.setCalendar(action.calendar)
                }
                _uiState.update { it.copy(activeBottomSheet = null) }
            }

            is SettingsAction.ToggleDarkMode -> {
                viewModelScope.launch {
                    settingsUseCase.toggleDarkMode()
                }
                _uiState.update { it.copy(isDarkMode = action.isEnabled) }
            }

            is SettingsAction.OpenBottomSheet -> {
                _uiState.update { it.copy(activeBottomSheet = action.type) }
            }

            SettingsAction.DismissBottomSheet -> {
                _uiState.update { it.copy(activeBottomSheet = null) }
            }

            SettingsAction.OnBackClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(SettingsUiEffect.NavigateBack)
                }
            }
        }
    }

    private suspend fun loadCurrencies() {
        settings.filterNotNull().collect { appSetting ->
            val currencies =
                currencyListProvider.getALl().map { it.toUiModel(appSetting.locale.locale) }
                    .map {
                        SettingsScreenOption(
                            it,
                            UiText.Dynamic(it.displayName),

                            )
                    }
            _uiState.update { it.copy(currencies = currencies) }
        }

    }


}