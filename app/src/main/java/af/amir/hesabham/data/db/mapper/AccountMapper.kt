package af.amir.hesabham.data.db.mapper

import af.amir.hesabham.data.db.entity.AccountEntity
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.account.AccountType

fun Account.toEntity() =
    AccountEntity(
        this.id.id,
        this.name,
        this.isArchive,
        this.type.name,
        this.balance.toLong()
    )

fun AccountEntity.toDomain() =
    Account(
        AccountId(this.accountId),
        this.name,
        this.idArchive,
        AccountType.valueOf(this.type),
        this.balance.toMoney()
    )



