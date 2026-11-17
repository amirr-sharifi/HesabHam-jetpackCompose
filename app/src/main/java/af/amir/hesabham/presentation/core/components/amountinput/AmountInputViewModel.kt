package af.amir.hesabham.presentation.core.components.amountinput

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class AmountInputViewModel(
    private val currencyFractionDigits: Int,
) : ViewModel() {

    private var inputPhase = AmountInputPhase.Idle

    private val _state = MutableStateFlow(AmountInputState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AmountInputState())


    private val _effects = Channel<AmountInputEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()


    private fun sendEffect(effect: AmountInputEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }

    fun setAmount(amount: BigDecimal) {
        if (amount != BigDecimal.ZERO) inputPhase = AmountInputPhase.WaitingForOperator
        _state.update {
            it.copy(
                rawAmount = amount.toPlainString(),
                formattedAmount = rawToDisplay(amount.toPlainString())
            )
        }

    }

    fun onIntent(intent: AmountInputIntent) {
        when (intent) {
            AmountInputIntent.BackspacePressed -> onBackspacePressed()
            AmountInputIntent.DecimalPressed -> onDecimalPressed()
            is AmountInputIntent.DigitPressed -> onDigitPressed(intent.digit)
            AmountInputIntent.DoubleZeroPressed -> onDoubleZeroPressed()
            is AmountInputIntent.OperatorPressed -> onOperatorPressed(intent.operator)
            AmountInputIntent.EqualsPressed -> onEqualsPressed()
            AmountInputIntent.ClearPressed -> reset()
            AmountInputIntent.CancelButtonClick -> sendEffect(AmountInputEffect.DismissBottomSheet)
            AmountInputIntent.DoneButtonClick -> onConfirmAmount()
        }
    }

    private fun onConfirmAmount() {
        if (_state.value.pendingOperand != null && _state.value.selectedOperator != null) {
            onEqualsPressed()
        }
        if (getRawAmount().startsWith('-')) {
            sendEffect(AmountInputEffect.ShowError(AmountInputError.NegativeAmountNotAllowed))
            return
        }
        val result = removeTrailingZeros(getRawAmount())
        val amount = result.toBigDecimal().setScale(currencyFractionDigits, RoundingMode.HALF_EVEN)
        reset()
        sendEffect(AmountInputEffect.ConfirmAmount(amount))
    }

    private fun removeTrailingZeros(input: String): String {
        return when {
            !input.contains('.') -> input
            else -> {
                val trimmed = input.trimEnd('0')
                if (trimmed.endsWith('.')) trimmed.dropLast(1) else trimmed
            }
        }
    }

    private fun calculate(
        firstOperand: BigDecimal,
        operator: MathOperator,
        secondOperand: BigDecimal,
    ): BigDecimal {
        return when (operator) {
            MathOperator.Add -> firstOperand.add(secondOperand)
            MathOperator.Subtract -> firstOperand.subtract(secondOperand)
            MathOperator.Multiply -> firstOperand.multiply(secondOperand)
            MathOperator.Divide -> firstOperand.divide(
                secondOperand,
                8,
                RoundingMode.HALF_EVEN
            )
        }
    }

    private fun onEqualsPressed() {
        if (_state.value.pendingOperand == null || _state.value.selectedOperator == null) {
            return
        }

        if (_state.value.selectedOperator == MathOperator.Divide) {
            val amount = removeTrailingZeros(getRawAmount())
            if (amount == "0") {
                sendEffect(AmountInputEffect.ShowError(AmountInputError.DivideByZero))
                return
            }
        }

        if (getRawAmount().endsWith(".")) updateRawAmount { it.dropLast(1) }

        val secondOperand = getRawAmount().toBigDecimal()
        val calculateResult =
            calculate(_state.value.pendingOperand!!, _state.value.selectedOperator!!, secondOperand)
        val normalizedResult = removeTrailingZeros(calculateResult.toPlainString())

        _state.update {
            it.copy(
                rawAmount = normalizedResult,
                formattedAmount = rawToDisplay(normalizedResult),
                pendingOperand = null,
                selectedOperator = null
            )
        }


    }


    private fun onOperatorPressed(operator: MathOperator) {
        if (inputPhase != AmountInputPhase.WaitingForOperator) {
            _state.update { it.copy(selectedOperator = operator) }
            return
        }

        if (getRawAmount().endsWith(".")) updateRawAmount { it + "0" }
        if (_state.value.selectedOperator != null && _state.value.pendingOperand != null) {
            onEqualsPressed()
        }
        _state.update {
            it.copy(
                pendingOperand = getRawAmount().toBigDecimal(),
                selectedOperator = operator
            )
        }
        inputPhase = AmountInputPhase.WaitingForSecondOperand
    }

    private fun onDoubleZeroPressed() {
        prepareForNewInputIfNeeded()

        if (getRawAmount().length == 1 && getRawAmount().startsWith("0")) return
        updateRawAmount { it + "00" }
    }

    private fun onDigitPressed(digit: String) {
        prepareForNewInputIfNeeded()

        if (getRawAmount().length == 1 && getRawAmount().startsWith("0")) {
            updateRawAmount { digit }
            return
        }
        updateRawAmount { it + digit }
    }

    private fun onDecimalPressed() {
        prepareForNewInputIfNeeded()

        if (getRawAmount().contains(".")) {
            sendEffect(AmountInputEffect.ShowError(AmountInputError.DecimalAlreadyExist))
            return
        }
        updateRawAmount { "$it." }
    }

    private fun onBackspacePressed() {
        if (getRawAmount() == "" || getRawAmount().isBlank()) return
        if (getRawAmount().length == 1) {
            resetRawAmount()
            return
        }
        updateRawAmount { it.dropLast(1) }
    }

    private fun getRawAmount(): String = _state.value.rawAmount


    private fun prepareForNewInputIfNeeded() {
        if (inputPhase == AmountInputPhase.Idle) return
        if (inputPhase == AmountInputPhase.WaitingForSecondOperand) {
            resetRawAmount()
        }
    }

    private fun resetRawAmount() {
        updateRawAmount { "0" }
        updateDisplayAmount()
    }

    private fun reset() {
        _state.value = AmountInputState()
        inputPhase = AmountInputPhase.Idle
    }

    private fun updateRawAmount(update: (String) -> String) {
        inputPhase = AmountInputPhase.WaitingForOperator
        _state.update { it.copy(rawAmount = update(getRawAmount())) }
        updateDisplayAmount()
    }

    private fun updateDisplayAmount() {
        _state.update { it.copy(formattedAmount = rawToDisplay(getRawAmount())) }
    }

    private fun formatThousands(input: String): String {
        val isNegative = input.startsWith("-")
        val pureNumber = if (isNegative) input.drop(1) else input
        return if (input.length <= 3) input else {
            val formatted = pureNumber
                .reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()
            if (isNegative) "-$formatted" else formatted
        }
    }

    private fun rawToDisplay(rawInput: String): String {
        val hasDot = rawInput.contains(".")
        val parts = rawInput.split(".")

        val integerPart = parts[0]
        val decimalPart = parts.getOrNull(1)

        val formattedInteger = formatThousands(integerPart)

        return when {
            hasDot && decimalPart != null -> "$formattedInteger.$decimalPart"
            hasDot -> "$formattedInteger."
            else -> formattedInteger
        }
    }
}

enum class AmountInputPhase {
    Idle, WaitingForOperator, WaitingForSecondOperand
}