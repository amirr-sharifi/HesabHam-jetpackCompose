package af.amir.hesabham.presentation.add_transaction.components.fullscreendialog

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


fun androidx.compose.foundation.lazy.LazyListScope.archivedCategoriesSection(
    archived: List<CategoryUiModel>,
    onRestore: (CategoryUiModel) -> Unit,
) {
    if (archived.isEmpty()) return

    item(key = "archived_header") {
        var expanded by rememberSaveable { mutableStateOf(false) }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            ArchivedHeaderRow(count = archived.size, expanded = expanded) {
                expanded = !expanded
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(300, easing = FastOutSlowInEasing)) + fadeIn(
                    tween(
                        250
                    )
                ),
                exit = shrinkVertically(tween(250, easing = FastOutSlowInEasing)) + fadeOut(
                    tween(
                        150
                    )
                )
            ) {
                Column(
                    Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    archived.forEach { category ->
                        ArchivedAccountRow(
                            category = category,
                            onRestore = { onRestore(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivedHeaderRow(count: Int, expanded: Boolean, onToggle: () -> Unit) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "chevron"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Inventory2,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            resources.getString(R.string.archived_categories),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(0.16f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) { Text("$count", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary) }

        Spacer(Modifier.weight(1f))

        Icon(
            Icons.Rounded.KeyboardArrowDown, null,
            tint = Color.Gray.copy(0.8f),
            modifier = Modifier.rotate(rotation)
        )
    }
}

@Composable
private fun ArchivedAccountRow(category: CategoryUiModel, onRestore: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Gray.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                category.icon,
                null,
                tint = Color.Gray.copy(0.8f),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            category.title.asString(),
            fontSize = 14.sp,
            color = Color(0xFF55605D),
            modifier = Modifier.weight(1f)
        )



        Box(
            Modifier
                .clip(CircleShape)
                .clickable(onClick = onRestore)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.RestartAlt,
                contentDescription = "restore",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
