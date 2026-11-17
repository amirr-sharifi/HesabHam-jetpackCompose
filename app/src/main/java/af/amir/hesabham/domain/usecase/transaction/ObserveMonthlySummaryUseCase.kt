package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.repo.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.threeten.bp.Instant

class ObserveMonthlySummaryUseCase(
    private val transactionRepository: TransactionRepository,
) {

    operator fun invoke(
        start: Instant,
        end: Instant,
    ): Flow<MonthlySummary> =
        combine(
            transactionRepository.observeMonthlyIncome(start, end),
            transactionRepository.observeMonthlyExpense(start, end),
        ) { income, expense ->
            MonthlySummary(income, expense)
        }


}

data class MonthlySummary(
    val income: Money,
    val expense: Money,
)