package af.amir.hesabham.presentation.onboarding

import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import java.math.BigDecimal

data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.Welcome,
    val isNextButtonEnabled: Boolean = true,
    val selectedLocale: AppLocale = AppLocale.English,
    val selectedCalendar: AppCalendar = AppCalendar.Gregorian,
    val currencyList: List<CurrencyUiModel> = emptyList(),
    val selectedCurrency: CurrencyUiModel? = null,
    val isCurrencyDropDownExpanded: Boolean = false,
    val accountName: String = "",
    val accountType: AccountType = AccountType.CASH,
    val initialBalance: BigDecimal = BigDecimal("0"),
    val isLoadingForPreparing: Boolean = false,
    val canGoBack: Boolean = false,
)



