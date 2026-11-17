package af.amir.hesabham.domain.usecase.category

import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.repo.CategoryRepository

class AddCategoryUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) :Long {
        return categoryRepository.addCategory(category)

    }

}