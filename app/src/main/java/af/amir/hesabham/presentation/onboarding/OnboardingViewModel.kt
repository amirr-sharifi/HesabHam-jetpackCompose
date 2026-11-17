package af.amir.hesabham.presentation.onboarding

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.provider.CurrencyListProvider
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.usecase.account.CreateAccountUseCase
import af.amir.hesabham.domain.usecase.settings.SetupSettingsUseCase
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import af.amir.hesabham.presentation.core.mapper.toDomain
import af.amir.hesabham.presentation.core.mapper.toUiModel
import af.amir.hesabham.domain.util.generateStringId
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale

class OnboardingViewModel(
    private val currencyListProvider: CurrencyListProvider,
    private val setupSettingsUseCase: SetupSettingsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
) : ViewModel() {


    private val _state = MutableStateFlow(OnboardingState())

    val state = _state
        .onStart {
            loadCurrencies()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OnboardingState())
    private val _effects = Channel<OnboardingEffect>()

    val effect = _effects.receiveAsFlow()


    init {
        viewModelScope.launch {
            _state.collect { currentState ->
                val accountName = currentState.accountName.trim(' ')
                val isNextButtonEnabled = when (currentState.currentStep) {
                    OnboardingStep.Welcome -> true
                    OnboardingStep.LanguageCalendar -> true
                    OnboardingStep.Currency -> currentState.selectedCurrency != null
                    OnboardingStep.Account -> accountName != "" && accountName.isNotBlank()
                }
                val canGoBack = currentState.currentStep != OnboardingStep.Welcome
                _state.update {
                    it.copy(
                        isNextButtonEnabled = isNextButtonEnabled,
                        canGoBack = canGoBack,
                    )
                }
            }
        }
    }

    private suspend fun loadCurrencies() {

        val currencies = currencyListProvider.getALl().map { it.toUiModel(getCurrentLocale()) }
        _state.update { it.copy(currencyList = currencies) }

    }


    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.AccountBalanceChange -> onAccountBalanceChange(action.balance)
            is OnboardingAction.AccountNameChange -> onAccountNameChange(action.name)
            is OnboardingAction.AccountTypeChange -> onAccountTypeChange(action.type)
            is OnboardingAction.CurrencyChange -> onCurrencyChange(action.currency)
            OnboardingAction.NextStep -> nextStep()
            OnboardingAction.CloseCurrencyDropDown -> onCurrencyDropDownExpandedChange(false)
            OnboardingAction.OpenCurrencyDropDown -> onCurrencyDropDownExpandedChange(true)
            is OnboardingAction.CalendarChanged -> onCalendarChanged(action.calendar)
            is OnboardingAction.LocaleChanged -> onLocaleChanged(action.language)
            OnboardingAction.BackStep -> backStep()
        }
    }

    private fun onAccountTypeChange(type: AccountType) {
        _state.update { it.copy(accountType = type) }
    }

    private fun backStep() {
        val back = when (_state.value.currentStep) {
            OnboardingStep.Welcome -> OnboardingStep.Welcome
            OnboardingStep.LanguageCalendar -> OnboardingStep.Welcome
            OnboardingStep.Currency -> OnboardingStep.LanguageCalendar
            OnboardingStep.Account -> OnboardingStep.Currency
        }
        _state.update { it.copy(currentStep = back) }
    }

    private fun onLocaleChanged(locale: AppLocale) {
        if (_state.value.selectedLocale == locale) return
        viewModelScope.launch {
            _state.update { it.copy(selectedLocale = locale) }
            loadCurrencies()
        }
    }

    private fun onCalendarChanged(calendar: AppCalendar) {
        if (_state.value.selectedCalendar == calendar) return
        _state.update { it.copy(selectedCalendar = calendar) }
    }

    private fun onCurrencyDropDownExpandedChange(expanded: Boolean) {
        _state.update { it.copy(isCurrencyDropDownExpanded = expanded) }
    }

    private fun nextStep() {
        val newStep = when (_state.value.currentStep) {
            OnboardingStep.Welcome -> OnboardingStep.LanguageCalendar
            OnboardingStep.LanguageCalendar -> OnboardingStep.Currency
            OnboardingStep.Currency -> OnboardingStep.Account
            OnboardingStep.Account -> {
                setupSettings()
                OnboardingStep.Account
            }

        }
        _state.update { it.copy(currentStep = newStep) }
    }

    private fun setupSettings() {
        _state.update { it.copy(isLoadingForPreparing = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value
            setupSettingsUseCase(
                currentState.selectedLocale,
                currentState.selectedCalendar,
                currentState.selectedCurrency!!.toDomain(),
                true
            )
            createAccountUseCase(
                Account(
                    AccountId(generateStringId()),
                    currentState.accountName,
                    false,
                    currentState.accountType,
                    Money(BigDecimal.ZERO)
                ),
                Money(currentState.initialBalance),
            )
            _effects.send(OnboardingEffect.FinishScreen)
        }
    }

    private fun onCurrencyChange(currency: CurrencyUiModel) {
        _state.update { it.copy(selectedCurrency = currency) }
    }

    private fun onAccountNameChange(name: String) {
        _state.update { it.copy(accountName = name) }
    }

    private fun onAccountBalanceChange(balance: BigDecimal) {
        _state.update {
            it.copy(
                initialBalance = balance
            )
        }

    }

    private fun getCurrentLocale(): Locale {
        return _state.value.selectedLocale.locale
    }

}