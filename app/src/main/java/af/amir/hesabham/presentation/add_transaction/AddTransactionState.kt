package af.amir.hesabham.presentation.add_transaction

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.components.category_editor.CategoryEditorState
import af.amir.hesabham.presentation.core.components.tab_switch.TabSwitchItem
import af.amir.hesabham.presentation.core.config.AppBarConfig
import af.amir.hesabham.presentation.core.model.AppBarState
import af.amir.hesabham.presentation.core.model.CategorySection
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import af.amir.hesabham.presentation.core.model.UiIcon
import af.amir.hesabham.presentation.core.model.UiText
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.math.BigDecimal

data class AddTransactionState(
    val isLoading: Boolean = false,
    val isSaving : Boolean = false,
    val appBarState: AppBarState = AppBarConfig.AddTransaction,

    val transactionTabEnabled :Boolean = true,
    val transactionTabList: List<TabSwitchItem> = listOf(
        TabSwitchItem(
            UiText.Resource(R.string.income),
            UiIcon.Resource(R.drawable.ic_down_arrow),
        ),
        TabSwitchItem(
            UiText.Resource(R.string.expense),
            UiIcon.Resource(R.drawable.ic_up_arrow),
        ),
        TabSwitchItem(
            UiText.Resource(R.string.transfer),
            UiIcon.Resource(R.drawable.ic_transfer),
        ),
    ),
    val transactionType: TransactionType = TransactionType.INCOME,
    val amount: BigDecimal = BigDecimal.ZERO,
    val comment: String? = null,
    val selectedDate: AppDateTime = AppDateTime(AppDate(1, 1, 2000), 0, 0),
    val formattedSelectedDate: String = "",

    //category
    val categories: List<CategorySection> = emptyList(),
    val archivedCategories: List<CategoryUiModel> = emptyList(),
    val selectedCategory: CategoryUiModel? = null,
    val isCategoriesLoading: Boolean = true,
    val categoryEditorState: CategoryEditorState = CategoryEditorState.empty(),
    val isAccountsLoading: Boolean = true,


    val accountList: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val transferFromAccount : Account? = null,
    val transferToAccount : Account? = null,
    val accountEditorState: AccountEditorState = AccountEditorState(),


    val overlayDialog: OverlayDialog = OverlayDialog.None,
    val fullScreenDialogStack: List<FullScreenDialog> = emptyList(),

    //Error Messages
    val amountEmptyErrorMessage : UiText? =null,
    val accountEmptyErrorMessage : UiText? =null,
    val categoryEmptyErrorMessage : UiText? =null,
    val toAccountEmptyErrorMessage : UiText? =null,
    val fromAccountEmptyErrorMessage : UiText? =null,

    ) {
    fun getAmountInString(): String = amount.toPlainString()
}


enum class FullScreenDialog {
    Categories, Accounts, AddCategory, AccountEditor
}

enum class OverlayDialog {
    None, Date, Comment, AmountInput , AccountArchiveConfirm,CategoryArchiveConfirm,
}

data class TransactionFieldConfig(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val subTitle: String,
    val isLoading: Boolean,
    val onClick: () -> Unit,
    val error : String? = null,
)
