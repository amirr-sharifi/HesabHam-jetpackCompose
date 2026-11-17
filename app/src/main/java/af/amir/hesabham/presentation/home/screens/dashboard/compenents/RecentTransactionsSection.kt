package af.amir.hesabham.presentation.home.screens.dashboard.compenents

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionWithDetailUiModel>,
    onSeeAllClick: () -> Unit,
    onTransactionClick: (TransactionWithDetailUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    val resources = rememberLocalizeResources(LocalAppLocale.current)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SectionHeader(
            resources.getString(R.string.recent_transactions),
            resources.getString(R.string.view_all),
            onSeeAllClick
        )

        if (transactions.isEmpty()) {
            EmptyTransactionsCard()
        } else {
            AppCard {
                transactions.take(5).forEachIndexed { index, transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction) }
                    )

                    if (index < transactions.take(5).lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun TransactionItemRow(
    transaction: TransactionWithDetailUiModel,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val locale = LocalAppLocale.current
    val currency = LocalAppCurrency.current
    val formattedAmount = rememberFormattedAmount(transaction.amount.toPlainString(), locale)

    val (amountPrefix, amountColor) = when (transaction.type) {
        TransactionType.INCOME, TransactionType.TRANSFER_IN, TransactionType.INITIAL_BALANCE -> "+" to IncomeGreen
        TransactionType.EXPENSE, TransactionType.TRANSFER_OUT -> "-" to ExpenseRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(transaction.categoryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.categoryIcon?.asImageVector()
                        ?: transaction.transferIcon.asImageVector(),
                    contentDescription = null,
                    tint = transaction.categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = transaction.categoryTitle.asString(
                        rememberLocalizeResources(
                            LocalAppLocale.current
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.accountName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))



        Column(
            horizontalAlignment = Alignment.End
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = "$amountPrefix $formattedAmount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    maxLines = 1
                )
            }

            Text(
                text = currency.symbol,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

