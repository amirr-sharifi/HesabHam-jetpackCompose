package af.amir.hesabham.presentation.core.model

import af.amir.hesabham.domain.model.transaction.TransactionType
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.math.BigDecimal

data class TransactionWithDetailUiModel(
    val id: String,
    val amount: BigDecimal,
    val type : TransactionType,
    val categoryTitle: UiText,
    val categoryIcon: UiIcon?,
    val accountName : String,
    val categoryColor: Color,
    val transferIcon : UiIcon,
    val comment : String?,
    val formattedTime : String,
    val formattedDate : String
)
