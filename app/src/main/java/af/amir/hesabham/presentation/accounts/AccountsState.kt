package af.amir.hesabham.presentation.accounts

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.config.AppBarConfig
import af.amir.hesabham.presentation.core.model.AppBarState
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeUiModel
import androidx.annotation.StringRes
import java.math.BigDecimal

data class AccountsState(
    val appBarState: AppBarState = AppBarConfig.AccountsScreen,
    val totalBalance: BigDecimal = BigDecimal.ZERO,
    val selectedFilter: AccountTypeUiModel? = null,
    val selectedSort: SortOption = SortOption.DEFAULT,
    val accounts: List<Account> = emptyList(),
    val archived :List<Account> = emptyList(),
    val accountEditorState: AccountEditorState? = null,
    val isLoading : Boolean = true
)

enum class SortOption(@StringRes val titleRes: Int) {
    DEFAULT(R.string.default_text),
    HIGHEST_BALANCE(R.string.highest_balance),
    LOWEST_BALANCE(R.string.lowest_balance),
    NAME(R.string.account_name)
}
