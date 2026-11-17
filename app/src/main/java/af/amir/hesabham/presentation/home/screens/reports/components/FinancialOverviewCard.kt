package af.amir.hesabham.presentation.home.screens.reports.components
import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

@Composable
fun FinancialOverviewCard(
    balance: BigDecimal,
    income: BigDecimal,
    expense: BigDecimal,
    modifier: Modifier = Modifier,
) {
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = resources.getString(R.string.period_balance),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val balanceColor =
                        if (balance < BigDecimal.ZERO) ExpenseRed else IncomeGreen
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(LocalAppCurrency.current.symbol,
                                color = balanceColor)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = rememberFormattedAmount(
                                    balance.toPlainString(),
                                    locale
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = balanceColor
                            )
                        }
                    }

                }



                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryBar(
                        title = resources.getString(R.string.income),
                        amount =rememberFormattedAmount(income.toPlainString(), locale),
                        color = Color(0xFF2E7D32),
                        icon = Icons.Rounded.ArrowDownward,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBar(
                        title = resources.getString(R.string.expense),
                        amount = rememberFormattedAmount(expense.toPlainString(), locale),
                        color = Color(0xFFE53935),
                        icon = Icons.Rounded.ArrowUpward,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
}

@Composable
private fun SummaryBar(
    title: String,
    amount: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}
