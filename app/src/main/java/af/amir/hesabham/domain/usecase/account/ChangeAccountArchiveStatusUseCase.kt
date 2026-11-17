package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.repo.AccountRepository

class ChangeAccountArchiveStatusUseCase(
    private val repo : AccountRepository
) {

    suspend fun archive(account: Account){
        val archiveAccount = account.copy(isArchive = true)
        repo.updateAccount(archiveAccount)
    }

    suspend fun unarchive(account: Account){
        val archiveAccount = account.copy(isArchive = false)
        repo.updateAccount(archiveAccount)
    }

}