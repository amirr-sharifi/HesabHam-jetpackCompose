package af.amir.hesabham.presentation.core.config

import af.amir.hesabham.domain.model.settings.AppLocale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.text.NumberFormat
import java.util.Locale

@Composable
fun rememberFormattedAmount(amount: String, locale: Locale) =
    remember(amount, locale) {
        amount.toLongOrNull()?.let {
            NumberFormat.getNumberInstance(locale).format(it)
        } ?: amount
    }
