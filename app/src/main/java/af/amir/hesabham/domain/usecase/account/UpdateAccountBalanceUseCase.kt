package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.repo.AccountRepository

class UpdateAccountBalanceUseCase(
    private val getAccountUseCase: GetAccountUseCase,
    private val accountRepository: AccountRepository,
) {


    suspend operator fun invoke(
        accountId: AccountId,
        transactionType: TransactionType,
        transactionAmount: Money,
    ) {
        val account = getAccountUseCase.getById(accountId)
        val accountBalance = account.balance.amount
        val newBalance = when (transactionType) {
            TransactionType.INCOME,
            TransactionType.INITIAL_BALANCE,
            TransactionType.TRANSFER_IN,
            -> accountBalance.plus(transactionAmount.amount)

            TransactionType.TRANSFER_OUT,
            TransactionType.EXPENSE,
            -> accountBalance.subtract(transactionAmount.amount)
        }

        accountRepository.updateAccount(account.copy(balance = Money(newBalance)))

    }

}