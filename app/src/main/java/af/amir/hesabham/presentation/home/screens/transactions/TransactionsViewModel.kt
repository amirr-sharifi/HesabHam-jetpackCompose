package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModel(
    private val transactionUseCase: TransactionUseCase,
    private val calendarEngineProvider: CalendarEngineProvider,
    setting: SettingsUseCase,
    private val uiMapper: TransactionsUiMapper,
) : ViewModel() {

    private val settings = setting.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    private var tempDeleteTransactionId: String? = null

    private val dateRangeFlow = MutableStateFlow<DateRange?>(null)
    private val filterTypeFlow = MutableStateFlow(FilterBottomSheetTypes.ALL)

    private val _state = MutableStateFlow(TransactionsState())
    val state = _state.asStateFlow()

    private val _uiEffect = Channel<TransactionsEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeTransactions()
    }

    fun onAction(action: TransactionsAction) {
        when (action) {
            TransactionsAction.OnAddTransactionClick ->
                viewModelScope.launch {
                    _uiEffect.send(TransactionsEffect.NavigateToAddTransaction(null))
                }


            is TransactionsAction.OnTransactionClick -> {
                viewModelScope.launch {
                    _uiEffect.send(
                        TransactionsEffect.OnTransactionClick(action.transaction)
                    )
                }
            }

            is TransactionsAction.SaveFilterState -> onSaveFilterState(action.filter)

            is TransactionsAction.StartTimePicked -> updateTempDate(
                isStart = true,
                date = action.date
            )

            is TransactionsAction.EndTimePicked -> updateTempDate(
                isStart = false,
                date = action.date
            )

            TransactionsAction.OnSettingsClick -> prepareFilterState()
            is TransactionsAction.OnEditTransactionClick ->
                viewModelScope.launch {
                    _uiEffect.send(TransactionsEffect.NavigateToAddTransaction(action.transactionId))
                }

            is TransactionsAction.OnShowDeleteDialog -> onShowDeleteDialog(action.transactionId)
            TransactionsAction.OnDeleteTransaction -> {
                viewModelScope.launch {
                    tempDeleteTransactionId?.let { tId ->
                        transactionUseCase.deleteTransaction(TransactionId(tId), onSuccess = {
                            viewModelScope.launch {
                                _uiEffect.send(TransactionsEffect.CloseDetailBottomSheet)
                            }
                        }, onError = {})

                        tempDeleteTransactionId = null
                    }
                }
            }
        }
    }

    private fun onShowDeleteDialog(transactionId: String) {
        tempDeleteTransactionId = transactionId
        viewModelScope.launch {
            _uiEffect.send(TransactionsEffect.ShowDeleteDialog)
        }
    }

    private fun updateTempDate(isStart: Boolean, date: AppDateTime) {
        val appSetting = settings.value ?: return
        val calendarEngine = calendarEngineProvider.get(appSetting.calendar)
        val formatted = calendarEngine.formatDate(date.date, appSetting.locale.locale)

        _state.update { current ->
            if (isStart) {
                current.copy(tempStartDate = date, tempFormattedStartDate = formatted)
            } else {
                current.copy(tempEndDate = date, tempFormattedEndDate = formatted)
            }
        }
    }


    private fun prepareFilterState() {
        _state.update { current ->
            current.copy(
                tempStartDate = current.startDate,
                tempEndDate = current.endDate,
                tempFormattedEndDate = current.formattedEndDate,
                tempFormattedStartDate = current.formattedStartDate,
            )
        }
    }

    private fun onSaveFilterState(filter: FilterBottomSheetTypes) {
        val currentState = _state.value
        if (isDateRangeInvalid(currentState.tempStartDate.date, currentState.tempEndDate.date)) {
            viewModelScope.launch {
                _uiEffect.send(TransactionsEffect.ShowDateRangeError)
            }
            return
        }

        _state.update { current ->
            current.copy(
                startDate = current.tempStartDate,
                endDate = current.tempEndDate,
                formattedEndDate = current.tempFormattedEndDate,
                formattedStartDate = current.tempFormattedStartDate,
                selectedFilterType = filter,
            )
        }

        val newRange = DateRange(
            from = currentState.tempStartDate.date,
            to = currentState.tempEndDate.date,
        )

        if (dateRangeFlow.value != newRange) {
            dateRangeFlow.value = newRange
        }
        filterTypeFlow.value = filter
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


    private fun observeTransactions() {
        val rawTransactionsFlow = settings
            .filterNotNull()
            .flatMapLatest { appSettings ->
                val calendarEngine = calendarEngineProvider.get(appSettings.calendar)

                val today = calendarEngine.now().date
                dateRangeFlow.value = DateRange(
                    from = calendarEngine.startOfMonth(today),
                    to = calendarEngine.endOfMonth(today),
                )


                dateRangeFlow
                    .filterNotNull()
                    .flatMapLatest { range ->
                        val startInstant = calendarEngine.toInstant(range.from)
                        val endInstant = calendarEngine.toInstant(
                            AppDateTime(date = range.to, hour = 23, minute = 59)
                        )

                        transactionUseCase
                            .transactionWithRelations(startInstant, endInstant)
                            .map { list ->
                                RawTransactionsResult(
                                    transactions = list,
                                    calendarEngine = calendarEngine,
                                    locale = appSettings.locale,
                                    range = range,
                                )
                            }
                            .onStart {
                                _state.update { it.copy(isLoading = true) }
                            }
                            .catch {
                                _state.update { it.copy(isLoading = false) }
                            }
                    }
            }

        combine(rawTransactionsFlow, filterTypeFlow) { raw, filter ->
            uiMapper.map(raw, filter)
        }
            .flowOn(Dispatchers.Default)
            .onEach { newState -> _state.value = newState }
            .launchIn(viewModelScope)
    }
}
