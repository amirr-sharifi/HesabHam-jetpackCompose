package af.amir.hesabham.presentation.home.screens.dashboard.compenents

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.theme.BrandPrimary
import af.amir.hesabham.presentation.core.theme.BrandPrimaryDark
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import kotlin.math.sin

@Composable
fun TopHeaderSection(
    monthName: String,
    onNextMonth :()->Unit,
    onPreviousMonth :()->Unit,
    ) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Rounded.KeyboardArrowLeft, "")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    monthName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.Rounded.KeyboardArrowRight, "")
                }
            }
        }

    }
}


@Composable
fun BalanceCardSection(
    totalBalance: BigDecimal,
    monthlyBalance: BigDecimal,
    income: BigDecimal,
    expense: BigDecimal,
) {

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)
    val currency = LocalAppCurrency.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(BrandPrimary, BrandPrimaryDark)))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val wavePath = Path()
                wavePath.moveTo(0f, size.height * 0.6f)
                for (i in 0..size.width.toInt() step 5) {
                    val x = i.toFloat()
                    val y = (sin(x * 0.03) * 20f).toFloat() + size.height * 0.55f
                    wavePath.lineTo(x, y)
                }
                wavePath.lineTo(size.width, size.height)
                wavePath.lineTo(0f, size.height)
                wavePath.close()
                drawPath(wavePath, color = Color.White.copy(alpha = 0.1f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    resources.getString(R.string.total_balance),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        rememberFormattedAmount(totalBalance.toPlainString(), locale),
                        color = if (totalBalance < BigDecimal.ZERO) ExpenseRed else Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(currency.symbol, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .shadow(
                    12.dp,
                    RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.05f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.ArrowDownward,
                            contentDescription = "Income",
                            tint = IncomeGreen,
                            modifier = Modifier
                                .background(IncomeGreen.copy(0.1f), CircleShape)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                resources.getString(R.string.income),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                            )
                            Text(
                                rememberFormattedAmount(income.toPlainString(), locale),
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp), color = Color.LightGray
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                resources.getString(R.string.expense),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                            )
                            Text(
                                rememberFormattedAmount(expense.toPlainString(), locale),
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Rounded.ArrowUpward,
                            contentDescription = "Expense",
                            tint = ExpenseRed,
                            modifier = Modifier
                                .background(ExpenseRed.copy(0.1f), CircleShape)
                                .padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        resources.getString(R.string.balance_for_this_month),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

                        Text(
                            rememberFormattedAmount(
                                monthlyBalance.toPlainString(),
                                locale
                            ),
                            fontWeight = FontWeight.Bold,
                            color = if (monthlyBalance < BigDecimal.ZERO) ExpenseRed else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}