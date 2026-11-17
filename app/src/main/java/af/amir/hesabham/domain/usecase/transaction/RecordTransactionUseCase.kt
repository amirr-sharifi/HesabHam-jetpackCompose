package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase
import af.amir.hesabham.domain.util.generateStringId
import org.threeten.bp.Instant

class RecordTransactionUseCase(
    private val repo: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val transactionRunner: TransactionRunner
) {

    suspend operator fun invoke(
        type: TransactionType,
        amount: Money,
        category: CategoryId,
        account: AccountId,
        date: Instant,
        note: String?,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        val transactionId = TransactionId(generateStringId())
        val newTransaction = Transaction(
            id = transactionId,
            type = type,
            accountId = account,
            amount = amount,
            categoryId = category,
            date = date,
            note = note,
            transferId = null
        )
        try {
            transactionRunner.run {
               repo.insertTransaction(newTransaction)
                updateAccountBalanceUseCase(account,type,amount)
            }
            onSuccess()
        } catch (e: Exception) {
            onError()
        }
    }
}