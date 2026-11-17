package af.amir.hesabham.presentation.core.mapper

import af.amir.hesabham.domain.model.settings.AppCurrency
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import java.util.Locale

fun AppCurrency.toUiModel(locale: Locale): CurrencyUiModel {
    val currency = java.util.Currency.getInstance(this.code)
    return CurrencyUiModel(
        displayName = currency.getDisplayName(locale),
        code = this.code,
        symbol = currency.symbol,
        fractionDigits = currency.defaultFractionDigits
    )
}

fun CurrencyUiModel.toDomain() = AppCurrency(this.code)