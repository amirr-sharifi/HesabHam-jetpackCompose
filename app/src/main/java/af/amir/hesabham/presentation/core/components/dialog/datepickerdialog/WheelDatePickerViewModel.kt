package af.amir.hesabham.presentation.core.components.dialog.datepickerdialog

import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.framework.datetime.CalendarEngine
import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.dialog.wheel_picker.PickerItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class WheelDatePickerViewModel(
    private val setting: SettingsUseCase,
    private val calendarEngineProvider: CalendarEngineProvider,
) : ViewModel() {

    private val settings = setting.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _state = MutableStateFlow(WheelDatePickerState())
    val state = _state.asStateFlow()

    private val _uiEffect = Channel<AppDateTime>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    fun setInitialDate(date: AppDateTime) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val appSetting = settings.filterNotNull().first()


            val calendarEngine = calendarEngineProvider.get(appSetting.calendar)

            val yearRange = getYearRange(date.date)
            val selectedYear = yearRange.indexOfFirst { it.value == date.date.year }

            val monthNames = getMonthsName(calendarEngine, appSetting.locale.locale)
            val selectedMonth = monthNames.indexOfFirst { it.value == date.date.month }

            val dayOfMonth = calculateDayOfMonths(
                calendarEngine,
                date.date.month,
                date.date.year
            )
            val selectedDay = dayOfMonth.indexOfFirst { it.value == date.date.day }

            val hours = getHours(appSetting.locale.locale)
            val selectedHour = hours.indexOfFirst { it.value == date.hour }

            val minutes = getMinutes(appSetting.locale.locale)
            val selectedMinute = minutes.indexOfFirst { it.value == date.minute }


            _state.update {
                it.copy(
                    days = dayOfMonth,
                    monthNames = monthNames,
                    years = yearRange,
                    hours = hours,
                    minutes = minutes,
                    selectedDayIndex = selectedDay,
                    selectedMonthIndex = selectedMonth,
                    selectedYearIndex = selectedYear,
                    selectedHourIndex = selectedHour,
                    selectedMinuteIndex = selectedMinute,
                    isLoading = false
                )
            }
        }


    }

    private fun calculateDayOfMonths(
        calendarEngine: CalendarEngine,
        month: Int,
        year: Int,
    ): List<PickerItem> {
        return (1..calendarEngine.daysInMonth(year, month)).map {
            PickerItem(it, it.toString())
        }
    }


    fun onAction(action: WheelDatePickerAction) {
        when (action) {
            WheelDatePickerAction.DoneClick -> onDoneClick()
            is WheelDatePickerAction.ScrollFinished -> onScrollFinished(action.index, action.column)
        }
    }

    private fun onScrollFinished(index: Int, column: WheelDatePickerColumn) {
        when (column) {
            WheelDatePickerColumn.DAY -> {
                _state.update { it.copy(selectedDayIndex = index) }
            }

            WheelDatePickerColumn.MONTH -> {
                viewModelScope.launch {
                    val calendar = setting.observe().map { it.calendar }.first()
                    val calendarEngine = calendarEngineProvider.get(calendar)
                    val month = _state.value.monthNames[index].value
                    val year = _state.value.years[_state.value.selectedYearIndex].value
                    val newDaysOfMonth = calculateDayOfMonths(calendarEngine, month, year)
                    _state.update { it.copy(selectedMonthIndex = index, days = newDaysOfMonth) }
                }


            }

            WheelDatePickerColumn.YEAR -> {
                viewModelScope.launch {
                    val calendar = setting.observe().map { it.calendar }.first()
                    val calendarEngine = calendarEngineProvider.get(calendar)
                    val month = _state.value.monthNames[_state.value.selectedMonthIndex].value
                    val year = _state.value.years[index].value
                    val newDaysOfMonth = calculateDayOfMonths(calendarEngine, month, year)
                    _state.update { it.copy(selectedYearIndex = index, days = newDaysOfMonth) }
                }
            }

            WheelDatePickerColumn.HOUR -> {
                _state.update { it.copy(selectedHourIndex = index) }
            }

            WheelDatePickerColumn.MINUTE -> {
                _state.update { it.copy(selectedMinuteIndex = index) }
            }
        }
    }

    private fun onDoneClick() {
        val currentState = _state.value
        val day = currentState.days[currentState.selectedDayIndex].value
        val month = currentState.monthNames[currentState.selectedMonthIndex].value
        val year = currentState.years[currentState.selectedYearIndex].value
        val hour = currentState.hours[currentState.selectedHourIndex].value
        val minute = currentState.minutes[currentState.selectedMinuteIndex].value

        val selectedDate = AppDateTime(
            date = AppDate(day, month, year),
            hour, minute
        )
        viewModelScope.launch {
            _uiEffect.send(selectedDate)
        }
    }


    private fun getMonthsName(calendarEngine: CalendarEngine, locale: Locale): List<PickerItem> {
        return (1..12).map {
            val monthName = calendarEngine.monthName(it, locale)
            PickerItem(it, monthName)
        }


    }


    private fun getYearRange(currentDate: AppDate): List<PickerItem> {
        val years = (currentDate.year - 5..currentDate.year + 5)
        return years.map { year ->
            PickerItem(
                value = year,
                label = year.toString()
            )
        }
    }

    private fun getHours(locale: Locale): List<PickerItem> {
        return (0..23).map {
            val formatted = String.format(locale, "%02d", it)
            PickerItem(
                value = it,
                formatted
            )
        }
    }

    private fun getMinutes(locale: Locale): List<PickerItem> {
        return (0..59).map {
            val formatted = String.format(locale, "%02d", it)
            PickerItem(
                value = it,
                formatted
            )
        }
    }
}