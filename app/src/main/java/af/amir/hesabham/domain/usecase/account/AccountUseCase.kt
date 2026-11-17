package af.amir.hesabham.domain.usecase.account

data class AccountUseCase(
    val create: CreateAccountUseCase,
    val update: UpdateAccountUseCase,
    val get: GetAccountUseCase,
    val totalBalance : ObserveTotalBalanceUseCase,
    val changeArchiveStatus : ChangeAccountArchiveStatusUseCase,
)
