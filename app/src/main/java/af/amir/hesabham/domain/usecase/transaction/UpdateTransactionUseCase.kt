package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase
import java.util.concurrent.CancellationException

class UpdateTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val transactionRunner: TransactionRunner,

    ) {

    suspend operator fun invoke(
        transaction: Transaction,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        val oldTransaction = transactionRepository.getTransactionById(transaction.id)
        if (transaction.type != oldTransaction.type) return
        val amountDifference =
            transaction.amount.amount.subtract(oldTransaction.amount.amount)



        try {
            transactionRunner.run {
                transactionRepository.updateTransaction(transaction)

                if (transaction.accountId != oldTransaction.accountId) {
                    val reversedType =
                        updateAccountBalanceUseCase(
                            oldTransaction.accountId,
                            getReversedType(oldTransaction.type),
                            oldTransaction.amount
                        )
                    updateAccountBalanceUseCase(
                        transaction.accountId,
                        transaction.type,
                        transaction.amount
                    )
                } else {
                    updateAccountBalanceUseCase(
                        transaction.accountId,
                        transaction.type,
                        Money(amountDifference)
                    )
                }


                onSuccess()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError()
        }


    }

    private fun getReversedType(type: TransactionType): TransactionType {
        return when (type) {
            TransactionType.INCOME -> TransactionType.EXPENSE
            TransactionType.EXPENSE -> TransactionType.INCOME
            TransactionType.TRANSFER_IN -> TransactionType.TRANSFER_OUT
            TransactionType.TRANSFER_OUT -> TransactionType.TRANSFER_IN
            TransactionType.INITIAL_BALANCE -> TransactionType.INITIAL_BALANCE
        }
    }
}