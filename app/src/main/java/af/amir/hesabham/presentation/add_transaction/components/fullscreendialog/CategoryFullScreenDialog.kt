package af.amir.hesabham.presentation.add_transaction.components.fullscreendialog

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.setting.*
import af.amir.hesabham.presentation.core.model.CategorySection
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryFullScreenDialog(
    modifier: Modifier = Modifier,
    categorySection: List<CategorySection>,
    archivedCategories: List<CategoryUiModel>,
    onDismissRequest: () -> Unit,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onEditCategory: (category: CategoryUiModel, parent: CategoryUiModel?) -> Unit,
    onAddClick: (CategoryUiModel?) -> Unit,
    onArchiveCategory: (CategoryUiModel) -> Unit,
    onRestoreCategory: (CategoryUiModel) -> Unit,
) {
    val expandedHeaderIds = remember { mutableStateListOf<String>() }

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    BackHandler {
        onDismissRequest()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
    ) {


        Text(
            resources.getString(R.string.category_dialog_subtitle),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categorySection, key = { it.header.id }) { section ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val header = section.header
                    FullScreenDialogItem(
                        title = header.title.asString(),
                        icon = header.icon,
                        iconColor = header.color,
                        showDropDown = !header.isDefault,
                        showArchiveActionButton = categorySection.isEmpty(),
                        onEdit = { onEditCategory(header, null) },
                        onDelete = { onArchiveCategory(header) },
                        onItemClick = {
                            if (expandedHeaderIds.contains(header.id)) expandedHeaderIds -= header.id else expandedHeaderIds += header.id
                        }
                    )

                    if (expandedHeaderIds.contains(header.id)) {
                        section.children.forEach { category ->
                            FullScreenDialogItem(
                                title = category.title.asString(),
                                icon = category.icon,
                                iconColor = category.color,
                                showDropDown = !category.isDefault,
                                modifier = Modifier.padding(start = 24.dp),
                                onEdit = { onEditCategory(category, header) },
                                onDelete = { onArchiveCategory(category) },
                                onItemClick = {
                                    onCategoryClick(category)
                                    onDismissRequest()
                                }
                            )
                        }
                        FullScreenDialogAddItem(Modifier.padding(start = 24.dp)) { onAddClick(header) }
                    }

                }

            }
            item {
                FullScreenDialogAddItem { onAddClick(null) }
            }

            if (archivedCategories.isNotEmpty()){
                item {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                }
            }

            archivedCategoriesSection(archivedCategories, onRestoreCategory)
        }
    }


}





