package af.amir.hesabham.presentation.core.model.accounttype

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.presentation.core.model.UiText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.ui.graphics.vector.ImageVector

data class AccountTypeUiModel(
    val type: AccountType,
    val text: UiText,
    val icon: ImageVector,
)

fun AccountType.toUi() = when (this) {
    AccountType.CASH -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_cash),
        icon = Icons.Rounded.AttachMoney
    )

    AccountType.BANK -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_bank),
        icon = Icons.Rounded.AccountBalance
    )
    AccountType.CARD -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_card),
        icon = Icons.Rounded.CreditCard
    )
    AccountType.SAVING -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_saving),
        icon = Icons.Rounded.Savings
    )
    AccountType.DIGITAL_WALLET -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_digital_wallet),
        icon = Icons.Rounded.CurrencyBitcoin
    )
    AccountType.OTHER -> AccountTypeUiModel(
        type = this,
        text = UiText.Resource(R.string.account_type_other),
        icon = Icons.Rounded.Inventory2
    )
}


