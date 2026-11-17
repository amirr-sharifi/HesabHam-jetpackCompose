package af.amir.hesabham.presentation.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- Brand Colors ---
val BrandPrimary = Color(0xFF29756F)
val BrandPrimaryDark = Color(0xFF175952)
val BrandPrimarySoft = Color(0x1A29756F) // 10% alpha

// --- Background & Surface (تنظیمات جدید برای عمق‌دهی) ---
// حالت روشن: بک‌گراند کمی خاکستری/سرد، کاردها کاملا سفید
val BackgroundLight = Color(0xFFF3F4F6)
val SurfaceLight = Color(0xFFFFFFFF)

// حالت تاریک: بک‌گراند بسیار تیره (نزدیک به مشکی)، کاردها خاکستری تیره
val BackgroundDark = Color(0xFF09090B)
val SurfaceDark = Color(0xFF1E1E1E)
val SurfaceVariantDark = Color(0xFF2C2C2C)

// --- Text Colors ---
val TextPrimaryLight = Color(0xFF121212)
val TextSecondaryLight = Color(0xFF666666)
val TextDisabledLight = Color(0xFF9E9E9E)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFB3B3B3)
val TextDisabledDark = Color(0xFF6F6F6F)

// --- Utility & Borders ---
val BorderDefault = Color(0xFF438883)
val DividerLight = Color(0x1F000000) // 12% black
val DividerDark = Color(0x1FFFFFFF)  // 12% white

// --- Semantic/Status Colors ---
val IncomeGreen = Color(0xFF3CB371)
val IncomeGreenSoft = Color(0xFFDFF3EA)
val ExpenseRed = Color(0xFFE5533D)
val ExpenseRedSoft = Color(0xFFFBE4Df)
val WarningYellow = Color(0xFFFFCA28)

// --- Gradients ---
val PrimaryButtonColor = Brush.horizontalGradient(listOf(BrandPrimary, BrandPrimaryDark))