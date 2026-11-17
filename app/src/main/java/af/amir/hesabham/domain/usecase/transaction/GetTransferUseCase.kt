package af.amir.hesabham.domain.usecase.transaction

import af.amir.hesabham.domain.model.transfer.TransferId
import af.amir.hesabham.domain.repo.TransactionRepository

class GetTransferUseCase(
    private val repo: TransactionRepository,
) {

    suspend operator fun invoke(id: TransferId) =
        repo.getTransferById(id)


}