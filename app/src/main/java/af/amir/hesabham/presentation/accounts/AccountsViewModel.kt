package af.amir.hesabham.presentation.accounts

import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.usecase.account.AccountUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.config.AppBarConfig
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeUiModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val account: AccountUseCase,
    private val transactionUseCase: TransactionUseCase,
) : ViewModel() {

    private var _accounts = emptyList<Account>()

    private val _state = MutableStateFlow(AccountsState())
    val state = _state.asStateFlow()

    private val _uiEffect = Channel<AccountsUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        combine(
            account.totalBalance(),
            account.get.observeAll()
        ) { totalBalance, accounts ->
            Pair(totalBalance, accounts)
        }
            .flowOn(Dispatchers.Default)
            .onEach { (totalBalance, accounts) ->
                val archived = accounts.filter { it.isArchive }
                val sort = _state.value.selectedSort
                val type = _state.value.selectedFilter
                _accounts = accounts.filterNot { it.isArchive }
                val newAccounts = _accounts.filterAndSortedBy(sort, type)
                _state.update {
                    it.copy(
                        isLoading = false,
                        accounts = newAccounts,
                        archived = archived,
                        totalBalance = totalBalance.amount
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: AccountsAction) {
        when (action) {
            is AccountsAction.OnFilterSelected -> {
                val newAccounts =
                    _accounts.filterAndSortedBy(_state.value.selectedSort, action.filter)
                _state.update { it.copy(accounts = newAccounts, selectedFilter = action.filter) }
            }

            is AccountsAction.OnSortSelected -> {
                val newAccounts =
                    _accounts.filterAndSortedBy(action.sort, _state.value.selectedFilter)
                _state.update { it.copy(accounts = newAccounts, selectedSort = action.sort) }
            }

            is AccountsAction.OnAddAccountClick -> onAddAccountClicked()
            is AccountsAction.OnEditAccountClick -> onEditAccountClicked(action.account)
            is AccountsAction.OnSaveAccount -> onSaveAccount(action.newState)
            AccountsAction.OnCloseAccountDialog -> onCloseAccountDialog()
            AccountsAction.OnNavigateIconClicked -> {
                if (_state.value.accountEditorState != null)
                    onCloseAccountDialog()
                else
                    viewModelScope.launch {
                        _uiEffect.send(AccountsUiEffect.Navigate)
                    }
            }

            is AccountsAction.OnAccountClick -> viewModelScope.launch {
                _uiEffect.send(AccountsUiEffect.ShowAccountDetailBottomSheet(action.account))
            }

            is AccountsAction.OnArchiveAccount -> viewModelScope.launch {
                account.changeArchiveStatus.archive(account = action.account)
            }

            is AccountsAction.OnRestoreAccount -> {
                viewModelScope.launch {
                    account.changeArchiveStatus.unarchive(action.account)
                }
            }
        }
    }

    private fun onCloseAccountDialog() {
        _state.update {
            it.copy(
                accountEditorState = null,
                appBarState = AppBarConfig.AccountsScreen
            )
        }
    }

    private fun onSaveAccount(newState: AccountEditorState) {
        viewModelScope.launch {
            if (newState.isUpdating) {
                account.update(
                    newState.account,
                    Money(newState.balance)
                )
            } else {
                account.create(
                    newState.account,
                    Money(newState.balance)
                )
            }
            onCloseAccountDialog()
        }
    }

    private fun onEditAccountClicked(account: Account) {
        viewModelScope.launch {
            val initialBalance = transactionUseCase.getInitialBalance(account.id)
            _state.update {
                it.copy(
                    accountEditorState = AccountEditorState(
                        account = account,
                        balance = initialBalance.amount.amount,
                        isUpdating = true
                    ),
                    appBarState = AppBarConfig.EditAccount
                )
            }
        }
    }

    private fun onAddAccountClicked() {
        val emptyEditorState = AccountEditorState()
        _state.update {
            it.copy(accountEditorState = emptyEditorState, appBarState = AppBarConfig.AddAccount)
        }
    }


    private fun List<Account>.filterAndSortedBy(
        sort: SortOption,
        filter: AccountTypeUiModel?,
    ): List<Account> {
        val sorted = when (sort) {
            SortOption.DEFAULT -> this
            SortOption.HIGHEST_BALANCE -> this.sortedByDescending { it.balance.amount }
            SortOption.LOWEST_BALANCE -> this.sortedBy { it.balance.amount }
            SortOption.NAME -> this.sortedBy { it.name }
        }
        return filter?.let { f -> sorted.filter { it.type == f.type } } ?: sorted
    }

}