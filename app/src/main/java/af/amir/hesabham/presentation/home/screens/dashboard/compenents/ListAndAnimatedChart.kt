package af.amir.hesabham.presentation.home.screens.dashboard.compenents

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.CategoryExpenseSummaryUiModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal


@Composable
fun MyAccountsSection(
    accounts: List<Account>,
    onViewAllAccountsClick:()->Unit,
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    Column {
        SectionHeader(resources.getString(R.string.my_accounts), resources.getString(R.string.view_all),onViewAllAccountsClick)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            items(accounts) { acc ->
                AppCard(modifier = Modifier.width(110.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(acc.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            rememberFormattedAmount(
                                acc.balance.amount.toPlainString(),
                                LocalAppLocale.current
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseReportSection(
    categories: Map<CategoryExpenseSummaryUiModel, Float>,
    totalAmount: BigDecimal,
    onViewFullReportClick:()->Unit
) {
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    Column {
        SectionHeader(resources.getString(R.string.this_month_expenses),
            resources.getString(R.string.view_full_report),onViewFullReportClick)
        Spacer(modifier = Modifier.height(16.dp))
        if (categories.isEmpty()) {
            EmptyTransactionsCard()
        } else {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var animationPlayed by remember { mutableStateOf(false) }
                    val sweepAngle by animateFloatAsState(
                        targetValue = if (animationPlayed) 360f else 0f,
                        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                        label = "sweet"
                    )

                    LaunchedEffect(key1 = true) { animationPlayed = true }



                    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            var startAngle = -90f
                            categories.forEach { entry ->
                                val category = entry.key
                                val percentage = entry.value
                                val sweep = (percentage / 100f) * sweepAngle
                                drawArc(
                                    color = category.color,
                                    startAngle = startAngle,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = 30f, cap = StrokeCap.Round)
                                )
                                startAngle += sweep
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                rememberFormattedAmount(totalAmount.toPlainString(), locale),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                LocalAppCurrency.current.symbol,
                                fontSize = 10.sp,
                                color = Color.Gray.copy(0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Legend
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { entry ->
                            val cat = entry.key
                            val percentage = entry.value
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(cat.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    cat.title.asString(),
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "${percentage.toInt()}%",
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}