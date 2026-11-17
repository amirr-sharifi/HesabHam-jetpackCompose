package af.amir.hesabham.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["accountId"],
            childColumns = ["accountId"]
        ),
    ],
    indices = [Index("accountId")]
)
data class TransactionEntity(
    @PrimaryKey
    val transactionId :String,
    val accountId : String,
    val type : String,
    val amount : Long,
    val categoryId : String?,
    val transferId : String?,
    val date : Long,
    val note : String?,
)

