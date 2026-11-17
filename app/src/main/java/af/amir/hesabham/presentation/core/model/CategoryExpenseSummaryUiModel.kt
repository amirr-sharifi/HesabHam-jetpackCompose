package af.amir.hesabham.presentation.core.model

import androidx.compose.ui.graphics.Color
import java.math.BigDecimal

data class CategoryExpenseSummaryUiModel(
    val categoryId : String,
    val title : UiText,
    val icon: UiIcon,
    val color : Color,
    val totalAmount : BigDecimal
)
