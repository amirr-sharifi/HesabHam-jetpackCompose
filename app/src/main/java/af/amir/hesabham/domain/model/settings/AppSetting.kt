package af.amir.hesabham.domain.model.settings

data class AppSetting(
    val calendar: AppCalendar,
    val currency: AppCurrency,
    val locale : AppLocale,
    val isOnboardingCompleted : Boolean,
    val darkMode : Boolean
)
