package af.amir.hesabham.presentation.home.screens.reports.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.home.screens.reports.CategoryReportItem
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.math.BigDecimal


@Composable
fun UnifiedCategoryDistributionCard(
    categories: List<CategoryReportItem>,
    totalExpense: BigDecimal,
    selectedIndex: Int?,
    onCategorySelect: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {

        val locale = LocalAppLocale.current
        val resources = rememberLocalizeResources(locale)
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = resources.getString(R.string.transactions_distribution),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isEmpty())
                ReportEmptyState(
                    icon = Icons.Outlined.PieChart,
                    title = resources.getString(R.string.reports_chart_empty_title),
                    description = resources.getString(R.string.reports_chart_empty_subtitle),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            else
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val animateProgress by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        label = "donut_anim"
                    )

                    Canvas(modifier = Modifier.size(200.dp)) {
                        val strokeWidth = 20.dp.toPx()
                        var startAngle = -90f

                        categories.forEachIndexed { index, item ->
                            val sweepAngle = (item.percentage / 100f) * 360f * animateProgress
                            val isSelected = selectedIndex == index
                            val currentStroke =
                                if (isSelected) strokeWidth + 8.dp.toPx() else strokeWidth

                            drawArc(
                                color = if (selectedIndex == null || isSelected) item.color else item.color.copy(
                                    alpha = 0.25f
                                ),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 2f,
                                useCenter = false,
                                style = Stroke(width = currentStroke, cap = StrokeCap.Round),
                                size = Size(
                                    size.width - currentStroke,
                                    size.height - currentStroke
                                ),
                                topLeft = Offset(currentStroke / 2, currentStroke / 2)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val selectedCategory = selectedIndex?.let { categories.getOrNull(it) }
                        Text(
                            text = selectedCategory?.name?.asString() ?:resources.getString(R.string.total_expenses),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedCategory?.let {
                                rememberFormattedAmount(
                                    it.amount.toPlainString(),
                                    locale
                                )
                            } ?: rememberFormattedAmount(totalExpense.toPlainString(), locale),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = selectedCategory?.color ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onCategorySelect(if (isSelected) null else index) },
                        color = if (isSelected) item.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.3f
                        ),
                        border = if (isSelected) BorderStroke(1.dp, item.color) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(item.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon.asImageVector(),
                                        contentDescription = null,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name.asString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${ item.percentage.toInt() } ٪" + resources.getString(R.string.of_total),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = rememberFormattedAmount(
                                        item.amount.toPlainString(),
                                        locale
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            AnimatedVisibility(visible = isSelected) {
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = item.color.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = resources.getString(R.string.number_of_transactions)+"${item.transactionCount}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = resources.getString(R.string.daily_average) +"${
                                                rememberFormattedAmount(
                                                    item.dailyAverage.toPlainString(),
                                                    locale
                                                )
                                            } " + LocalAppCurrency.current.symbol,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}