package af.amir.hesabham.presentation.core.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.AppBarState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ExpenseTopBar(state: AppBarState, onNavigateUp: () -> Unit) {

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    val (icon, title) = Pair(state.appbarIcon, state.appbarTitle)

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        title = {
            AnimatedContent(
                targetState = title,
                label = "TitleAnimation",
                transitionSpec = {
                    slideInHorizontally(animationSpec = tween(300)) { -it / 2 } + fadeIn(
                        animationSpec = tween(300)
                    ) with slideOutHorizontally(animationSpec = tween(300)) { -it / 2 } + fadeOut(
                        tween(
                            300
                        )
                    )
                }
            ) { newTitle ->
                Text(
                    text = resources.getString(newTitle),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }, navigationIcon = {
            AnimatedContent(
                targetState = icon,
                label = "TitleAnimation",
                transitionSpec = {
                    fadeIn(tween(300)) with fadeOut(tween(300))
                }
            ) { newIcon ->

                IconButton(
                    onClick = onNavigateUp,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(ImageVector.vectorResource(newIcon), "")
                }
            }
        }
    )

}