package af.amir.hesabham.presentation.accounts

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeUiModel

sealed interface AccountsAction {
    data object OnNavigateIconClicked : AccountsAction
    data class OnFilterSelected(val filter: AccountTypeUiModel?) : AccountsAction
    data class OnSortSelected(val sort: SortOption) : AccountsAction
    data class OnEditAccountClick(val account: Account) : AccountsAction
    data object OnAddAccountClick : AccountsAction
    class OnSaveAccount(val newState: AccountEditorState) : AccountsAction
    class OnAccountClick(val account: Account) : AccountsAction
    class OnArchiveAccount(val account : Account) : AccountsAction
    class OnRestoreAccount(val account: Account) : AccountsAction
    data object OnCloseAccountDialog : AccountsAction
}