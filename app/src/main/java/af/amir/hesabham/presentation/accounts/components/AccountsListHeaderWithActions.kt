package af.amir.hesabham.presentation.accounts.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.accounts.SortOption
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeUiModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun AccountsListHeaderWithActions(
    selectedFilter: AccountTypeUiModel?,
    selectedSort: SortOption,
    onFilterSelected: (AccountTypeUiModel?) -> Unit,
    onSortSelected: (SortOption) -> Unit,
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val resources = rememberLocalizeResources(LocalAppLocale.current)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = resources.getString(R.string.accounts_list),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                FilterChipButton(
                    title = selectedFilter?.text?.asString() ?: resources.getString(R.string.all),
                    icon = Icons.Rounded.FilterList,
                    isActive = selectedFilter != null,
                    onClick = { showFilterMenu = true }
                )

                DropdownMenu(
                    containerColor = MaterialTheme.colorScheme.surface,
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    offset = DpOffset(0.dp, 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = resources.getString(R.string.all),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.AllInclusive,
                                contentDescription = null,
                                tint = if (selectedFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            onFilterSelected(null)
                            showFilterMenu = false
                        }
                    )
                    AccountTypeRegistry.items.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = type.text.asString(),
                                    fontWeight = if (type == selectedFilter) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = null,
                                    tint = if (type == selectedFilter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                onFilterSelected(type)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            }

            Box {
                FilterChipButton(
                    title = resources.getString(selectedSort.titleRes),
                    icon = Icons.AutoMirrored.Rounded.Sort,
                    isActive = selectedSort != SortOption.DEFAULT,
                    onClick = { showSortMenu = true }
                )

                DropdownMenu(
                    containerColor = MaterialTheme.colorScheme.surface,
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    offset = DpOffset(0.dp, 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    SortOption.entries.forEach { sort ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = resources.getString(sort.titleRes),
                                    fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortSelected(sort)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = if (!isActive) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        ) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}