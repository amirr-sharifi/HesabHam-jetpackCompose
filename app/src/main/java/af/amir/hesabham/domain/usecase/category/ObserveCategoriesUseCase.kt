package af.amir.hesabham.domain.usecase.category

import af.amir.hesabham.domain.repo.CategoryRepository

class ObserveCategoriesUseCase(
    private val repo: CategoryRepository,
) {
    operator fun invoke() = repo.getCategories()
}