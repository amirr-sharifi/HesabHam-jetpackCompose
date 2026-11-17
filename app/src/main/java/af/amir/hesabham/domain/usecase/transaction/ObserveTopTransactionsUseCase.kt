package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.transaction.TopTransactions
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.repo.TransactionRepository
import kotlinx.coroutines.flow.combine
import org.threeten.bp.Instant

class ObserveTopTransactionsUseCase(
    private val repo: TransactionRepository,
) {

    operator fun invoke(
        start: Instant,
        end: Instant,
        limit: Int = 3,
    ) =
        combine(
            repo.observeTopTransactions(start, end, TransactionType.EXPENSE, limit),
            repo.observeTopTransactions(start, end, TransactionType.INCOME, limit)
        ) {expense,income->
            TopTransactions(expense,income)
        }
}