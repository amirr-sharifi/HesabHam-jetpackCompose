package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.TransferId
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase
import java.util.concurrent.CancellationException

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val transactionRunner: TransactionRunner
) {

    suspend operator fun invoke(transactionId: TransactionId,onSuccess : ()->Unit,onError: ()->Unit) {
        val transaction = transactionRepository.getTransactionById(transactionId)
        invoke(transaction,onSuccess,onError)
    }

    suspend operator fun invoke(transaction: Transaction,onSuccess : ()->Unit,onError: ()->Unit)  {
        if (isTypeTransfer(transaction))
            deleteTransfer(transaction.transferId!!,onSuccess, onError)
        else
            deleteTransaction(transaction,onSuccess, onError)
    }

    private fun isTypeTransfer(transaction: Transaction) =
        transaction.type == TransactionType.TRANSFER_IN ||
                transaction.type == TransactionType.TRANSFER_OUT

    private suspend fun deleteTransaction(transaction: Transaction,onSuccess : ()->Unit,onError: ()->Unit)  {
        val reversedType = when (transaction.type) {
            TransactionType.INCOME -> TransactionType.EXPENSE
            TransactionType.EXPENSE -> TransactionType.INCOME
            TransactionType.TRANSFER_IN -> TransactionType.TRANSFER_OUT
            TransactionType.TRANSFER_OUT -> TransactionType.TRANSFER_IN
            TransactionType.INITIAL_BALANCE -> TransactionType.INITIAL_BALANCE
        }
        try {
            transactionRunner.run {
                updateAccountBalanceUseCase(transaction.accountId, reversedType, transaction.amount)
                transactionRepository.deleteTransaction(transaction)
                onSuccess()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e :Exception){
            onError()
        }
    }

    private suspend fun deleteTransfer(id: TransferId,onSuccess : ()->Unit,onError: ()->Unit) {
        val transfer = transactionRepository.getTransferById(id)
        val relatedTransactions = transactionRepository.getTransactionByTransferId(id)
        try {
            transactionRunner.run {
                relatedTransactions.forEach {
                    deleteTransaction(it,onSuccess
                    ) {
                        onError()
                        throw IllegalStateException()
                    }
                }
                transactionRepository.deleteTransfer(transfer)
            }
            onSuccess()
        } catch (e: CancellationException) {
            throw e
        } catch (e : Exception){
            onError()
        }
    }
}