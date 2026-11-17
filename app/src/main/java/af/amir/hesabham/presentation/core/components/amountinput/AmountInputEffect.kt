package af.amir.hesabham.presentation.core.components.amountinput

import java.math.BigDecimal

sealed interface AmountInputEffect {
    data class ShowError(val error: AmountInputError): AmountInputEffect
    object DismissBottomSheet : AmountInputEffect
    data class ConfirmAmount(val amount : BigDecimal) : AmountInputEffect
}
