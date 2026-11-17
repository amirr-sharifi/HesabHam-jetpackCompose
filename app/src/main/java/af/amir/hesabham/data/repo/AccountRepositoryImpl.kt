package af.amir.hesabham.data.repo

import af.amir.hesabham.data.db.dao.AccountDao
import af.amir.hesabham.data.db.mapper.toDomain
import af.amir.hesabham.data.db.mapper.toEntity
import af.amir.hesabham.data.db.mapper.toMoney
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.repo.AccountRepository
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
) : AccountRepository {

    override suspend fun createAccount(account: Account): Long {
        return accountDao.insert(account.toEntity())
    }

    override suspend fun updateAccount(account: Account): Int {
        return accountDao.update(account.toEntity())
    }

    override suspend fun getAll(): List<Account> {
        return accountDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Account>> {
        return accountDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(accountId: AccountId): Account =
        accountDao.getById(accountId.id).toDomain()

    override fun observeTotalBalance(): Flow<Money> {
        return accountDao.observeTotalBalance().map {
            it.toMoney() }
    }


}