package af.amir.hesabham.presentation.core.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AppBarState(
    @StringRes
    val appbarTitle: Int,
    @DrawableRes
    val appbarIcon: Int,
)
