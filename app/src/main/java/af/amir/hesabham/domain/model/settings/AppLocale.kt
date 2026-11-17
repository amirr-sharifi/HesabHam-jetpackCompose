package af.amir.hesabham.domain.model.settings

import java.util.Locale

enum class AppLocale(val locale: Locale) {
    English(Locale.ENGLISH), Persian(Locale("fa"))
}
