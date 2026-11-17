package af.amir.hesabham.data.db.dao

import af.amir.hesabham.data.db.entity.TransferEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface TransferDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transfer: TransferEntity): Long

    @Update
    suspend fun update(transfer: TransferEntity): Int

}