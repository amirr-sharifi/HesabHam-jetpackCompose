package af.amir.hesabham.presentation.core.components.dialog.datepickerdialog

import af.amir.hesabham.presentation.core.components.dialog.wheel_picker.PickerItem


data class WheelDatePickerState(
    val selectedDayIndex: Int = 0,
    val selectedMonthIndex: Int = 0,
    val selectedYearIndex: Int = 0,
    val selectedHourIndex: Int = 0,
    val selectedMinuteIndex: Int = 0,
    val days: List<PickerItem> = emptyList(),
    val monthNames: List<PickerItem> = emptyList(),
    val years: List<PickerItem> = emptyList(),
    val hours: List<PickerItem> = emptyList(),
    val minutes: List<PickerItem> = emptyList(),
    val isLoading : Boolean = true
)
