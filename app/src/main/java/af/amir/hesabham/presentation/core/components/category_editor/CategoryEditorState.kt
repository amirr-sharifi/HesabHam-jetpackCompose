package af.amir.hesabham.presentation.core.components.category_editor

import af.amir.hesabham.presentation.core.model.CategoryUiModel

data class CategoryEditorState(
    val category: CategoryUiModel,
    val isUpdating: Boolean,
    val parentCategory: CategoryUiModel?,
    val incomeParents: List<CategoryUiModel>,
    val expenseParents: List<CategoryUiModel>,
) {
    companion object {
        fun empty() = CategoryEditorState(
            CategoryUiModel.empty(), false, null,
            emptyList(),
            emptyList()
        )
    }
}