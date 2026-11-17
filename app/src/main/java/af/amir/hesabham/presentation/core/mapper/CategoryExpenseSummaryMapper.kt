package af.amir.hesabham.presentation.core.mapper

import af.amir.hesabham.domain.model.category.CategoryExpenseSummary
import af.amir.hesabham.presentation.core.model.CategoryExpenseSummaryUiModel
import af.amir.hesabham.presentation.core.model.UiIcon
import android.content.Context
import androidx.compose.ui.graphics.Color

fun CategoryExpenseSummary.toUiModel(context: Context) =
    CategoryExpenseSummaryUiModel(
        categoryId = this.categoryId.id,
        title = getStringResource(this.nameResKey, this.name, context),
        icon = UiIcon.Dynamic(getIconByName(this.icon)),
        color = Color(android.graphics.Color.parseColor(this.color)),
        totalAmount = this.totalAmount.amount
    )

