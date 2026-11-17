package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.repo.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccountUseCase(
    private val accountRepo: AccountRepository,
) {

    fun observeAll(): Flow<List<Account>> {
        return accountRepo.observeAll()
    }

    suspend fun getAll(): List<Account> {
        return accountRepo.getAll()
    }

    suspend fun getById(accountId: AccountId): Account {
        return accountRepo.getById(accountId)
    }
}