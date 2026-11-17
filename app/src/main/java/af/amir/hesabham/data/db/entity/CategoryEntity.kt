package af.amir.hesabham.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    val categoryId: String,
    val parentId: String?,
    val name: String?,
    val type: String,
    val nameResKey : String?,
    val iconName : String ,
    val color : String ,
    val isDefault : Boolean,
    val isArchived : Boolean
)