package af.amir.hesabham.presentation.core.model.accounttype

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountType

object AccountTypeRegistry {

    val items = AccountType.entries.map {
        it.toUi()
    }

    fun get(type: AccountType) = items.first { it.type == type }
    fun get(account: Account) = items.first { it.type == account.type }

}