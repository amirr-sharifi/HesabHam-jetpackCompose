
package af.amir.hesabham.presentation.core.components.category_editor

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.category.CategoryType
import af.amir.hesabham.presentation.core.components.PrimaryButton
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import af.amir.hesabham.presentation.core.model.UiText
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val expenseIcons: List<ImageVector> = listOf(
    Icons.Rounded.Restaurant,
    Icons.Rounded.RestaurantMenu,
    Icons.Rounded.LocalCafe,
    Icons.Rounded.LocalGroceryStore,
    Icons.Rounded.LunchDining,
    Icons.Rounded.DirectionsCar,
    Icons.Rounded.LocalGasStation,
    Icons.Rounded.DirectionsBus,
    Icons.Rounded.LocalTaxi,
    Icons.Rounded.DirectionsTransit,
    Icons.Rounded.Flight,
    Icons.Rounded.Home,
    Icons.Rounded.Apartment,
    Icons.Rounded.Bolt,
    Icons.Rounded.WaterDrop,
    Icons.Rounded.Wifi,
    Icons.Rounded.Build,
    Icons.Rounded.HomeRepairService,
    Icons.Rounded.LocalHospital,
    Icons.Rounded.MedicalServices,
    Icons.Rounded.LocalPharmacy,
    Icons.Rounded.HealthAndSafety,
    Icons.Rounded.MedicalInformation,
    Icons.Rounded.ShoppingBag,
    Icons.Rounded.Checkroom,
    Icons.Rounded.Devices,
    Icons.Rounded.Weekend,
    Icons.Rounded.Movie,
    Icons.Rounded.Theaters,
    Icons.Rounded.SportsEsports,
    Icons.Rounded.Event,
    Icons.Rounded.Hotel,
    Icons.Rounded.Subscriptions,
    Icons.Rounded.LiveTv,
    Icons.Rounded.School,
    Icons.Rounded.MenuBook,
    Icons.Rounded.CastForEducation,
    Icons.Rounded.People,
    Icons.Rounded.ChildCare,
    Icons.Rounded.Cake,
    Icons.Rounded.Celebration,
    Icons.Rounded.ReceiptLong,
    Icons.Rounded.Code,
    Icons.Rounded.CalendarMonth,
    Icons.Rounded.Schedule,
    Icons.Rounded.Replay
)
private val incomeIcons: List<ImageVector> = listOf(
    Icons.Rounded.AccountBalance,
    Icons.Rounded.AccountBalanceWallet,
    Icons.Rounded.Payments,
    Icons.Rounded.PointOfSale,
    Icons.Rounded.BusinessCenter,
    Icons.Rounded.ShowChart,
    Icons.Rounded.StackedLineChart,
    Icons.Rounded.CurrencyBitcoin,
    Icons.Rounded.TrendingUp,
    Icons.Rounded.Savings,
    Icons.Rounded.CardGiftcard,
    Icons.Rounded.Redeem
)
private val CategoryColors = listOf(
    Color(0xFFF28C28), // Orange
    Color(0xFFFFD54F), // Yellow
    Color(0xFF108971), // Green
    Color(0xFF00ACC1), // Teal
    Color(0xFF4285F4), // Blue
    Color(0xFF7E57C2), // Purple
    Color(0xFFEC407A)  // Pink
)



private val SoftGray = Color.Gray.copy(alpha = 0.15f)

@Composable
fun CategoryEditorDialog(
    categoryEditorState: CategoryEditorState,
    onDismissRequest: () -> Unit,
    onSave: (CategoryUiModel) -> Unit,
) {
    BackHandler {
        onDismissRequest()
    }

    CategoryEditorScreen(
        categoryEditorState = categoryEditorState,
        onDismissRequest = onDismissRequest,
        onSave = {
            onSave(it)
            onDismissRequest()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryEditorScreen(
    categoryEditorState: CategoryEditorState,
    onDismissRequest: () -> Unit,
    onSave: (CategoryUiModel) -> Unit,
) {
    BackHandler {
        onDismissRequest()
    }

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    val category by remember { mutableStateOf(categoryEditorState.category) }
    var currentParent by remember { mutableStateOf(categoryEditorState.parentCategory) }

    var categoryTitle by remember { mutableStateOf(category.title.asString(resources)) }
    var titleIsError by remember { mutableStateOf(false) }
    var categoryType by remember { mutableStateOf(category.type) }

    var selectedColor by remember {
        mutableStateOf(CategoryColors.firstOrNull { it == category.color } ?: CategoryColors[0])
    }

    val animatedThemeColor by animateColorAsState(
        targetValue = selectedColor,
        animationSpec = tween(400),
        label = "ThemeColorAnim"
    )

    var selectedIcon by remember(categoryType) {
        mutableStateOf(
            when (categoryType) {
                CategoryType.INCOME -> incomeIcons.firstOrNull { it == category.icon } ?: incomeIcons[0]
                else -> expenseIcons.firstOrNull { it == category.icon } ?: expenseIcons[0]
            }
        )
    }

    var iconsExpanded by remember { mutableStateOf(false) }
    var parentCategoriesExpanded by remember { mutableStateOf(false) }

    val categories by remember(categoryType) {
        mutableStateOf(if (categoryType == CategoryType.INCOME) categoryEditorState.incomeParents else categoryEditorState.expenseParents)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(0.04f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionTitle(resources.getString(R.string.add_category_name_and_icon))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconBox(
                            size = 56.dp,
                            color = animatedThemeColor,
                            icon = selectedIcon,
                            onClick = { iconsExpanded = !iconsExpanded }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = categoryTitle,
                            onValueChange = {
                                categoryTitle = it
                                if (titleIsError && it.isNotBlank()) titleIsError = false
                            },
                            placeholder = { Text(resources.getString(R.string.add_category_placeholder_exaple), color = Color.Gray.copy(0.6f)) },
                            isError = titleIsError,
                            supportingText = if (titleIsError) { { Text(resources.getString(R.string.title_cant_be_empty_error)) } } else null,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SoftGray,
                                focusedBorderColor = animatedThemeColor,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = animatedThemeColor.copy(alpha = 0.02f)
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = iconsExpanded,
                        enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                        exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 6,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val currentIcons = if (categoryType == CategoryType.INCOME) incomeIcons else expenseIcons
                                currentIcons.forEach { icon ->
                                    val isSelected = selectedIcon == icon
                                    IconBox(
                                        size = 46.dp,
                                        color = if (isSelected) animatedThemeColor else Color.Gray.copy(0.4f),
                                        icon = icon,
                                        isFilled = isSelected,
                                        onClick = {
                                            selectedIcon = icon
                                            iconsExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = SoftGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle(resources.getString(R.string.add_category_category_type))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TypeSelectorButton(
                            title = resources.getString(R.string.income),
                            icon = Icons.Rounded.ArrowDownward,
                            isSelected = categoryType == CategoryType.INCOME,
                            activeColor = IncomeGreen,
                            modifier = Modifier.weight(1f)
                        ) {
                            categoryType = CategoryType.INCOME
                            currentParent = null
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        TypeSelectorButton(
                            title = resources.getString(R.string.expense),
                            icon = Icons.Rounded.ArrowUpward,
                            isSelected = categoryType == CategoryType.EXPENSE,
                            activeColor = ExpenseRed,
                            modifier = Modifier.weight(1f)
                        ) {
                            categoryType = CategoryType.EXPENSE
                            currentParent = null
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = SoftGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))


                    SectionTitle(resources.getString(R.string.add_category_parent))
                    ExposedDropdownMenuBox(
                        expanded = parentCategoriesExpanded,
                        onExpandedChange = { parentCategoriesExpanded = !parentCategoriesExpanded },
                    ) {
                        OutlinedTextField(
                            value = currentParent?.title?.asString() ?: resources.getString(R.string.none),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentCategoriesExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SoftGray,
                                focusedBorderColor = animatedThemeColor,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = animatedThemeColor.copy(alpha = 0.02f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = parentCategoriesExpanded,
                            onDismissRequest = { parentCategoriesExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = resources.getString(R.string.none)) },
                                onClick = {
                                    currentParent = null
                                    parentCategoriesExpanded = false
                                },
                            )
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(text = cat.title.asString()) },
                                    onClick = {
                                        currentParent = cat
                                        parentCategoriesExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle(resources.getString(R.string.add_category_color))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CategoryColors.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = color },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))


            PrimaryButton(
                title = resources.getString(R.string.add_category_save_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                if (categoryTitle.isBlank()) {
                    titleIsError = true
                } else {
                    val newCategory = category.copy(
                        parentId = currentParent?.id,
                        type = categoryType,
                        title = UiText.Dynamic(text = categoryTitle),
                        icon = selectedIcon,
                        color = selectedColor
                    )
                    onSave(newCategory)
                    onDismissRequest()
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun IconBox(
    size: Dp,
    color: Color,
    icon: ImageVector,
    isFilled: Boolean = true,
    onClick: () -> Unit
) {
    val bgColor = if (isFilled) color.copy(alpha = 0.15f) else Color.Transparent
    val tintColor = if (isFilled) color else color.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(if (!isFilled) 1.dp else 0.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
private fun TypeSelectorButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val borderColor = if (isSelected) activeColor else SoftGray

    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
