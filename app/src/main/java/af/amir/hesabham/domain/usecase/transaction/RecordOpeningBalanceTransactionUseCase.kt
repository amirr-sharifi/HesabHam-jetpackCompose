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
import af.amir.hesabham.domain.util.SystemCategoryIds
import af.amir.hesabham.domain.util.generateStringId
import org.threeten.bp.Instant

class RecordOpeningBalanceTransactionUseCase(
    private val transactionRepo: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val transactionRunner: TransactionRunner,
) {

    suspend operator fun invoke(
        accountId: AccountId,
        initialAmount: Money,
    ) {

        val transactionId = TransactionId(generateStringId())

        val balanceTransaction = Transaction(
            transactionId,
            TransactionType.INITIAL_BALANCE,
            accountId,
            initialAmount,
            CategoryId(SystemCategoryIds.INITIAL_CATEGORY_ID),
            null,
            Instant.now(),
            null,
        )

        transactionRunner.run {
            transactionRepo.insertTransaction(balanceTransaction)
            updateAccountBalanceUseCase(
                balanceTransaction.accountId,
                balanceTransaction.type,
                balanceTransaction.amount
            )
        }

    }

}