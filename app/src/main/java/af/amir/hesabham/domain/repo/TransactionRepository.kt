package af.amir.hesabham.domain.repo

import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transaction.TransactionWithRelation
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.model.transfer.TransferId
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.Instant

interface TransactionRepository {
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction): Int
    suspend fun insertTransfer(transfer: Transfer): Long
    suspend fun updateTransfer(transfer: Transfer): Int
    suspend fun deleteTransaction(transaction: Transaction) : Int
    suspend fun deleteTransfer(transfer: Transfer) : Int
    suspend fun getInitialBalanceTransaction(accountId: AccountId): Transaction
    suspend fun getTransactionById(transactionId: TransactionId): Transaction
    suspend fun getTransactionByTransferId(transferId: TransferId): List<Transaction>
    suspend fun getTransferById(transferId: TransferId): Transfer
    fun observeMonthlyIncome(start: Instant, end: Instant): Flow<Money>
    fun observeMonthlyExpense(start: Instant, end: Instant): Flow<Money>
    fun observeTransactionWithRelation(limit: Int, from : Instant, to : Instant): Flow<List<TransactionWithRelation>>
    fun observeTransactionWithRelation( from : Instant, to : Instant): Flow<List<TransactionWithRelation>>
    fun observeCategoryExpenseSummary(start: Instant, end: Instant,): Flow<List<CategoryExpenseSummary>>
    fun observeTopTransactions(start: Instant,end: Instant,type : TransactionType,limit: Int=3): Flow<List<TransactionWithRelation>>

}