package af.amir.hesabham.presentation.home.screens.settings.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedThemeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = checked, label = "SwitchState")

    val trackBackgroundColor by transition.animateColor(
        label = "TrackColor",
        transitionSpec = { tween(durationMillis = 350) }
    ) { isDark ->
        if (isDark) Color(0xFF22886F) else Color(0xFFD6DCD9)
    }

    val thumbOffsetPercent by transition.animateFloat(
        label = "ThumbOffset",
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        }
    ) { isDark ->
        if (isDark) 1f else 0f
    }

    val iconRotation by transition.animateFloat(
        label = "IconRotation",
        transitionSpec = { tween(durationMillis = 400, easing = FastOutSlowInEasing) }
    ) { isDark ->
        if (isDark) 360f else 0f
    }

    val iconColor by transition.animateColor(label = "IconColor") { isDark ->
        if (isDark) Color(0xFFF4C550) else Color(0xFF1B6F5C)
    }

    Box(
        modifier = modifier
            .width(54.dp)
            .height(30.dp)
            .clip(CircleShape)
            .background(trackBackgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxTravel = maxWidth - maxHeight
            val currentOffset = maxTravel * thumbOffsetPercent

            Box(
                modifier = Modifier
                    .offset(x = currentOffset)
                    .size(maxHeight)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (checked) Icons.Rounded.NightsStay else Icons.Rounded.WbSunny,
                    contentDescription = "Theme Switch Icon",
                    tint = iconColor,
                    modifier = Modifier
                        .size(15.dp)
                        .rotate(iconRotation)
                )
            }
        }
    }
}