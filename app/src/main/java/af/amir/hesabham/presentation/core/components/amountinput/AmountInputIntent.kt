package af.amir.hesabham.presentation.core.components.amountinput

sealed interface AmountInputIntent {

    data class DigitPressed(val digit : String) : AmountInputIntent
    data class OperatorPressed(val operator: MathOperator) : AmountInputIntent
    object DecimalPressed : AmountInputIntent
    object EqualsPressed : AmountInputIntent
    object BackspacePressed : AmountInputIntent
    object DoubleZeroPressed : AmountInputIntent
    object ClearPressed : AmountInputIntent
    object DoneButtonClick : AmountInputIntent
    object CancelButtonClick : AmountInputIntent
}
