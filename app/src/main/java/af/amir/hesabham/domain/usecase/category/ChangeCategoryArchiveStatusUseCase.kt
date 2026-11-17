package af.amir.hesabham.domain.usecase.category

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.repo.CategoryRepository

class ChangeCategoryArchiveStatusUseCase(
    private val repo : CategoryRepository
) {

    suspend fun archive(category: Category){
        val archiveCategory = category.copy(isArchived = true)
        repo.update(archiveCategory)
    }

    suspend fun unarchive(category: Category){
        val archiveCategory = category.copy(isArchived = false)
        repo.update(archiveCategory)
    }

}