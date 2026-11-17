package af.amir.hesabham.data.db.dao

import af.amir.hesabham.data.db.entity.AccountEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: AccountEntity) : Long


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: List<AccountEntity>)

    @Update
    suspend fun update(account: AccountEntity) : Int

    @Query("SELECT * FROM AccountEntity")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM AccountEntity")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM AccountEntity WHERE accountId = :id")
    suspend fun getById(id : String) : AccountEntity

    @Query("SELECT COALESCE(SUM(balance),0) FROM AccountEntity WHERE idArchive = 0")
    fun observeTotalBalance() : Flow<Long>
}