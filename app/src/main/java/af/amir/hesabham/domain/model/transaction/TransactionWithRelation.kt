package af.amir.hesabham.domain.model.transaction

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.category.Category

data class TransactionWithRelation(
    val transaction: Transaction,
    val account: Account,
    val category: Category?
)
