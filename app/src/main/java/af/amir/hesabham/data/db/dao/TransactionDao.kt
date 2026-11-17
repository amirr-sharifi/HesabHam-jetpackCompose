package af.amir.hesabham.data.db.dao

import af.amir.hesabham.data.db.entity.CategoryExpenseSummaryDbModel
import af.amir.hesabham.data.db.entity.TransactionEntity
import af.amir.hesabham.data.db.entity.TransactionWithRelationEntity
import af.amir.hesabham.data.db.entity.TransferEntity
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.TransferId
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: List<TransactionEntity>): List<Long>

    @Update
    suspend fun update(transaction: TransactionEntity): Int

    @Delete(entity = TransactionEntity::class)
    suspend fun deleteTransaction(transaction: TransactionEntity) : Int

    @Delete(entity = TransferEntity::class)
    suspend fun deleteTransfer(transfer: TransferEntity): Int

    @Query("SELECT * FROM TransactionEntity WHERE type == :type AND accountId ==:accountId")
    suspend fun getInitialBalanceTransaction(
        accountId: String,
        type: String = TransactionType.INITIAL_BALANCE.name,
    ): TransactionEntity

    @Query("SELECT * FROM TransactionEntity WHERE transactionId =:id")
    suspend fun getTransactionById(id: String): TransactionEntity

    @Query("SELECT * FROM TransactionEntity WHERE transferId =:transferId")
    suspend fun getTransactionsByTransferId(transferId: String): List<TransactionEntity>

    @Query("SELECT * FROM TransferEntity WHERE id =:id")
    suspend fun getTransferById(id: String): TransferEntity

    @Query("SELECT COALESCE(SUM(amount),0) FROM TransactionEntity WHERE type = :type AND date BETWEEN :start AND :end")
    fun observeMonthlyIncome(
        start: Long,
        end: Long,
        type: String = TransactionType.INCOME.name,
    ): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount),0) FROM TransactionEntity WHERE type = :type AND date BETWEEN :start AND :end")
    fun observeMonthlyExpense(
        start: Long,
        end: Long,
        type: String = TransactionType.EXPENSE.name,
    ): Flow<Long>

    @Query("SELECT * FROM TransactionEntity WHERE date >= :from AND date < :to ORDER BY date DESC LIMIT :limit")
    fun observeRecentTransaction(
        limit: Int,
        from: Long,
        to: Long,
    ): Flow<List<TransactionWithRelationEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun observeRecentTransaction(from: Long, to: Long): Flow<List<TransactionWithRelationEntity>>


    @Query("SELECT * FROM TransactionEntity WHERE type =:type AND date BETWEEN :from AND :to ORDER BY amount DESC LIMIT :limit")
    fun observeTopTransactions(from: Long, to: Long,type: String,limit: Int): Flow<List<TransactionWithRelationEntity>>


    @Query(
        """
        SELECT 
         COALESCE(parent.categoryId,c.categoryId) AS categoryId,
         COALESCE(parent.name,c.name) AS name,
         COALESCE(parent.iconName,c.iconName) AS icon,
         COALESCE(parent.nameResKey,c.nameResKey) AS nameResKey,
         COALESCE(parent.color,c.color) AS color,
         SUM(t.amount) AS totalAmount,
         COUNT(t.transactionId) AS transactionCount
        FROM TransactionEntity AS t
        INNER JOIN CategoryEntity AS c ON t.categoryId = c.categoryId
        LEFT JOIN CategoryEntity AS parent ON c.parentId = parent.categoryId
        WHERE t.type =:type 
        AND t.date >= :start 
        AND t.date < :end
        GROUP BY COALESCE(parent.categoryId,c.categoryId)
        ORDER BY totalAmount DESC 
    """
    )
    fun observeCategoryExpenseSummary(
        start: Long,
        end: Long,
        type: String = TransactionType.EXPENSE.name,
    ): Flow<List<CategoryExpenseSummaryDbModel>>
}