package af.amir.hesabham.presentation.core.components.amountinput

import java.math.BigDecimal

data class AmountInputState(
    val rawAmount : String = "0",
    val formattedAmount : String = "0",
    val pendingOperand : BigDecimal? = null,
    val selectedOperator : MathOperator? = null
)

enum class MathOperator{
    Add,Subtract,Multiply,Divide
}

enum class AmountInputError{
    DecimalAlreadyExist, Unexpected,DivideByZero, NegativeAmountNotAllowed
}
