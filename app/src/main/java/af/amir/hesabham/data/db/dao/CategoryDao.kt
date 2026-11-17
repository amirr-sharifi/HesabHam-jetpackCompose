package af.amir.hesabham.data.db.dao

import af.amir.hesabham.data.db.entity.CategoryEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(categoryList: List<CategoryEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: CategoryEntity): Long

    @Query("SELECT * FROM CategoryEntity")
    fun getAllFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE categoryId == :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Update
    suspend fun update(category: CategoryEntity)

}