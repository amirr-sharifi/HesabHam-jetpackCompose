package af.amir.hesabham.presentation.core.model

data class CategorySection(
    val header: CategoryUiModel,
    val children: List<CategoryUiModel>,
)
