package af.amir.hesabham.domain.usecase.category

data class CategoryUseCase(
    val add : AddCategoryUseCase,
    val observeAll : ObserveCategoriesUseCase,
    val changeCategoryArchiveStatus: ChangeCategoryArchiveStatusUseCase
)
