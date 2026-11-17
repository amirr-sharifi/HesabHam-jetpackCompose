package af.amir.hesabham.domain.repo
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun createAccount(account: Account):Long
    suspend fun updateAccount(account: Account) :Int
    suspend fun getAll(): List<Account>
    fun observeAll() :Flow<List<Account>>
    suspend fun getById(accountId: AccountId) : Account
    fun observeTotalBalance():Flow<Money>
}