package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.repo.TransactionRepository

class GetTransactionUseCase (
    private val repo : TransactionRepository
){

    suspend operator fun invoke(id : TransactionId) =
        repo.getTransactionById(id)
}