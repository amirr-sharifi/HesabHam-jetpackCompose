package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.repo.TransactionRepository
import org.threeten.bp.Instant

class ObserveCategoryExpenseSummary(
    private val repo: TransactionRepository,
) {

    operator fun invoke(
        start: Instant,
        end: Instant,
    ) = repo.observeCategoryExpenseSummary(start, end)
}