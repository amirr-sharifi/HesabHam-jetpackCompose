package af.amir.hesabham.domain.model.category

data class Category(
    val categoryId: CategoryId,
    val name : String?,
    val parentId : CategoryId?,
    val categoryType : CategoryType,
    val nameResKey : String?,
    val iconName : String,
    val color : String,
    val isDefault : Boolean,
    val isArchived : Boolean
)

enum class CategoryType {
INCOME , EXPENSE, INITIAL_BALANCE
}
