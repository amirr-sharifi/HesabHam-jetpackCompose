package af.amir.hesabham.presentation.accounts.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

@Composable
fun TotalBalanceCard(isLoading: Boolean, balance: BigDecimal) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
        AppCard(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth(),
            elevation = 0.dp
        ) {


                if (isLoading)
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    } else
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = resources.getString(R.string.total_balance),
                            color = Color.Gray.copy(0.7f),
                        )
                        Row() {
                            Text(
                                text = LocalAppCurrency.current.symbol,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 4.dp, end = 4.dp),
                                fontStyle = MaterialTheme.typography.bodySmall.fontStyle
                            )
                            Text(
                                text = rememberFormattedAmount(
                                    balance.toPlainString(),
                                    LocalAppLocale.current
                                ),
                                color = if (balance < BigDecimal.ZERO) ExpenseRed else MaterialTheme.colorScheme.primary,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
            }



}
