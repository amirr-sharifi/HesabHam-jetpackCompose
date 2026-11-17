package af.amir.hesabham.presentation.home.screens.reports


import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.tab_switch.TabSwitch
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.home.screens.reports.components.CustomDateRangeCard
import af.amir.hesabham.presentation.home.screens.reports.components.FinancialOverviewCard
import af.amir.hesabham.presentation.home.screens.reports.components.TopTransactionsSection
import af.amir.hesabham.presentation.home.screens.reports.components.UnifiedCategoryDistributionCard
import af.amir.hesabham.presentation.util.ObserveAsEffects
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportsScreen() {
    val viewModel = koinViewModel<ReportsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReportsStatelessScreen(
        state,
        viewModel.uiEffect,
        viewModel::onAction,
    )
}

@Composable
private fun ReportsStatelessScreen(
    newState: ReportsState,
    uiEffect: Flow<ReportsEffect>,
    onAction: (ReportsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    val context = LocalContext.current
    ObserveAsEffects(uiEffect) { effect ->
        when (effect) {
            ReportsEffect.ShowDateRangeError -> Toast.makeText(
                context,
                resources.getString(R.string.invalid_date_range), Toast.LENGTH_SHORT
            ).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TabSwitch(
                modifier = Modifier
                    .fillMaxWidth(),
                tabs = newState.timePeriodTabItems,
                selectedTabIndex = newState.selectedTabIndex,
                onTabSelected = { onAction(ReportsAction.OnTabIndexChanged(it)) })
        }
        if (newState.isLoading) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            item {
                AnimatedVisibility(
                    visible = newState.selectedTabIndex == ReportsTimePeriod.CUSTOM.ordinal,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CustomDateRangeCard (
                        formattedStartDate = newState.customFormattedTimePeriodStart,
                        formattedEndDate = newState.customFormattedTimePeriodEnd,
                        startDate = newState.customTimePeriodStart,
                        endDate = newState.customTimePeriodEnd,
                        onStartDatePicked = { onAction(ReportsAction.OnCustomRangeStartPicked(it)) },
                        onEndDatePicked = { onAction(ReportsAction.OnCustomRangeEndPicked(it)) }
                    )
                }
            }

            item {
                FinancialOverviewCard(
                    balance = newState.timePeriodBalance,
                    income = newState.timePeriodIncome,
                    expense = newState.timePeriodExpense,
                )
            }


            item {
                UnifiedCategoryDistributionCard(
                    categories = newState.categories,
                    totalExpense = newState.timePeriodExpense,
                    selectedIndex = newState.selectedCategoryIndex,
                    onCategorySelect = { onAction(ReportsAction.SelectCategory(it)) },
                )
            }

            item {
                TopTransactionsSection(newState.topExpenses, newState.topIncomes)
            }

        }


    }
}










