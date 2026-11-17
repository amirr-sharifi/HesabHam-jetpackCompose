package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel

sealed interface TransactionsAction {
    class OnTransactionClick(val transaction: TransactionWithDetailUiModel) : TransactionsAction
    object OnAddTransactionClick : TransactionsAction
    class OnEditTransactionClick(val transactionId : String) : TransactionsAction
    class OnShowDeleteDialog(val transactionId : String) : TransactionsAction
    object OnSettingsClick : TransactionsAction
    object OnDeleteTransaction : TransactionsAction


    //BottomSheet
    class StartTimePicked(val date: AppDateTime) : TransactionsAction
    class EndTimePicked(val date: AppDateTime) : TransactionsAction
    class SaveFilterState(val filter : FilterBottomSheetTypes) : TransactionsAction
}