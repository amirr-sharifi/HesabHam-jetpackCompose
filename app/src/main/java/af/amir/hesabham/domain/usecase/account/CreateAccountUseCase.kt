package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.repo.AccountRepository
import af.amir.hesabham.domain.usecase.transaction.RecordOpeningBalanceTransactionUseCase

class CreateAccountUseCase(
    private val accountRepo: AccountRepository,
    private val recordOpeningBalanceTransactionUseCase: RecordOpeningBalanceTransactionUseCase,
    private val transactionRunner: TransactionRunner
) {


    suspend operator fun invoke(
        account: Account,
        initialAmount: Money,
    ) {
        transactionRunner.run {
            accountRepo.createAccount(account)
            recordOpeningBalanceTransactionUseCase(account.id, initialAmount)
        }
    }
}