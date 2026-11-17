package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel

sealed interface TransactionsEffect {
    data class NavigateToAddTransaction(val transactionId: String?) : TransactionsEffect
    data class OnTransactionClick(
        val transaction: TransactionWithDetailUiModel,
    ) : TransactionsEffect

    object ShowDateRangeError : TransactionsEffect
    object ShowDeleteDialog : TransactionsEffect
    object CloseDetailBottomSheet : TransactionsEffect

}