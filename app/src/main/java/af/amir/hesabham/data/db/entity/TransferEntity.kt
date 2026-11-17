package af.amir.hesabham.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransferEntity(
    @PrimaryKey
    val id: String,
    val fromAccount: String,
    val toAccount: String,
    val amount: Long,
    val date: Long,
    val note: String?,
)