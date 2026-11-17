package af.amir.hesabham.presentation.core.config.setting

import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.presentation.core.model.CurrencyUiModel
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale


//Locale
val LocalAppLocale = staticCompositionLocalOf<Locale> {
    error("LocalAppLocale not provided!")
}

@Composable
fun rememberLocalizeResources(locale: Locale): Resources {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    return remember(locale,configuration,context) {
        val config = Configuration(configuration).apply {
            setLocale(locale)
        }
        context.createConfigurationContext(config).resources
    }
}


//Layout
fun layoutDirectionFor(locale: AppLocale): LayoutDirection {
    return when (locale) {
        AppLocale.English -> LayoutDirection.Ltr
        AppLocale.Persian -> LayoutDirection.Rtl
    }
}


//Currency
val LocalAppCurrency = staticCompositionLocalOf<CurrencyUiModel> {
    error("LocalAppCurrency not provided")
}

//Calendar
val LocalAppCalendar = staticCompositionLocalOf<AppCalendar> {
    error("LocalAppCalendar not provided")
}