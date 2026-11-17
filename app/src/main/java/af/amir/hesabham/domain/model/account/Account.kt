package af.amir.hesabham.domain.model.account

import af.amir.hesabham.domain.model.Money

data class Account(
    val id: AccountId,
    val name: String,
    val isArchive: Boolean,
    val type : AccountType,
    val balance : Money
)

enum class AccountType {
    CASH, BANK, CARD, SAVING, DIGITAL_WALLET, OTHER
}
