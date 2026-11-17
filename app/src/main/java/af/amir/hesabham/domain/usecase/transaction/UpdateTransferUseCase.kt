package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase

class UpdateTransferUseCase(
    private val transactionRepository: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val transactionRunner: TransactionRunner,

    ) {

    suspend operator fun invoke(transfer: Transfer, onSuccess: () -> Unit, onError: () -> Unit) {
        val oldTransfer = transactionRepository.getTransferById(transfer.id)
        if (transfer == oldTransfer) return
        val relatedTransactions = transactionRepository.getTransactionByTransferId(transfer.id)
        val incomingTransaction =
            relatedTransactions.first { it.type == TransactionType.TRANSFER_IN }
        val outgoingTransaction =
            relatedTransactions.first { it.type == TransactionType.TRANSFER_OUT }

        try {


            transactionRunner.run {
                //Incoming

                updateTransferFromAccount(outgoingTransaction, transfer)
                updateTransferToAccount(incomingTransaction, transfer)


                transactionRepository.updateTransfer(transfer)
                onSuccess()
            }

        } catch (e: Exception) {
            onError()
        }


    }

    private suspend fun updateTransferFromAccount(oldTransaction: Transaction, transfer: Transfer) {
        if (oldTransaction.accountId != transfer.fromAccount) {
            updateAccountBalanceUseCase(
                oldTransaction.accountId,
                TransactionType.TRANSFER_IN,
                oldTransaction.amount
            )
            updateAccountBalanceUseCase(
                transfer.fromAccount,
                TransactionType.TRANSFER_OUT,
                transfer.amount
            )
            transactionRepository.updateTransaction(
                oldTransaction.copy(
                    amount = transfer.amount,
                    date = transfer.date,
                    note = transfer.note,
                    accountId = transfer.fromAccount
                )
            )
        } else {
            updateTransactionUseCase(oldTransaction.copy(
                amount = transfer.amount,
                date = transfer.date,
                note = transfer.note,
                accountId = transfer.fromAccount
            ), {}, {})
        }

    }

    private suspend fun updateTransferToAccount(oldTransaction: Transaction, transfer: Transfer) {
        if (oldTransaction.accountId != transfer.toAccount) {
            updateAccountBalanceUseCase(
                oldTransaction.accountId,
                TransactionType.TRANSFER_OUT,
                oldTransaction.amount
            )
            updateAccountBalanceUseCase(
                transfer.toAccount,
                TransactionType.TRANSFER_IN,
                transfer.amount
            )
            transactionRepository.updateTransaction(
                oldTransaction.copy(
                    amount = transfer.amount,
                    date = transfer.date,
                    note = transfer.note,
                    accountId = transfer.toAccount
                )
            )
        } else {
            updateTransactionUseCase(oldTransaction.copy(
                amount = transfer.amount,
                date = transfer.date,
                note = transfer.note,
                accountId = transfer.toAccount
            ), {}, {})
        }

    }
}