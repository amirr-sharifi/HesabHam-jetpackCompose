package af.amir.hesabham.presentation.core.components.amountinput

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.dialog.BottomAttachDialog
import af.amir.hesabham.presentation.core.config.setting.*
import af.amir.hesabham.presentation.util.ObserveAsEffects
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

@Composable
fun AmountInputBottomSheet(
    modifier: Modifier = Modifier,
    defaultValue: BigDecimal = BigDecimal(0),
    onDismissRequest: () -> Unit,
    onConfirmAmount: (amount: BigDecimal) -> Unit,
) {
    val localCurrency = LocalAppCurrency.current
    val viewModel = koinViewModel<AmountInputViewModel>(parameters = {
        parametersOf(localCurrency.fractionDigits)
    })

    LaunchedEffect(defaultValue) {
        viewModel.setAmount(defaultValue)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    AmountInputBottomSheetStateless(
        modifier = modifier,
        currencyFractionDigits = localCurrency.fractionDigits,
        state = state,
        uiEffects = viewModel.effects,
        onIntent = viewModel::onIntent,
        onDismissRequest = onDismissRequest,
        onConfirmAmount = onConfirmAmount
    )
}

@Composable
fun AmountInputBottomSheetStateless(
    modifier: Modifier = Modifier,
    currencyFractionDigits: Int,
    state: AmountInputState,
    uiEffects: Flow<AmountInputEffect>,
    onIntent: (AmountInputIntent) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmAmount: (amount: BigDecimal) -> Unit,
) {
    val context = LocalContext.current
    val appLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(appLocale)

    var lastError by rememberSaveable { mutableStateOf<AmountInputError?>(null) }
    var lastErrorTime by rememberSaveable { mutableStateOf(0L) }

    fun showErrorToast(error: AmountInputError, message: String) {
        val now = System.currentTimeMillis()
        if (lastError == error && now - lastErrorTime < 1000) return
        lastError = error
        lastErrorTime = now
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    ObserveAsEffects(uiEffects) { effect ->
        when (effect) {
            is AmountInputEffect.ConfirmAmount -> onConfirmAmount(effect.amount)
            is AmountInputEffect.ShowError -> {
                val message = when (effect.error) {
                    AmountInputError.DecimalAlreadyExist -> resources.getString(R.string.error_decimal_already_exist)
                    AmountInputError.Unexpected -> resources.getString(R.string.error_unexpected)
                    AmountInputError.DivideByZero -> resources.getString(R.string.error_divide_by_zero)
                    AmountInputError.NegativeAmountNotAllowed -> resources.getString(R.string.error_negative_error)
                }
                showErrorToast(effect.error, message)
            }
            AmountInputEffect.DismissBottomSheet -> onDismissRequest()
        }
    }

    BottomAttachDialog(
        modifier = modifier,
        onDismiss = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AmountInputDisplay(
                amount = state.formattedAmount,
                onClearPressed = { onIntent(AmountInputIntent.ClearPressed) }
            )

            Spacer(Modifier.height(24.dp))

            AmountInputUnifiedKeypad(currencyFractionDigits, onIntent)
        }
    }
}

@Composable
private fun AmountInputUnifiedKeypad(currencyFractionDigits: Int, onIntent: (AmountInputIntent) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadRow {
                KeypadButton(
                    text = "\u00F7", // ÷
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) { onIntent(AmountInputIntent.OperatorPressed(MathOperator.Divide)) }
                KeypadButton(text = "7") { onIntent(AmountInputIntent.DigitPressed("7")) }
                KeypadButton(text = "8") { onIntent(AmountInputIntent.DigitPressed("8")) }
                KeypadButton(text = "9") { onIntent(AmountInputIntent.DigitPressed("9")) }

            }

            KeypadRow {
                KeypadButton(
                    text = "\u00D7", // ×
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) { onIntent(AmountInputIntent.OperatorPressed(MathOperator.Multiply)) }
                KeypadButton(text = "4") { onIntent(AmountInputIntent.DigitPressed("4")) }
                KeypadButton(text = "5") { onIntent(AmountInputIntent.DigitPressed("5")) }
                KeypadButton(text = "6") { onIntent(AmountInputIntent.DigitPressed("6")) }

            }

            // ردیف سوم
            KeypadRow {
                KeypadButton(
                    text = "\u2212", // −
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) { onIntent(AmountInputIntent.OperatorPressed(MathOperator.Subtract)) }
                KeypadButton(text = "1") { onIntent(AmountInputIntent.DigitPressed("1")) }
                KeypadButton(text = "2") { onIntent(AmountInputIntent.DigitPressed("2")) }
                KeypadButton(text = "3") { onIntent(AmountInputIntent.DigitPressed("3")) }
            }

            // ردیف چهارم
            KeypadRow {
                KeypadButton(
                    text = "+",
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) { onIntent(AmountInputIntent.OperatorPressed(MathOperator.Add)) }
                KeypadButton(text = if (currencyFractionDigits > 0) "." else "00") {
                    if (currencyFractionDigits > 0) onIntent(AmountInputIntent.DecimalPressed)
                    else onIntent(AmountInputIntent.DoubleZeroPressed)
                }
                KeypadButton(text = "0") { onIntent(AmountInputIntent.DigitPressed("0")) }
                KeypadButton(icon = Icons.Rounded.Backspace) { onIntent(AmountInputIntent.BackspacePressed) }
            }

            KeypadRow {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { onIntent(AmountInputIntent.EqualsPressed) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "=",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }


                val locale = LocalAppLocale.current
                val resources = rememberLocalizeResources(locale)
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onIntent(AmountInputIntent.DoneButtonClick) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = resources.getString(R.string.amout_input_bottomsheet_bottom_text),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}


@Composable
private fun KeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun RowScope.KeypadButton(
    text: String = "",
    icon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .weight(1f)
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun AmountInputDisplay(amount: String, modifier: Modifier = Modifier, onClearPressed: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = amount.ifEmpty { "0" },
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            letterSpacing = (-1).sp
        )

        Spacer(Modifier.width(16.dp))

        TextButton(
            onClick = onClearPressed,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(
                text = "C",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}