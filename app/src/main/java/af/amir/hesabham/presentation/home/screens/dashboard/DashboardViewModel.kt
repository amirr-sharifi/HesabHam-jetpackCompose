package af.amir.hesabham.presentation.home.screens.dashboard

import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.usecase.account.AccountUseCase
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.presentation.core.mapper.toUiModel
import af.amir.hesabham.presentation.core.model.CategoryExpenseSummaryUiModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("OPT_IN_USAGE")
class DashboardViewModel(
    accountUseCase: AccountUseCase,
    transactionUseCase: TransactionUseCase,
    private val calendarEngineProvider: CalendarEngineProvider,
    setting: SettingsUseCase,
    context: Context,
) : ViewModel() {

    private val settings = setting.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val currentDateFlow = MutableStateFlow<CalendarDateHolder?>(null)


    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()


    private data class CalendarDateHolder(
        val calendar: AppCalendar,
        val date: AppDate,
    )

    private data class ChartProcessedData(
        val uiList: Map<CategoryExpenseSummaryUiModel, Float>,
        val totalAmount: BigDecimal,
    )

    init {

        settings
            .filterNotNull()
            .flatMapLatest { appSettings ->

                val calendar = appSettings.calendar
                val locale = appSettings.locale

                val calendarEngine = calendarEngineProvider.get(calendar)
                val currentDate =
                    currentDateFlow.value
                        ?.takeIf { it.calendar == calendar }
                        ?.date
                        ?: calendarEngine.startOfMonth(
                            calendarEngine.now().date
                        ).also { date ->
                            currentDateFlow.value =
                                CalendarDateHolder(calendar, date)
                        }





                currentDateFlow
                    .filterNotNull()
                    .filter { it.calendar == calendar }
                    .flatMapLatest { dateHolder ->



                        val startOfMonth =calendarEngine.startOfMonth(dateHolder.date)
                        val endOfMonth = calendarEngine.endOfMonth(dateHolder.date)
                        val startInstant =
                            calendarEngine.toInstant(startOfMonth)
                        val endInstant =
                            calendarEngine.toInstant(endOfMonth)

                        val recentTransactionsFlow = transactionUseCase.transactionWithRelations(8,startInstant,endInstant)
                            .map { list -> list.map { it.toUiModel(context,calendarEngine, locale) } }

                        val chartDataFlow =
                            transactionUseCase.categoryExpenseSummary(startInstant, endInstant)
                                .map { categorySummaryList ->
                                    val totalAmount =
                                        categorySummaryList.sumOf { it.totalAmount.amount }
                                    val processedList = if (categorySummaryList.size > 6) {
                                        val limitList = categorySummaryList.take(5).toMutableList()
                                        val otherAmount =
                                            categorySummaryList.drop(5)
                                                .sumOf { it.totalAmount.amount }
                                        val otherCount =
                                            categorySummaryList.drop(5)
                                                .sumOf { it.transactionCount}
                                        limitList.add(
                                            CategoryExpenseSummary(
                                                CategoryId("other"),
                                                null,
                                                "other",
                                                "#780606",
                                                "AccountBalanceWallet",
                                                Money(otherAmount),
                                                otherCount
                                            )
                                        )
                                        limitList
                                    } else {
                                        categorySummaryList
                                    }

                                    val uiMap = processedList.associate { summary ->
                                        summary.toUiModel(context) to calculatePercentage(
                                            totalAmount,
                                            summary.totalAmount.amount
                                        )
                                    }

                                    ChartProcessedData(uiList = uiMap, totalAmount = totalAmount)
                                }

                        combine(
                            accountUseCase.totalBalance(),
                            accountUseCase.get.observeAll(),
                            chartDataFlow,
                            recentTransactionsFlow,
                            transactionUseCase.monthlySummary(startInstant, endInstant),
                        ) { totalBalance, accounts, chartData, recentTransactions, monthlySummary ->

                            _state.value.copy(
                                selectedMonthName = calendarEngine.monthName(
                                    dateHolder.date.month,
                                    locale.locale
                                ),
                                totalBalance = totalBalance.amount,
                                accounts = accounts.filterNot { it.isArchive },
                                chartCategories = chartData.uiList,
                                chartTotalAmount = chartData.totalAmount,
                                recentTransactions = recentTransactions,
                                monthlyBalance = monthlySummary.income.amount.subtract(
                                    monthlySummary.expense.amount
                                ),
                                monthlyIncome = monthlySummary.income.amount,
                                monthlyExpense = monthlySummary.expense.amount
                            )
                        }
                    }


            }
            .flowOn(Dispatchers.Default)
            .onEach { newState ->
                _state.update { newState }
            }
            .launchIn(viewModelScope)


    }


    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.NextMonth -> onNextMonth()
            DashboardAction.PreviousMonth -> onPreviousMonth()
        }
    }

    private fun onPreviousMonth() {
        val current = currentDateFlow.value ?: return
        val engine = calendarEngineProvider.get(current.calendar)
        currentDateFlow.update {
            Log.e("TAG", "onPreviousMonth: $it", )
            it?.copy(date = engine.subMonths(it.date, 1))
        }
    }

    private fun onNextMonth() {
        val current = currentDateFlow.value ?: return
        val engine = calendarEngineProvider.get(current.calendar)
        currentDateFlow.update {
            it?.copy(date = engine.addMonths(it.date, 1))
        }
    }

    private fun calculatePercentage(total: BigDecimal, amount: BigDecimal): Float {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0f
        return amount
            .divide(total, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
            .toFloat()
    }

}