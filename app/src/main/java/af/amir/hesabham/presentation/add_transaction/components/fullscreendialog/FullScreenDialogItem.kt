package af.amir.hesabham.presentation.add_transaction.components.fullscreendialog

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenDialogItem(
    title: String,
    icon: ImageVector?,
    iconColor: Color,
    showDropDown: Boolean,
    showArchiveActionButton: Boolean = true,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    var dropDownExtended by remember { mutableStateOf(false) }

    AppCard(elevation = 0.dp) {
        Row(
            modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon?.let {
                Box(
                    Modifier
                        .size(48.dp)
                        .background(iconColor.copy(alpha = 0.2f), MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, "", Modifier.size(32.dp), tint = iconColor)
                }
            }

            Text(title, Modifier.weight(1f))
            if (showDropDown) {
                Box {
                    IconButton(
                        onClick = { dropDownExtended = true }
                    ) {
                        Icon(
                            Icons.Rounded.MoreVert,
                            contentDescription = "More",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        containerColor = MaterialTheme.colorScheme.surface,
                        expanded = dropDownExtended,
                        onDismissRequest = { dropDownExtended = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(resources.getString(R.string.category_dropdown_edit))
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Edit, null)
                            },
                            onClick = {
                                dropDownExtended = false
                                onEdit()
                            }
                        )

                        if (showArchiveActionButton)
                            DropdownMenuItem(
                                text = {
                                    Text(resources.getString(R.string.archive))
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.DeleteOutline, null)
                                },
                                onClick = {
                                    dropDownExtended = false
                                    onDelete()
                                }
                            )
                    }
                }
            }

        }
    }
}