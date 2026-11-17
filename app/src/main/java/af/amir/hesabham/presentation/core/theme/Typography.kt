package af.amir.hesabham.presentation.core.theme

import af.amir.hesabham.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val VazirFontFamily = FontFamily(

    Font(R.font.vazir_thin, FontWeight.Thin),

    Font(R.font.vazir_extra_light, FontWeight.ExtraLight),

    Font(R.font.vazir_light, FontWeight.Light),

    Font(R.font.vazir_regular, FontWeight.Normal),

    Font(R.font.vazir_medium, FontWeight.Medium),

    Font(R.font.vazir_semi_bold, FontWeight.SemiBold),

    Font(R.font.vazir_bold, FontWeight.Bold),

    Font(R.font.vazir_extra_bold, FontWeight.ExtraBold),

    Font(R.font.vazir_black, FontWeight.Black),
)

val AppTypography = Typography(

    bodyLarge = TextStyle(
        fontFamily = VazirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    titleLarge = TextStyle(
        fontFamily = VazirFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = VazirFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp
    ),

    labelMedium = TextStyle(
        fontFamily = VazirFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)