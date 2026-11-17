package af.amir.hesabham.domain.usecase.account

import af.amir.hesabham.domain.repo.AccountRepository

class ObserveTotalBalanceUseCase(
    private val accountRepository: AccountRepository,
) {

    operator fun invoke() = accountRepository.observeTotalBalance()
}