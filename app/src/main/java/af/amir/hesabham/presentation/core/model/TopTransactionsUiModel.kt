package af.amir.hesabham.presentation.core.model

data class TopTransactionsUiModel(
    val expenses : List<TransactionWithDetailUiModel>,
    val incomes : List<TransactionWithDetailUiModel>,
)
