package af.amir.hesabham.data.db

import af.amir.hesabham.data.db.dao.AccountDao
import af.amir.hesabham.data.db.dao.CategoryDao
import af.amir.hesabham.data.db.dao.TransactionDao
import af.amir.hesabham.data.db.dao.TransferDao
import af.amir.hesabham.data.db.entity.AccountEntity
import af.amir.hesabham.data.db.entity.CategoryEntity
import af.amir.hesabham.data.db.entity.TransactionEntity
import af.amir.hesabham.data.db.entity.TransferEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =
    [AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        TransferEntity::class],

    version = 1
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao
    abstract val transferDao: TransferDao
    abstract val categoryDao: CategoryDao
    abstract val accountDao: AccountDao
}