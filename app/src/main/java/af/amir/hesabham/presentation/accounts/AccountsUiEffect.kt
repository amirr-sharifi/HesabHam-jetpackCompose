package af.amir.hesabham.presentation.accounts

import af.amir.hesabham.domain.model.account.Account

sealed interface AccountsUiEffect {
    class ShowAccountDetailBottomSheet(val  account: Account) : AccountsUiEffect

    data object Navigate : AccountsUiEffect
}