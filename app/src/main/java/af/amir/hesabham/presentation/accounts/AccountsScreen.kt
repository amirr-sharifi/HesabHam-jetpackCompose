package af.amir.hesabham.presentation.accounts

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.accounts.components.AccountItem
import af.amir.hesabham.presentation.accounts.components.AccountsListHeaderWithActions
import af.amir.hesabham.presentation.accounts.components.EmptyAccountsState
import af.amir.hesabham.presentation.accounts.components.TotalBalanceCard
import af.amir.hesabham.presentation.accounts.components.archivedAccountsSection
import af.amir.hesabham.presentation.core.components.AccountDetailBottomSheet
import af.amir.hesabham.presentation.core.components.ExpenseTopBar
import af.amir.hesabham.presentation.core.components.PrimaryButton
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorDialog
import af.amir.hesabham.presentation.core.config.AppBarConfig
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.util.ObserveAsEffects
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel


@Composable
fun AccountsScreen(onNavigateUp: () -> Unit, onNavigateToAddTransaction: () -> Unit) {
    val viewModel = koinViewModel<AccountsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AccountsScreenStateless(
        state,
        viewModel::onAction,
        viewModel.uiEffect,
        onNavigateUp,
        onNavigateToAddTransaction
    )
}

@Composable
private fun AccountsScreenStateless(
    state: AccountsState,
    onAction: (AccountsAction) -> Unit,
    uiEffect: Flow<AccountsUiEffect>,
    onNavigateUp: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
) {

    val resources = rememberLocalizeResources(LocalAppLocale.current)
    var accountDetail by remember {
        mutableStateOf<Account?>(null)
    }

    ObserveAsEffects(uiEffect) {
        when (it) {
            AccountsUiEffect.Navigate -> onNavigateUp()
            is AccountsUiEffect.ShowAccountDetailBottomSheet -> accountDetail = it.account
        }
    }

    Scaffold(
        topBar = {
            ExpenseTopBar(state.appBarState) { onAction(AccountsAction.OnNavigateIconClicked) }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {


                item {
                    TotalBalanceCard(
                        isLoading = state.isLoading,
                        balance = state.totalBalance,
                    )
                }

                item {
                    AccountsListHeaderWithActions(
                        selectedFilter = state.selectedFilter,
                        selectedSort = state.selectedSort,
                        onFilterSelected = { onAction(AccountsAction.OnFilterSelected(it)) },
                        onSortSelected = { onAction(AccountsAction.OnSortSelected(it)) }
                    )
                }

                if (state.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                } else {
                    if (state.accounts.isEmpty()) {
                        item {
                            EmptyAccountsState(onAddAccountClick = { onAction(AccountsAction.OnAddAccountClick) })
                        }
                    } else {
                        items(
                            items = state.accounts,
                            key = { it.id.id }
                        ) { account ->
                            AccountItem(
                                account = account,
                                onClick = { onAction(AccountsAction.OnAccountClick(account)) },
                            )
                        }
                    }
                }




                if (state.archived.isNotEmpty())
                    item {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    }
                archivedAccountsSection(
                    state.archived,
                    onRestore = { onAction(AccountsAction.OnRestoreAccount(it)) })

            }

            PrimaryButton(
                onClick = { onAction(AccountsAction.OnAddAccountClick) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                title = resources.getString(R.string.create_new_account)
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            visible = state.accountEditorState != null,
            enter = slideInHorizontally(
                animationSpec = tween(300)
            ) { fullWidth -> -fullWidth },
            exit = slideOutHorizontally(
                animationSpec = tween(300)
            ) { fullWidth -> -fullWidth }
        ) {
            state.accountEditorState?.let { dialogState ->
                AccountEditorDialog(
                    dialogState,
                    onDismissRequest = { onAction(AccountsAction.OnCloseAccountDialog) },
                    onSaveClick = { onAction(AccountsAction.OnSaveAccount(it)) })
            }

        }

        accountDetail?.let {
            AccountDetailBottomSheet(
                account = it,
                onDismiss = { accountDetail = null },
                onAddTransaction = {
                    accountDetail = null
                    onNavigateToAddTransaction()
                },
                onEdit = {
                    accountDetail = null
                    onAction(AccountsAction.OnEditAccountClick(it))
                },
                onDelete = {
                    accountDetail = null
                    onAction(AccountsAction.OnArchiveAccount(it))
                }
            )
        }

    }
}





