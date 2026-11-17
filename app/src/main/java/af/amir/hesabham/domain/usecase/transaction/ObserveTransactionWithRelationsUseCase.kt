package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.repo.TransactionRepository
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant

class ObserveTransactionWithRelationsUseCase(
    private val repo : TransactionRepository
) {

    operator fun invoke(limit : Int, from : Instant, to : Instant) = repo.observeTransactionWithRelation(limit,from,to).map { it.filterNot { withRelation -> withRelation.transaction.type == TransactionType.INITIAL_BALANCE } }
    operator fun invoke(from : Instant, to : Instant) = repo.observeTransactionWithRelation(from,to).map { it.filterNot { withRelation -> withRelation.transaction.type == TransactionType.INITIAL_BALANCE } }
}