package af.amir.hesabham.data.db

import af.amir.hesabham.domain.common.TransactionRunner
import androidx.room.withTransaction

class RoomTransactionRunner(
    private val db: ExpenseDatabase,
) : TransactionRunner {

    override suspend fun <T> run(block: suspend () -> T): T {
        return db.withTransaction(block)
    }
}