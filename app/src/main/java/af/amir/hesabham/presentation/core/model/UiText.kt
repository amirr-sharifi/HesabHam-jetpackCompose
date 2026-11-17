package af.amir.hesabham.presentation.core.model

import af.amir.hesabham.presentation.core.config.setting.*
import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

sealed class UiText {
    data class Dynamic(val text: String) : UiText()
    data class Resource(@StringRes val textResId: Int) : UiText()

    @Composable
    fun asString(): String {
        val locale = LocalAppLocale.current
        val resources = rememberLocalizeResources(locale)
        return when (this) {
            is Dynamic -> text?:""
            is Resource -> resources.getString(textResId)
        }
    }

    fun asString(resources : Resources): String {
        return when (this) {
            is Dynamic -> text?:""
            is Resource -> resources.getString(textResId)
        }
    }


}