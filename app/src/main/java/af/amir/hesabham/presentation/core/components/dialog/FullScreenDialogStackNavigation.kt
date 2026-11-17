package af.amir.hesabham.presentation.core.components.dialog

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier


private data class StackEntry<T>(val screen: T, val depth: Int)

@Composable
fun <T : Any> FullScreenDialogStackNavigation(
    backStack: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val currentDestination = backStack.lastOrNull()

    val currentEntry = StackEntry(currentDestination, backStack.size)

    val duration = 400

    val saveableStateHolder = rememberSaveableStateHolder()
    AnimatedVisibility(
        visible = backStack.isNotEmpty(),
        enter = slideInHorizontally(
            animationSpec = tween(duration)
        ) { fullWidth -> -fullWidth },
        exit = slideOutHorizontally (
            animationSpec = tween(duration)
        ) { fullWidth -> -fullWidth }
    ) {
        AnimatedContent(
            targetState = currentEntry,
            modifier = modifier.fillMaxSize(),
            transitionSpec = {
                val isPush = targetState.depth > initialState.depth
                if (isPush) {
                    val enter = slideInHorizontally(
                        animationSpec = tween(duration)
                    ) { fullWidth -> -fullWidth }

                    val exit = slideOutHorizontally(
                        animationSpec = tween(duration)
                    ) { fullWidth -> (fullWidth * 0.2f).toInt() }

                    (enter togetherWith exit).apply {
                        targetContentZIndex = 1f
                    }
                } else {
                    val enter = slideInHorizontally (
                        animationSpec = tween(duration)
                    ) { fullWidth -> (fullWidth * 0.2f).toInt() }

                    val exit = slideOutHorizontally(
                        animationSpec = tween(duration)
                    ) { fullWidth -> -fullWidth }

                    (enter togetherWith exit).apply {
                        targetContentZIndex = -1f
                    }
                }
            },
            contentKey = { it.screen.hashCode() },
            label = "StackTransition"
        ) { tStackEntry ->
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                saveableStateHolder.SaveableStateProvider(tStackEntry.hashCode()) {
                    tStackEntry.screen?.let {
                        content(it)
                    }
                }

            }
        }
    }

}
