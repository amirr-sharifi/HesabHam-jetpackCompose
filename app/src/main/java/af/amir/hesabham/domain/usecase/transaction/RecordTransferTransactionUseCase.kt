package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.model.transfer.TransferId
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase
import af.amir.hesabham.domain.util.generateStringId
import org.threeten.bp.Instant

class RecordTransferTransactionUseCase(
    private val repo: TransactionRepository,
    private val updateAccountBalanceUseCase: UpdateAccountBalanceUseCase,
    private val transactionRunner: TransactionRunner,
) {

    suspend operator fun invoke(
        amount: Money,
        fromAccount: AccountId,
        toAccount: AccountId,
        date: Instant,
        note: String?,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {

        val transferId = TransferId(generateStringId())

        val outgoingTransaction = Transaction(
            id = TransactionId(generateStringId()),
            type = TransactionType.TRANSFER_OUT,
            accountId = fromAccount,
            amount = amount,
            categoryId = null,
            transferId = transferId,
            date = date,
            note = note
        )

        val incomingTransaction = Transaction(
            id = TransactionId(generateStringId()),
            type = TransactionType.TRANSFER_IN,
            accountId = toAccount,
            amount = amount,
            categoryId = null,
            transferId = transferId,
            date = date,
            note = note
        )

        val transfer = Transfer(
            id = transferId,
            fromAccount = fromAccount,
            toAccount = toAccount,
            amount = amount,
            date = date,
            note = note
        )

        try {
            transactionRunner.run {
                repo.insertTransaction(incomingTransaction)
                updateAccountBalanceUseCase(incomingTransaction.accountId,incomingTransaction.type,incomingTransaction.amount)

                repo.insertTransaction(outgoingTransaction)
                updateAccountBalanceUseCase(outgoingTransaction.accountId,outgoingTransaction.type,outgoingTransaction.amount)

                repo.insertTransfer(transfer)
            }
            onSuccess()
        } catch (e: Exception) {
            onError()
        }

    }
}