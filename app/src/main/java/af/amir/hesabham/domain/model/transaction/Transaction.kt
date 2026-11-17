package af.amir.hesabham.domain.model.transaction

import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.transfer.TransferId
import org.threeten.bp.Instant

data class Transaction(
    val id: TransactionId,
    val type : TransactionType,
    val accountId: AccountId,
    val amount: Money,
    val categoryId: CategoryId?,
    val transferId: TransferId?,
    val date: Instant,
    val note: String?,
)

enum class TransactionType{
    INCOME, EXPENSE, TRANSFER_IN, TRANSFER_OUT , INITIAL_BALANCE
}