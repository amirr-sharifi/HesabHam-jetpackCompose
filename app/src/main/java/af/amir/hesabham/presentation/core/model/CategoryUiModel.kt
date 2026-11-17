package af.amir.hesabham.presentation.core.model

import af.amir.hesabham.domain.model.category.CategoryType
import af.amir.hesabham.domain.util.generateStringId
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryUiModel(
    val id: String,
    val parentId: String?,
    val type: CategoryType,
    val title: UiText,
    val icon: ImageVector,
    val isDefault : Boolean,
    val isArchived : Boolean,
    val color: Color,
    ){
    companion object{


        fun empty() : CategoryUiModel{
            return CategoryUiModel(
                id = generateStringId(),
                parentId = null,
                type = CategoryType.INCOME,
                title = UiText.Dynamic(""),
                isDefault= false,
                isArchived = false,
                icon = Icons.Default.Home,
                color = Color.Transparent
            )
        }
    }
}
