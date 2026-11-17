package af.amir.hesabham.presentation.core.config

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.model.AppBarState

object AppBarConfig {

    val AddCategory = AppBarState(
        R.string.new_category,
        R.drawable.ic_close
    )
    val EditCategory = AppBarState(
        R.string.edit_category,
        R.drawable.ic_close
    )

    val AddAccount = AppBarState(
        R.string.new_account,
        R.drawable.ic_close
    )
    val EditAccount = AppBarState(
        R.string.edit_account,
        R.drawable.ic_close
    )

    val Categories = AppBarState(
        R.string.categories,
        R.drawable.ic_close
    )

    val AccountsDialog = AppBarState(
        R.string.accounts,
        R.drawable.ic_close
    )

    val AddTransaction = AppBarState(
        R.string.add_transaction,
        R.drawable.ic_rounded_back_arrow,
    )
    val EditTransaction = AppBarState(
        R.string.edit_transaction,
        R.drawable.ic_rounded_back_arrow,
    )

    val AccountsScreen = AppBarState(
        R.string.accounts,
        R.drawable.ic_rounded_back_arrow
    )

    val TransactionsScreen = AppBarState(
        R.string.transactions,
        R.drawable.ic_rounded_back_arrow
    )

}