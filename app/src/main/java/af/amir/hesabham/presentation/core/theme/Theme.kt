package af.amir.hesabham.presentation.core.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * تابع کمکی و ایمن برای استخراج Activity از Context در Jetpack Compose.
 * استفاده از این روش برای جلوگیری از ClassCastException در دیالوگ‌ها الزامی است.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E5753),
    onPrimaryContainer = Color.White,

    secondary = BrandPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E5753),
    onSecondaryContainer = Color.White,

    tertiary = IncomeGreen,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF215F41),
    onTertiaryContainer = Color.White,

    // اعمال کانتراست در حالت تاریک
    background = BackgroundDark, // تیره‌تر
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,       // روشن‌تر برای کاردها و دیالوگ‌ها
    onSurface = TextPrimaryDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline = Color(0xFF3A3A3A),
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = Color(0xFF6A2A23),
    onErrorContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimarySoft,
    onPrimaryContainer = BrandPrimary,

    secondary = BrandPrimary,
    onSecondary = Color.White,
    secondaryContainer = BrandPrimarySoft,
    onSecondaryContainer = BrandPrimary,

    tertiary = IncomeGreen,
    onTertiary = Color.White,
    tertiaryContainer = IncomeGreenSoft,
    onTertiaryContainer = IncomeGreen,

    // اعمال کانتراست در حالت روشن
    background = BackgroundLight, // خاکستری بسیار ملایم (سرد)
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,       // کاملا سفید برای برجسته شدن کاردها
    onSurface = TextPrimaryLight,

    surfaceVariant = Color(0xFFE5E5E5), // کمی تیره‌تر از surface برای تفکیک المان‌ها
    onSurfaceVariant = TextSecondaryLight,

    outline = BorderDefault,
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = ExpenseRedSoft,
    onErrorContainer = ExpenseRed
)

@Composable
fun HesabHamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    // اعمال تنظیمات Status Bar به صورت ایمن
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()

                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                // پیشنهاد: برای زیبایی بیشتر، بک‌گراند نویگیشن‌بار را نیز همگام کنید
                // it.navigationBarColor = colorScheme.background.toArgb()
                // insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // در صورت نیاز مطمئن شوید این مورد ایمپورت شده است
        content = content
    )
}