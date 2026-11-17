package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.repo.TransactionRepository

class GetInitialBalanceUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(accountId: AccountId): Transaction {
        return transactionRepository.getInitialBalanceTransaction(accountId)
    }
}