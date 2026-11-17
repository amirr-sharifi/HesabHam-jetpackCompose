package af.amir.hesabham.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithRelationEntity(
    @Embedded
    val transaction : TransactionEntity,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountId"
    )
    val account : AccountEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category : CategoryEntity?
)
