package af.amir.hesabham.data.db.mapper

import af.amir.hesabham.data.db.entity.CategoryExpenseSummaryDbModel
import af.amir.hesabham.data.db.entity.TransactionEntity
import af.amir.hesabham.data.db.entity.TransactionWithRelationEntity
import af.amir.hesabham.data.db.entity.TransferEntity
import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transaction.TransactionWithRelation
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.model.transfer.TransferId
import org.threeten.bp.Instant

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        this.id.id,
        this.accountId.id,
        this.type.name,
        this.amount.toLong(),
        this.categoryId?.id,
        this.transferId?.id,
        this.date.toEpochMilli(),
        this.note
    )
}

fun TransactionEntity.toDomain(): Transaction {
    val type = TransactionType.valueOf(this.type)
    val id = TransactionId(this.transactionId)
    val money = this.amount.toMoney()
    val date = Instant.ofEpochMilli(this.date)
    val note = this.note
    val category = if (this.categoryId == null) null else CategoryId(this.categoryId)
    val transfer = if (this.transferId == null) null else TransferId(this.transferId)
    val account = AccountId(this.accountId)
    return Transaction(id, type, account, money, category, transfer, date, note)
}

fun TransactionWithRelationEntity.toDomain() =
    TransactionWithRelation(
        transaction.toDomain(),
        account.toDomain(),
        category?.toDomain()
    )

fun CategoryExpenseSummaryDbModel.toDomain() =
    CategoryExpenseSummary(
        categoryId = CategoryId(this.categoryId),
        name = this.name,
        nameResKey = this.nameResKey,
        color = this.color,
        icon = this.icon,
        totalAmount = this.totalAmount.toMoney(),
        transactionCount = this.transactionCount
    )

fun Transfer.toEntity(): TransferEntity =
    TransferEntity(
        id = this.id.id,
        fromAccount = this.fromAccount.id,
        toAccount = this.toAccount.id,
        amount = this.amount.toLong(),
        date = this.date.toEpochMilli(),
        note = this.note
    )

fun TransferEntity.toDomain() = Transfer(
    id = TransferId(this.id),
    fromAccount = AccountId(this.fromAccount),
    toAccount = AccountId(this.toAccount),
    amount = this.amount.toMoney(),
    date = Instant.ofEpochMilli(this.date),
    note = this.note
)