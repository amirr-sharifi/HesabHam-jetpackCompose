package af.amir.hesabham.presentation.onboarding

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.layoutDirectionFor
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.onboarding.components.AccountPageContent
import af.amir.hesabham.presentation.onboarding.components.CurrencyPageContent
import af.amir.hesabham.presentation.onboarding.components.LanguageCalendarContent
import af.amir.hesabham.presentation.onboarding.pages.OnboardingBasePage
import af.amir.hesabham.presentation.onboarding.pages.OnboardingWelcomePage
import af.amir.hesabham.presentation.util.ObserveAsEffects
import af.amir.hesabham.presentation.core.mapper.toUiModel
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel


@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, onFinishScreen: () -> Unit) {

    val viewModel = koinViewModel<OnboardingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currency  = remember(state.selectedCurrency,state.selectedLocale){
        state.selectedCurrency ?: AppCurrency("USD").toUiModel(state.selectedLocale.locale)
    }

    ObserveAsEffects(viewModel.effect) { onboardingEffect ->
        when (onboardingEffect) {
            OnboardingEffect.FinishScreen -> onFinishScreen()
        }
    }

    CompositionLocalProvider(
        LocalAppLocale provides state.selectedLocale.locale,
        LocalLayoutDirection provides layoutDirectionFor(state.selectedLocale),
        LocalAppCurrency provides currency
    ) {

        Scaffold(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) { paddingValues ->

            OnboardingScreenStateless(
                modifier = modifier.padding(paddingValues),
                state = state,
                onAction = viewModel::onAction
            )
        }


    }
}

@Composable
fun OnboardingScreenStateless(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {

    BackHandler(enabled = state.canGoBack) {
        onAction(OnboardingAction.BackStep)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedContent(
            state.currentStep,
            modifier = Modifier.weight(8f),
            label = "content"
        ) { step ->
            when (step) {
                OnboardingStep.Welcome -> OnboardingWelcomePage()
                OnboardingStep.LanguageCalendar -> OnboardingBasePage(headerImage = R.drawable.ob_calendar) {
                    LanguageCalendarContent(
                        selectedLocale = state.selectedLocale,
                        selectedCalendar = state.selectedCalendar,
                        onCalendarChanged = { onAction(OnboardingAction.CalendarChanged(it)) },
                        onLocaleChanged = { onAction(OnboardingAction.LocaleChanged(it)) })
                }

                OnboardingStep.Currency -> OnboardingBasePage(headerImage = R.drawable.ob_currency) {
                    CurrencyPageContent(
                        currencies = state.currencyList,
                        selectedCurrency = state.selectedCurrency
                    ) {
                        onAction(OnboardingAction.CurrencyChange(it))
                    }
                }

                OnboardingStep.Account -> OnboardingBasePage(headerImage = R.drawable.ob_account) {
                    AccountPageContent(
                        accountName = state.accountName,
                        accountType = state.accountType,
                        initialBalance = state.initialBalance,
                        onAccountNameChange = {
                            onAction(OnboardingAction.AccountNameChange(it))
                        },
                        onAccountTypeChange = {onAction(OnboardingAction.AccountTypeChange(it))},
                        onInitialBalanceChange = {
                            onAction(OnboardingAction.AccountBalanceChange(it))
                        })
                }
            }

        }
        Spacer(Modifier.height(8.dp))

        OnboardingFooter(state = state, onAction = onAction)


    }


}


@Composable
fun OnboardingFooter(state: OnboardingState, onAction: (OnboardingAction) -> Unit) {

    val appLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(appLocale)


    val title = when (state.currentStep) {
        OnboardingStep.Welcome -> resources.getString(R.string.action_get_started)
        OnboardingStep.Account -> resources.getString(R.string.action_start_tracking)
        else -> resources.getString(R.string.action_next)
    }

    AnimatedContent(state.isLoadingForPreparing, label = "footer") { isLoading ->

        if (isLoading) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Button(
                onClick = { onAction(OnboardingAction.NextStep) },
                shape = MaterialTheme.shapes.medium,
                enabled = state.isNextButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
            ) {
                Text(title)
            }
        }
    }

    Spacer(Modifier.height(16.dp))
}
