package af.amir.hesabham.presentation.home.screens.settings

import af.amir.hesabham.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import af.amir.hesabham.presentation.core.components.AppCard
import af.amir.hesabham.presentation.core.config.setting.LocalAppCalendar
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.home.screens.settings.components.AnimatedThemeSwitch
import af.amir.hesabham.presentation.home.screens.settings.components.OptionsBottomSheet
import af.amir.hesabham.presentation.util.ObserveAsEffects
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsViewModel>()

    SettingsStatelessScreen(viewModel) {}
}


@Composable
fun SettingsStatelessScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEffects(viewModel.uiEffect) { effect ->
        when (effect) {
            SettingsUiEffect.NavigateBack -> onNavigateBack()
        }

    }

    val iconBgColor = MaterialTheme.colorScheme.primary.copy(0.15f)
    val iconTintColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMutedColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)
    val locale = LocalAppLocale.current
    val calendar = LocalAppCalendar.current
    val currency = LocalAppCurrency.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = resources.getString(R.string.setting),
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = resources.getString(R.string.general),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        AppCard {
            SettingsRowItem(
                icon = Icons.Rounded.MonetizationOn,
                label = resources.getString(R.string.currency),
                value = uiState.currencies.firstOrNull { it.data == currency }?.title?.asString()
                    ?: "",
                iconBg = iconBgColor,
                iconTint = iconTintColor,
                textColor = textColor,
                textMutedColor = textMutedColor,
                onClick = {
                    viewModel.onAction(
                        SettingsAction.OpenBottomSheet(
                            BottomSheetType.Currency
                        )
                    )
                }
            )

            val dividerColor = Color.Gray.copy(0.3f)
            HorizontalDivider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            SettingsRowItem(
                icon = Icons.Rounded.Language,
                label = resources.getString(R.string.language),
                value = uiState.languages.firstOrNull { it.data == locale }?.title?.asString()
                    ?: "",
                iconBg = iconBgColor,
                iconTint = iconTintColor,
                textColor = textColor,
                textMutedColor = textMutedColor,
                onClick = {
                    viewModel.onAction(
                        SettingsAction.OpenBottomSheet(
                            BottomSheetType.Language
                        )
                    )
                }
            )

            HorizontalDivider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            SettingsRowItem(
                icon = Icons.Rounded.CalendarToday,
                label = resources.getString(R.string.calendar),
                value = uiState.calendars.firstOrNull { it.data == calendar }?.title?.asString()
                    ?: "",
                iconBg = iconBgColor,
                iconTint = iconTintColor,
                textColor = textColor,
                textMutedColor = textMutedColor,
                onClick = {
                    viewModel.onAction(
                        SettingsAction.OpenBottomSheet(
                            BottomSheetType.Calendar
                        )
                    )
                }
            )

        }

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = resources.getString(R.string.appearance),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        AppCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Nightlight,
                            contentDescription = "حالت شب",
                            tint = iconTintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = resources.getString(R.string.dark_mode),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                        Text(
                            text = if (uiState.isDarkMode) resources.getString(R.string.active) else resources.getString(
                                R.string.inactive
                            ),
                            fontSize = 12.5.sp,
                            color = textMutedColor
                        )
                    }
                }

                AnimatedThemeSwitch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { isChecked ->
                        viewModel.onAction(SettingsAction.ToggleDarkMode(isChecked))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // فاصله جهت قرارگیری BottomNav
    }


    uiState.activeBottomSheet?.let { sheetType ->
        when (sheetType) {
            BottomSheetType.Currency -> {
                OptionsBottomSheet(
                    title = resources.getString(R.string.currency),
                    options = uiState.currencies,
                    selectedOption = currency,
                    onOptionSelected = {
                        viewModel.onAction(SettingsAction.SelectCurrency(it))

                    },
                    onDismissRequest = { viewModel.onAction(SettingsAction.DismissBottomSheet) },
                )
            }

            BottomSheetType.Language -> {
                OptionsBottomSheet(
                    title = resources.getString(R.string.language),
                    options = uiState.languages,
                    selectedOption = locale,
                    onOptionSelected = {
                        viewModel.onAction(SettingsAction.SelectLanguage(it))

                    },
                    onDismissRequest = { viewModel.onAction(SettingsAction.DismissBottomSheet) },
                )
            }

            BottomSheetType.Calendar -> {
                OptionsBottomSheet(
                    title = resources.getString(R.string.calendar),
                    options = uiState.calendars,
                    selectedOption = calendar,
                    onOptionSelected = {
                        viewModel.onAction(SettingsAction.SelectCalendar(it))

                    },
                    onDismissRequest = { viewModel.onAction(SettingsAction.DismissBottomSheet) },
                )
            }
        }
    }
}


@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconBg: Color,
    iconTint: Color,
    textColor: Color,
    textMutedColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textMutedColor
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = textMutedColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
