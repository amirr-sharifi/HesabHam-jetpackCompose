package af.amir.hesabham.presentation.core.model

import af.amir.hesabham.presentation.core.config.setting.*
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

sealed class UiIcon {
    data class Dynamic(val icon: ImageVector) : UiIcon()
    data class Resource(@DrawableRes val iconResId: Int) : UiIcon()

    @Composable
    fun asImageVector(): ImageVector {
        return when (this) {
            is Dynamic -> icon
            is Resource -> ImageVector.vectorResource(iconResId)
        }
    }

}