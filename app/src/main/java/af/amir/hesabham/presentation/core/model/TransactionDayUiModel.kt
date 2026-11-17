package af.amir.hesabham.presentation.core.model

import java.math.BigDecimal

data class TransactionDayUiModel(
    val date : String,
    val totalBalance  : BigDecimal,
    val transactions: List<TransactionWithDetailUiModel>,
    val times: List<String>
)
