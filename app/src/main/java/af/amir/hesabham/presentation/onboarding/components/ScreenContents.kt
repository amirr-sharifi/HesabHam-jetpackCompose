package af.amir.hesabham.presentation.onboarding.components

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.presentation.core.components.AmountTextField
import af.amir.hesabham.presentation.core.components.amountinput.AmountInputBottomSheet
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.*
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import af.amir.hesabham.presentation.core.model.accounttype.toUi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPageContent(
    accountName: String,
    accountType: AccountType,
    initialBalance: BigDecimal,
    onAccountNameChange: (String) -> Unit,
    onAccountTypeChange: (AccountType) -> Unit,
    onInitialBalanceChange: (BigDecimal) -> Unit,
) {
    val currentLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(currentLocale)
    var isAmountInputOpen by rememberSaveable { mutableStateOf(false) }


    Text(
        resources.getString(R.string.onboarding_account_name_title),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
    )
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(value = accountName, onValueChange = onAccountNameChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(resources.getString(R.string.onboarding_account_placeholder))
        },
        label = {
            Text(resources.getString(R.string.onboarding_account_hint))
        }
    )
    Text(
        resources.getString(R.string.onboarding_account_name_message),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = MaterialTheme.typography.labelSmall.fontSize
    )




    Spacer(Modifier.height(12.dp))
    Text(
        resources.getString(R.string.onboarding_balance_title),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
    )
    Spacer(Modifier.height(4.dp))
    val formattedAmount = rememberFormattedAmount(initialBalance.toPlainString(),currentLocale)
    AmountTextField(
        "0",
        formattedAmount,
        Modifier.fillMaxWidth()
    ) { isAmountInputOpen = true }

    Text(
        resources.getString(R.string.onboarding_balance_message),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = MaterialTheme.typography.labelSmall.fontSize
    )





    Spacer(Modifier.height(12.dp))

    var typeDropDownExpanded by remember{ mutableStateOf(false) }

    Text(
        resources.getString(R.string.onboarding_account_type_title),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
    )
    Spacer(Modifier.height(4.dp))
    ExposedDropdownMenuBox(
        expanded = typeDropDownExpanded,
        onExpandedChange = { typeDropDownExpanded = !typeDropDownExpanded },
    ) {
        OutlinedTextField(
            value = accountType.toUi().text.asString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = resources.getString(R.string.drop_down_choose_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropDownExpanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = typeDropDownExpanded,
            onDismissRequest = { typeDropDownExpanded = false }
        ) {
            AccountTypeRegistry.items.forEach { type ->
                DropdownMenuItem(
                    text = { Text(text = type.text.asString()) },
                    onClick = {
                        onAccountTypeChange(type.type)
                        typeDropDownExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
    Text(
        resources.getString(R.string.onboarding_account_type_message),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = MaterialTheme.typography.labelSmall.fontSize
    )



    if (isAmountInputOpen)
        AmountInputBottomSheet(
            onDismissRequest = { isAmountInputOpen = false },
            defaultValue = initialBalance
        ) {
            isAmountInputOpen = false
            onInitialBalanceChange(it)
        }

}

@Composable
fun CurrencyPageContent(
    currencies: List<CurrencyUiModel>,
    selectedCurrency: CurrencyUiModel? = null,
    onCurrencyChange: (CurrencyUiModel) -> Unit,
) {
    val currentLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(currentLocale)

    Text(
        resources.getString(R.string.onboarding_currency_title),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Text(
        resources.getString(R.string.onboarding_currency_message),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))

    Box() {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencies.forEach { currency ->
                SelectableChip(
                    selected = selectedCurrency == currency,
                    text = currency.displayName,
                    textFontSize = MaterialTheme.typography.bodySmall.fontSize
                ) {
                    onCurrencyChange(currency)
                }
            }
        }
    }
}


@Composable
fun LanguageCalendarContent(
    selectedLocale: AppLocale,
    selectedCalendar: AppCalendar,
    onCalendarChanged: (AppCalendar) -> Unit,
    onLocaleChanged: (AppLocale) -> Unit,
) {

    val currentLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(currentLocale)
    Text(
        resources.getString(R.string.onboarding_language_title),
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Row {
        SelectableChip(
            selected = selectedLocale == AppLocale.English,
            onClick = { onLocaleChanged(AppLocale.English) },
            text = "English"
        )
        Spacer(Modifier.width(8.dp))
        SelectableChip(
            selected = selectedLocale == AppLocale.Persian,
            onClick = { onLocaleChanged(AppLocale.Persian) },
            text = "فارسی"
        )
    }
    Text(
        resources.getString(R.string.onboarding_language_message),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )


    Spacer(Modifier.height(32.dp))


    Text(
        resources.getString(R.string.onboarding_calendar_title),
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Row {
        SelectableChip(
            selected = selectedCalendar == AppCalendar.Gregorian,
            onClick = { onCalendarChanged(AppCalendar.Gregorian) },
            text = resources.getString(R.string.Gregorian)
        )
        Spacer(Modifier.width(8.dp))
        SelectableChip(
            selected = selectedCalendar == AppCalendar.Jalali,
            onClick = { onCalendarChanged(AppCalendar.Jalali) },
            text = resources.getString(R.string.jalali)
        )
    }
    Text(
        resources.getString(R.string.onboarding_calendar_message),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )





}