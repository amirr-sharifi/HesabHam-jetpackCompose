package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transaction.TransactionWithRelation
import af.amir.hesabham.framework.datetime.CalendarEngine
import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.mapper.toUiModel
import af.amir.hesabham.presentation.core.model.TransactionDayUiModel
import android.content.Context
import android.util.Log
import java.math.BigDecimal


data class DateRange(
    val from: AppDate,
    val to: AppDate,
)

data class RawTransactionsResult(
    val transactions: List<TransactionWithRelation>,
    val calendarEngine: CalendarEngine,
    val locale: AppLocale,
    val range: DateRange,
)


class TransactionsUiMapper(
    private val context: Context,
) {

    fun map(raw: RawTransactionsResult, filter: FilterBottomSheetTypes): TransactionsState {
        Log.e("Locale", "map: ${raw.locale}", )
        val formattedStartDate = raw.calendarEngine.formatDate(raw.range.from, raw.locale.locale)
        val formattedEndDate = raw.calendarEngine.formatDate(raw.range.to, raw.locale.locale)

        val days = filterTransactions(raw.transactions, filter)
            .sortedByDescending { it.transaction.date }
            .groupBy { raw.calendarEngine.fromInstant(it.transaction.date).date }
            .map { (date, transactions) ->
                createDayUiModel(
                    date = date,
                    transactions = transactions,
                    calendarEngine = raw.calendarEngine,
                    locale = raw.locale,
                )
            }

        return TransactionsState(
            transactions = days,
            isLoading = false,
            selectedFilterType = filter,
            formattedStartDate = formattedStartDate,
            formattedEndDate = formattedEndDate,
            startDate = AppDateTime(date = raw.range.from, hour = 0, minute = 0),
            endDate = AppDateTime(date = raw.range.to, hour = 23, minute = 59),
        )
    }

    private fun filterTransactions(
        transactions: List<TransactionWithRelation>,
        filter: FilterBottomSheetTypes,
    ): List<TransactionWithRelation> = when (filter) {
        FilterBottomSheetTypes.ALL -> transactions
        FilterBottomSheetTypes.INCOME -> transactions.filter {
            it.transaction.type == TransactionType.INCOME
        }

        FilterBottomSheetTypes.EXPENSE -> transactions.filter {
            it.transaction.type == TransactionType.EXPENSE
        }

        FilterBottomSheetTypes.TRANSFER -> transactions.filter {
            it.transaction.type == TransactionType.TRANSFER_IN ||
                it.transaction.type == TransactionType.TRANSFER_OUT
        }
    }

    private fun createDayUiModel(
        date: AppDate,
        transactions: List<TransactionWithRelation>,
        calendarEngine: CalendarEngine,
        locale: AppLocale,
    ): TransactionDayUiModel {
        val totalBalance = transactions.fold(BigDecimal.ZERO) { total, item ->
            total + signedAmount(
                type = item.transaction.type,
                amount = item.transaction.amount.amount,
            )
        }

        return TransactionDayUiModel(
            date = calendarEngine.formatDate(date, locale.locale),
            totalBalance = totalBalance,
            transactions = transactions.map { it.toUiModel(context,calendarEngine, locale) },
            times = transactions.map {
                calendarEngine.formatTime(
                    calendarEngine.fromInstant(it.transaction.date),
                    locale.locale,
                )
            },
        )
    }

    private fun signedAmount(type: TransactionType, amount: BigDecimal): BigDecimal = when (type) {
        TransactionType.INCOME -> amount.abs()
        TransactionType.EXPENSE -> amount.abs().negate()
        TransactionType.TRANSFER_IN -> amount.abs()
        TransactionType.TRANSFER_OUT -> amount.abs().negate()
        TransactionType.INITIAL_BALANCE -> amount.abs()
    }
}
