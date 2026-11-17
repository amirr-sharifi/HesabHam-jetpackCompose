package af.amir.hesabham.presentation.add_transaction

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import java.math.BigDecimal

sealed interface AddTransactionAction {
    data class TransactionChanged(val ordinal: Int) : AddTransactionAction
    data class AmountChanged(val amount: BigDecimal) : AddTransactionAction
    data class CategorySelected(val category: CategoryUiModel) : AddTransactionAction
    data class CommentChanged(val comment: String) : AddTransactionAction
    data class DatePicked(val date: AppDateTime) : AddTransactionAction
    data class AccountChanged(val newAccount: Account) : AddTransactionAction


    //dialogs
    object OpenAmountInput : AddTransactionAction
    object OpenCategoryDialog : AddTransactionAction
    object CloseFullScreenDialog : AddTransactionAction
    data class OpenAccountsDialog(val type : TransactionType) : AddTransactionAction
    object OpenDateDialog : AddTransactionAction
    object OpenCommentDialog : AddTransactionAction

    data class EditCategoryClick(val category: CategoryUiModel, val parent: CategoryUiModel?) : AddTransactionAction
    data class AddCategoryClick(val parent: CategoryUiModel?, ) : AddTransactionAction

    object CloseAddCategoryDialog : AddTransactionAction
    data class SaveCategory(val category: CategoryUiModel) : AddTransactionAction
    object CloseOverlayDialog : AddTransactionAction
    object NavigateIconClick : AddTransactionAction

    data class EditAccountClick(val account: Account) : AddTransactionAction
    class AddAccountClick(val type: AccountType) : AddTransactionAction
    class SaveAccount(val newState: AccountEditorState) : AddTransactionAction


    object SaveTransaction : AddTransactionAction
    data class ShowAccountArchiveConfirmDialog(val  account : Account) : AddTransactionAction
    class ShowCategoryArchiveConfirmDialog(val category: CategoryUiModel) : AddTransactionAction
    class RestoreCategory(val category: CategoryUiModel) : AddTransactionAction

    object ArchiveAccount : AddTransactionAction
    object ArchiveCategory : AddTransactionAction {

    }
}