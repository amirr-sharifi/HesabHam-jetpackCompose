package af.amir.hesabham.presentation.home.screens.reports

import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.framework.datetime.CalendarEngine
import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.mapper.toUiModel
import af.amir.hesabham.presentation.core.model.TopTransactionsUiModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    setting: SettingsUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val calendarEngineProvider: CalendarEngineProvider,
    context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()


    private val _uiEffect = Channel<ReportsEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private val settings = setting.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val currentDateFlow = MutableStateFlow<CalendarDateHolder?>(null)


    private data class CalendarDateHolder(
        val calendar: AppCalendar,
        val start: AppDate,
        val end: AppDate,
    )


    init {

        settings
            .filterNotNull()
            .flatMapLatest { appSetting ->
                val calendar = appSetting.calendar
                val locale = appSetting.locale

                val calendarEngine = calendarEngineProvider.get(calendar)


                val currentTimePeriod = ReportsTimePeriod.entries[_state.value.selectedTabIndex]
                val (start, end) = calculateStartAndEnd(currentTimePeriod, calendarEngine)


                currentDateFlow.value = CalendarDateHolder(calendar, start, end)



                currentDateFlow
                    .filterNotNull()
                    .flatMapLatest { dateHolder ->
                        _state.update {
                            it.copy(
                                isLoading = true,
                                customTimePeriodStart = AppDateTime(dateHolder.start, 0, 0),
                                customTimePeriodEnd = AppDateTime(dateHolder.end, 0, 0),
                                customFormattedTimePeriodStart = calendarEngine.formatDate(
                                    dateHolder.start,
                                    locale.locale
                                ),
                                customFormattedTimePeriodEnd = calendarEngine.formatDate(
                                    dateHolder.end,
                                    locale.locale
                                )
                            )
                        }


                        val startInstant = calendarEngine.toInstant(dateHolder.start)
                        val endInstant = calendarEngine.toInstant(dateHolder.end)


                        val days = calendarEngine.getDaysBetween(startInstant, endInstant)


                        val chartDataFlow =
                            transactionUseCase.categoryExpenseSummary(startInstant, endInstant)
                                .map { categorySummaryList ->
                                    val limit = 7
                                    val totalAmount =
                                        categorySummaryList.sumOf { it.totalAmount.amount }
                                    val processedList = if (categorySummaryList.size > 6) {
                                        val limitList =
                                            categorySummaryList.take(limit).toMutableList()
                                        val otherAmount =
                                            categorySummaryList.drop(limit)
                                                .sumOf { it.totalAmount.amount }
                                        val otherCount =
                                            categorySummaryList.drop(limit)
                                                .sumOf { it.transactionCount }
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

                                    processedList.map { categorySummary ->
                                        val uiModel = categorySummary.toUiModel(context)
                                        CategoryReportItem(
                                            id = uiModel.categoryId,
                                            name = uiModel.title,
                                            icon = uiModel.icon,
                                            color = uiModel.color,
                                            amount = uiModel.totalAmount,
                                            percentage = calculatePercentage(
                                                totalAmount,
                                                uiModel.totalAmount
                                            ),
                                            transactionCount = categorySummary.transactionCount,
                                            dailyAverage = uiModel.totalAmount.divide(
                                                BigDecimal.valueOf(
                                                    days
                                                ), 2, RoundingMode.HALF_UP
                                            )
                                        )
                                    }
                                }


                        val topTransactions =
                            transactionUseCase.topTransactions(startInstant, endInstant)
                                .map {
                                    val uiExpenses =
                                        it.expense.map { expense -> expense.toUiModel(context,calendarEngine, locale) }
                                    val uiIncomes =
                                        it.income.map { income -> income.toUiModel(context,calendarEngine, locale) }
                                    TopTransactionsUiModel(uiExpenses, uiIncomes)
                                }

                        combine(
                            transactionUseCase.monthlySummary(startInstant, endInstant),
                            chartDataFlow,
                            topTransactions
                        ) { balanceSummary, chartData, topTransactionData ->


                            val balance =
                                balanceSummary.income.amount.subtract(balanceSummary.expense.amount)
                            _state.value.copy(
                                timePeriodBalance = balance,
                                timePeriodIncome = balanceSummary.income.amount,
                                timePeriodExpense = balanceSummary.expense.amount,
                                categories = chartData,
                                topIncomes = topTransactionData.incomes,
                                topExpenses = topTransactionData.expenses,
                                isLoading = false
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


    fun onAction(action: ReportsAction) {
        when (action) {
            is ReportsAction.OnTabIndexChanged -> onTimePeriodTabIndexChanged(action.index)
            is ReportsAction.SelectCategory -> onSelectCategory(action.newIndex)
            is ReportsAction.OnCustomRangeEndPicked -> onCustomRangeEndPicked(action.dateTime)
            is ReportsAction.OnCustomRangeStartPicked -> onCustomRangeStartPicked(action.dateTime)
        }
    }

    private fun onCustomRangeStartPicked(dateTime: AppDateTime) {
        settings.value?.let { settings ->
            val calendarEngine = calendarEngineProvider.get(settings.calendar)
            val currentState = _state.value
            if (isDateRangeInvalid(
                    dateTime.date,
                    currentState.customTimePeriodEnd.date
                )
            ) {
                viewModelScope.launch {
                    _uiEffect.send(ReportsEffect.ShowDateRangeError)
                }
                return
            }
            _state.update {
                it.copy(
                    customFormattedTimePeriodStart = calendarEngine.formatDate(
                        dateTime.date,
                        settings.locale.locale
                    ),
                    customTimePeriodStart = dateTime
                )
            }
            currentDateFlow.update {
                it?.copy(
                    start = dateTime.date,
                    end = currentState.customTimePeriodEnd.date
                )
            }

        }
    }

    private fun onCustomRangeEndPicked(dateTime: AppDateTime) {
        settings.value?.let { settings ->
            val calendarEngine = calendarEngineProvider.get(settings.calendar)
            val currentState = _state.value
            if (isDateRangeInvalid(
                    currentState.customTimePeriodStart.date,
                    dateTime.date
                )
            ) {
                viewModelScope.launch {
                    _uiEffect.send(ReportsEffect.ShowDateRangeError)
                }
                return
            }
            _state.update {
                it.copy(
                    customFormattedTimePeriodEnd = calendarEngine.formatDate(
                        dateTime.date,
                        settings.locale.locale
                    ),
                    customTimePeriodEnd = dateTime
                )
            }
            currentDateFlow.update {
                it?.copy(
                    start = currentState.customTimePeriodStart.date,
                    end = dateTime.date
                )
            }
        }
    }

    private fun onSelectCategory(newIndex: Int?) {
        _state.update { it.copy(selectedCategoryIndex = newIndex) }
    }

    private fun onTimePeriodTabIndexChanged(index: Int) {
        if (settings.value == null) return
        val calendarEngine = calendarEngineProvider.get(settings.value!!.calendar)
        val newTimePeriod = ReportsTimePeriod.entries[index]
        val (start, end) = calculateStartAndEnd(newTimePeriod, calendarEngine)
        _state.update { it.copy(selectedTabIndex = index) }
        currentDateFlow.update { it?.copy(start = start, end = end) }
    }


    private fun calculateStartAndEnd(
        timePeriod: ReportsTimePeriod,
        calendarEngine: CalendarEngine,
    ): Pair<AppDate, AppDate> {
        val today = calendarEngine.now().date
        return when (timePeriod) {
            ReportsTimePeriod.WEEK -> {
                val startOfWeek = calendarEngine.startOfWeek(today)
                val endOfWeek = calendarEngine.endOfWeek(today)
                Pair(startOfWeek, endOfWeek)
            }

            ReportsTimePeriod.Month -> {
                val startOfMonth = calendarEngine.startOfMonth(today)
                val endOfMonth = calendarEngine.endOfMonth(today)
                _state.update {
                    it.copy(
                        customTimePeriodStart = AppDateTime(startOfMonth, 0, 0),
                        customTimePeriodEnd = AppDateTime(endOfMonth, 23, 59)
                    )
                }
                Pair(startOfMonth, endOfMonth)
            }

            ReportsTimePeriod.YEAR -> {
                val startOfYear = calendarEngine.startOfYear(today)
                val endOfYear = calendarEngine.endOfYear(today)
                Pair(startOfYear, endOfYear)
            }

            ReportsTimePeriod.CUSTOM -> {
                Pair(
                    _state.value.customTimePeriodStart.date,
                    _state.value.customTimePeriodEnd.date
                )
            }
        }
    }


    private fun calculatePercentage(total: BigDecimal, amount: BigDecimal): Float {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0f
        return amount
            .divide(total, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
            .toFloat()
    }


    private fun isDateRangeInvalid(from: AppDate, to: AppDate): Boolean {
        return when {
            from.year > to.year -> true
            from.year < to.year -> false
            from.month > to.month -> true
            from.month < to.month -> false
            else -> from.day > to.day
        }
    }

}