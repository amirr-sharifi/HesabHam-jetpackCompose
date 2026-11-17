package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.repo.AccountRepository
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.usecase.transaction.UpdateTransactionUseCase


class UpdateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val transactionRunner: TransactionRunner
) {

    suspend operator fun invoke(account: Account, newInitialBalance: Money) {
        transactionRunner.run {
            accountRepository.updateAccount(account)
            val oldTransaction = transactionRepository.getInitialBalanceTransaction(account.id)
            updateTransactionUseCase(oldTransaction.copy(amount = newInitialBalance),{},{})
        }
    }


}