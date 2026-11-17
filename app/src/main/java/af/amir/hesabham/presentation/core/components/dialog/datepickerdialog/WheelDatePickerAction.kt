package af.amir.hesabham.presentation.core.components.dialog.datepickerdialog

sealed interface WheelDatePickerAction {
    data class ScrollFinished(val index: Int, val column: WheelDatePickerColumn) : WheelDatePickerAction
    object DoneClick: WheelDatePickerAction
}

enum class WheelDatePickerColumn {
    DAY, MONTH, YEAR, HOUR, MINUTE
}