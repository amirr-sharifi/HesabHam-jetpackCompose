package af.amir.hesabham.presentation.home.screens.reports.components

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TopTransactionsSection(
    topExpenses: List<TransactionWithDetailUiModel>,
    topIncomes: List<TransactionWithDetailUiModel>,
    modifier: Modifier = Modifier
) {

    val resources = rememberLocalizeResources(LocalAppLocale.current)

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val currentList = if (selectedTabIndex == 0) {
        topExpenses
    } else {
        topIncomes
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resources.getString(R.string.top_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                MiniTabRow(
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Crossfade(targetState = currentList, label = "TransactionsCrossFade") { list ->
                if (list.isEmpty()) {
                    val emptyTitle = if (selectedTabIndex == 0) resources.getString(R.string.reports_top_expenses_empty_title) else resources.getString(R.string.reports_top_incomes_empty_title)
                    ReportEmptyState(
                        icon = Icons.Outlined.List,
                        title = emptyTitle,
                        description = resources.getString(R.string.reports_top_transactions_empty_subtitle)
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        list.forEach { transaction ->
                            TopTransactionItem(transaction = transaction)
                        }
                    }
                }
            }
        }

    }

}

@Composable
private fun MiniTabRow(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val activeTabColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .background(color = containerColor, shape = RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TabItem(
            title = resources.getString(R.string.expense),
            isActive = selectedIndex == 0,
            activeColor = activeTabColor,
            onClick = { onTabSelected(0) }
        )
        TabItem(
            title = resources.getString(R.string.income),
            isActive = selectedIndex == 1,
            activeColor = activeTabColor,
            onClick = { onTabSelected(1) }
        )
    }
}

@Composable
private fun TabItem(
    title: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TopTransactionItem(
    transaction: TransactionWithDetailUiModel
) {
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.3f
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = transaction.categoryTitle.asString(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resources.getString(R.string.account) +": " + transaction.accountName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        Column(horizontalAlignment = Alignment.End) {

            Text(
                text = rememberFormattedAmount(transaction.amount.toPlainString(),locale) ,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )

            Text(
                text = LocalAppCurrency.current.symbol ,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
            )
        }


    }
}