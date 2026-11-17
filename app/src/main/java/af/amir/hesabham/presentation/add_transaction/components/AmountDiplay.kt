package af.amir.hesabham.presentation.add_transaction.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AmountDisplay(
    amount: String,
    modifier: Modifier = Modifier,
    onAmountClick: () -> Unit,
) {
    val currency = LocalAppCurrency.current
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    val textColor = MaterialTheme.colorScheme.onPrimary
    val formattedAmount = rememberFormattedAmount(amount, locale)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable { onAmountClick() }
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = resources.getString(R.string.enter_amount),
                color = textColor.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currency.symbol,
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                AutoResizedText(
                    text = formattedAmount.ifEmpty { "0" },
                    color = textColor,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    maxFontSize = 56.sp,
                    minFontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayMedium,
    color: Color = Color.Unspecified,
    maxFontSize: TextUnit = 44.sp,
    minFontSize: TextUnit = 12.sp,
    stepGranularity: TextUnit = 2.sp
) {
    var resizedTextStyle by remember(text, style, maxFontSize) {
        mutableStateOf(style.copy(fontSize = maxFontSize))
    }
    var shouldDraw by remember(text, style, maxFontSize) {
        mutableStateOf(false)
    }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        style = resizedTextStyle,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                val nextSize = resizedTextStyle.fontSize.value - stepGranularity.value
                if (nextSize >= minFontSize.value) {
                    resizedTextStyle = resizedTextStyle.copy(fontSize = nextSize.sp)
                } else {
                    resizedTextStyle = resizedTextStyle.copy(fontSize = minFontSize)
                    shouldDraw = true
                }
            } else {
                shouldDraw = true
            }
        }
    )
}