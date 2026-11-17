package af.amir.hesabham.presentation.onboarding

import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import java.math.BigDecimal

sealed interface OnboardingAction {
    data class CurrencyChange(val currency: CurrencyUiModel) : OnboardingAction
    data class AccountNameChange(val name: String) : OnboardingAction
    data class AccountBalanceChange(val balance: BigDecimal) : OnboardingAction
    object NextStep : OnboardingAction
    object OpenCurrencyDropDown : OnboardingAction
    object CloseCurrencyDropDown : OnboardingAction
    object BackStep : OnboardingAction
    data class LocaleChanged(val language: AppLocale) : OnboardingAction
    data class CalendarChanged(val calendar: AppCalendar) : OnboardingAction
    class AccountTypeChange(val type: AccountType) : OnboardingAction
}
