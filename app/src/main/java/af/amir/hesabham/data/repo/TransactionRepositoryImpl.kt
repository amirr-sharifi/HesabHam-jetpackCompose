package af.amir.hesabham.data.repo

import af.amir.hesabham.data.db.dao.TransactionDao
import af.amir.hesabham.data.db.dao.TransferDao
import af.amir.hesabham.data.db.mapper.toDomain
import af.amir.hesabham.data.db.mapper.toEntity
import af.amir.hesabham.data.db.mapper.toMoney
import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transaction.TransactionWithRelation
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.model.transfer.TransferId
import af.amir.hesabham.domain.repo.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val transferDao: TransferDao,
) : TransactionRepository {

    override suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction): Int =
        transactionDao.update(transaction.toEntity())

    override suspend fun insertTransfer(transfer: Transfer): Long =
        transferDao.insert(transfer.toEntity())

    override suspend fun updateTransfer(transfer: Transfer): Int =
        transferDao.update(transfer.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction): Int {
        return transactionDao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransfer(transfer: Transfer): Int {
        return transactionDao.deleteTransfer(transfer.toEntity())
    }

    override suspend fun getInitialBalanceTransaction(accountId: AccountId): Transaction {
        return transactionDao.getInitialBalanceTransaction(accountId.id).toDomain()
    }

    override suspend fun getTransactionById(transactionId: TransactionId): Transaction {
        return transactionDao.getTransactionById(transactionId.id).toDomain()
    }

    override suspend fun getTransactionByTransferId(transferId: TransferId): List<Transaction> {
        return transactionDao.getTransactionsByTransferId(transferId.id).map { it.toDomain() }
    }

    override suspend fun getTransferById(transferId: TransferId): Transfer {
        return transactionDao.getTransferById(transferId.id).toDomain()
    }

    override fun observeMonthlyIncome(start: Instant, end: Instant): Flow<Money> {
        return transactionDao.observeMonthlyIncome(start.toEpochMilli(), end.toEpochMilli())
            .map { it.toMoney() }
    }

    override fun observeMonthlyExpense(start: Instant, end: Instant): Flow<Money> {
        return transactionDao.observeMonthlyExpense(start.toEpochMilli(), end.toEpochMilli())
            .map { it.toMoney() }
    }

    override fun observeTransactionWithRelation(
        limit: Int,
        from: Instant,
        to: Instant,
    ): Flow<List<TransactionWithRelation>> {
        return transactionDao.observeRecentTransaction(
            limit,
            from.toEpochMilli(),
            to.toEpochMilli()
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun observeTransactionWithRelation(
        from: Instant,
        to: Instant,
    ): Flow<List<TransactionWithRelation>> {
        return transactionDao.observeRecentTransaction(from.toEpochMilli(), to.toEpochMilli())
            .map { list -> list.map { it.toDomain() } }
    }

    override fun observeCategoryExpenseSummary(
        start: Instant,
        end: Instant,
    ): Flow<List<CategoryExpenseSummary>> {
        return transactionDao.observeCategoryExpenseSummary(
            start.toEpochMilli(),
            end.toEpochMilli()
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun observeTopTransactions(
        start: Instant,
        end: Instant,
        type: TransactionType,
        limit: Int,
    ): Flow<List<TransactionWithRelation>> {
        return transactionDao.observeTopTransactions(
            start.toEpochMilli(),
            end.toEpochMilli(),
            type.name,
            limit
        ).map { list -> list.map { it.toDomain() } }
    }
}