package af.amir.hesabham.presentation.core.mapper

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transaction.TransactionWithRelation
import af.amir.hesabham.framework.datetime.CalendarEngine
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.core.model.UiIcon
import af.amir.hesabham.presentation.core.model.UiText
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import android.content.Context

fun TransactionWithRelation.toUiModel(context: Context, calendarEngine: CalendarEngine,locale: AppLocale): TransactionWithDetailUiModel {
    val date = calendarEngine.fromInstant(this.transaction.date)
    val formattedTime = calendarEngine.formatTime(date,locale.locale)
    val formattedDate = calendarEngine.formatDate(date.date,locale.locale)
    val categoryMapper = UiCategoryMapper(context)
    val uiCategory = category?.let {
        categoryMapper.mapToUiModel(this.category)
    }
    return TransactionWithDetailUiModel(
        id = transaction.id.id,
        amount = transaction.amount.amount,
        type = transaction.type,
        categoryTitle = uiCategory?.title ?: UiText.Resource(R.string.transfer),
        categoryIcon = uiCategory?.icon?.let { UiIcon.Dynamic(it) },
        categoryColor = uiCategory?.color
            ?: if (transaction.type == TransactionType.TRANSFER_IN) IncomeGreen else ExpenseRed,
        accountName = account.name,
        transferIcon =
        UiIcon.Resource(if (transaction.type == TransactionType.TRANSFER_IN) R.drawable.ic_wallet_receive else R.drawable.ic_wallet_send),
        comment = transaction.note,
        formattedTime = formattedTime,
        formattedDate = formattedDate

    )
}


