package af.amir.hesabham.presentation.core.components.account_editor

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.presentation.core.components.PrimaryButton
import af.amir.hesabham.presentation.core.components.amountinput.AmountInputBottomSheet
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeUiModel
import af.amir.hesabham.presentation.core.theme.BrandPrimary
import af.amir.hesabham.presentation.core.theme.BrandPrimaryDark
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import kotlin.math.sin

private val SoftGray = Color.Gray.copy(0.15f)

@Composable
fun AccountEditorDialog(
    state: AccountEditorState,
    onDismissRequest: () -> Unit,
    onSaveClick: (state: AccountEditorState) -> Unit,
) {
    Log.e("TAG", "AccountEditorDialog: ${state.balance}", )
    BackHandler {
        onDismissRequest()
    }

    AccountEditorScreen(
        state = state,
        onDismissRequest = onDismissRequest,
        onSaveClick = onSaveClick
    )
}

@Composable
private fun AccountEditorScreen(
    state: AccountEditorState,
    onDismissRequest: () -> Unit,
    onSaveClick: (state: AccountEditorState) -> Unit,
) {
    var accountName by remember { mutableStateOf(state.account.name) }
    var selectedAccountType by remember { mutableStateOf(state.account.type) }
    var titleIsError by remember { mutableStateOf(false) }

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    var isAmountInputOpen by remember { mutableStateOf(false) }
    var initialBalance by remember { mutableStateOf(state.balance) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            AmountHeaderSection(
                amount = initialBalance,
                onClick = { isAmountInputOpen = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(0.04f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ورودی نام حساب
                    Text(
                        text = resources.getString(R.string.account_editor_account_name),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = accountName,
                        onValueChange = {
                            accountName = it
                            if (titleIsError && it.isNotBlank()) titleIsError = false
                        },
                        placeholder = { Text(resources.getString(R.string.new_account_title_place_holder), color = Color.Gray.copy(0.6f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = null,
                                tint = if (titleIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = titleIsError,
                        supportingText = if (titleIsError) {
                            { Text(resources.getString(R.string.title_cant_be_empty_error)) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SoftGray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = SoftGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))


                    Text(
                        text = resources.getString(R.string.account_editor_account_type),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AccountTypeGrid(
                        selectedType = selectedAccountType,
                        onTypeSelected = { selectedAccountType = it }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                title = resources.getString(R.string.save),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                if (accountName.isBlank()) {
                    titleIsError = true
                } else {
                    val newAccount = state.account.copy(
                        name = accountName,
                        type = selectedAccountType
                    )
                    onSaveClick(state.copy(account = newAccount, balance = initialBalance))
                    onDismissRequest()
                }
            }
        }
    }

    if (isAmountInputOpen) {
        AmountInputBottomSheet(
            onDismissRequest = { isAmountInputOpen = false },
            defaultValue = initialBalance
        ) {
            isAmountInputOpen = false
            initialBalance = it
        }
    }
}

@Composable
fun AmountHeaderSection(amount: BigDecimal, onClick: () -> Unit) {
    val formattedAmount = rememberFormattedAmount(amount.toPlainString(), LocalAppLocale.current)
    val resources = rememberLocalizeResources(LocalAppLocale.current)

    val currency = LocalAppCurrency.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(BrandPrimary, BrandPrimaryDark)))
            .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val wavePath = Path()
            wavePath.moveTo(0f, size.height * 0.6f)
            for (i in 0..size.width.toInt() step 5) {
                val x = i.toFloat()
                val y = (sin(x * 0.04) * 15f).toFloat() + size.height * 0.65f
                wavePath.lineTo(x, y)
            }
            wavePath.lineTo(size.width, size.height)
            wavePath.lineTo(0f, size.height)
            wavePath.close()
            drawPath(wavePath, color = Color.White.copy(alpha = 0.05f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = resources.getString(R.string.account_editor_initial_amount),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formattedAmount,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currency.symbol,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }
    }
}

@Composable
private fun AccountTypeGrid(
    selectedType: AccountType,
    onTypeSelected: (AccountType) -> Unit,
) {
    val items = AccountTypeRegistry.items
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (i in items.indices step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (j in 0 until 3) {
                    if (i + j < items.size) {
                        AccountTypeItem(
                            type = items[i + j],
                            isSelected = selectedType == items[i + j].type,
                            onClick = { onTypeSelected(items[i + j].type) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTypeItem(
    type: AccountTypeUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val iconBgColor = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val iconColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = if (!isSelected) BorderStroke(1.dp, SoftGray) else null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = type.text.asString(),
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = type.text.asString(),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}