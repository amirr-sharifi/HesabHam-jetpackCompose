package af.amir.hesabham.presentation.core.components.dialog.datepickerdialog

import af.amir.hesabham.R
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.dialog.BottomAttachDialog
import af.amir.hesabham.presentation.core.components.dialog.wheel_picker.SelectorProperties
import af.amir.hesabham.presentation.core.components.dialog.wheel_picker.WheelPickerDefaults
import af.amir.hesabham.presentation.core.components.dialog.wheel_picker.WheelTextPicker
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.threeten.bp.Instant


@Composable
fun WheelDatePickerDialog(
    modifier: Modifier = Modifier,
    initialDate: AppDateTime,
    showTimePicker: Boolean = true,
    rowSize: DpSize = DpSize(256.dp, 128.dp),
    rowCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onDismissDialog: () -> Unit,
    onDoneClick: (snappedDate: AppDateTime) -> Unit,
) {

    val viewModel: WheelDatePickerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(initialDate) {
        viewModel.setInitialDate(initialDate)
    }

    LaunchedEffect(true) {
        viewModel.uiEffect.collect {
            onDoneClick(it)
            onDismissDialog()
        }
    }

    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSecondary = MaterialTheme.colorScheme.secondary.copy(0.5f)
    val colorOnSecondary = MaterialTheme.colorScheme.onSecondary
    val colorOnPrimary = MaterialTheme.colorScheme.onPrimary

    val currentLocale = LocalAppLocale.current
    val resource = rememberLocalizeResources(currentLocale)
    val doneButtonLabel = resource.getString(R.string.Done)
    val cancelButtonLabel = resource.getString(R.string.cancel)
    val titleHeader = resource.getString(R.string.date_picker_dialog_title)

    BottomAttachDialog(onDismissDialog) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                titleHeader,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else
                DatePickerContent(
                    modifier,
                    state,
                    showTimePicker,
                    rowSize,
                    rowCount,
                    textStyle,
                    textColor,
                    selectorProperties,
                    viewModel::onAction
                )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        viewModel.onAction(WheelDatePickerAction.DoneClick)
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorOnPrimary,
                        containerColor = colorPrimary
                    )
                ) {
                    Text(doneButtonLabel)
                }
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = onDismissDialog,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colorOnSecondary,
                        containerColor = colorSecondary
                    )
                ) {
                    Text(cancelButtonLabel)
                }
            }
        }
    }

}

@Composable
private fun DatePickerContent(
    modifier: Modifier,
    state: WheelDatePickerState,
    showTimePicker: Boolean,
    size: DpSize,
    rowCount: Int,
    textStyle: TextStyle,
    textColor: Color,
    selectorProperties: SelectorProperties,
    onAction: (action: WheelDatePickerAction) -> Unit,
) {


    val spacing = remember { 8.dp }
    val rowWidth =
        remember(showTimePicker) { if (showTimePicker) size.width - 16.dp else size.width }

    val columnNum = remember(showTimePicker) { if (showTimePicker) 5 else 3 }
    val availableWidth = remember(rowWidth, columnNum) {
        rowWidth - spacing * (columnNum - 1)
    }
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Day of Month
                WheelTextPicker(
                    size = DpSize(
                        width = (availableWidth / columnNum) - 20.dp,
                        height = size.height
                    ),
                    texts = state.days.map { it.label },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = state.selectedDayIndex,
                    onScrollFinished = { snappedIndex ->
                        if (snappedIndex != state.selectedDayIndex)
                            onAction(
                                WheelDatePickerAction.ScrollFinished(
                                    snappedIndex,
                                    WheelDatePickerColumn.DAY
                                )
                            )
                        return@WheelTextPicker snappedIndex
                    }
                )
                //Month
                WheelTextPicker(
                    size = DpSize(
                        width = (availableWidth / columnNum) + 40.dp,
                        height = size.height
                    ),
                    texts = state.monthNames.map { it.label },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = state.selectedMonthIndex,
                    onScrollFinished = { snappedIndex ->
                        if (snappedIndex != state.selectedMonthIndex)
                            onAction(
                                WheelDatePickerAction.ScrollFinished(
                                    snappedIndex,
                                    WheelDatePickerColumn.MONTH
                                )
                            )
                        return@WheelTextPicker snappedIndex
                    }
                )
                //Year
                WheelTextPicker(
                    size = DpSize(
                        width = (availableWidth / columnNum),
                        height = size.height
                    ),
                    texts = state.years.map { it.label },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = state.selectedYearIndex,
                    onScrollFinished = { snappedIndex ->
                        if (snappedIndex != state.selectedYearIndex)
                            onAction(
                                WheelDatePickerAction.ScrollFinished(
                                    snappedIndex,
                                    WheelDatePickerColumn.YEAR
                                )
                            )
                        return@WheelTextPicker snappedIndex
                    }
                )


                if (showTimePicker) {

                    Text("-", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

                    //Hour
                    WheelTextPicker(
                        size = DpSize(
                            width = (availableWidth / columnNum) - 10.dp,
                            height = size.height
                        ),
                        texts = state.hours.map { it.label },
                        rowCount = rowCount,
                        style = textStyle,
                        color = textColor,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            enabled = false
                        ),
                        startIndex = state.selectedHourIndex,
                        onScrollFinished = { snappedIndex ->
                            if (snappedIndex != state.selectedHourIndex)
                                onAction(
                                    WheelDatePickerAction.ScrollFinished(
                                        snappedIndex,
                                        WheelDatePickerColumn.HOUR
                                    )
                                )
                            return@WheelTextPicker snappedIndex
                        }
                    )
                    Text(":", fontWeight = FontWeight.Bold)

                    //Minute
                    WheelTextPicker(
                        size = DpSize(
                            width = (availableWidth / columnNum) - 10.dp,
                            height = size.height
                        ),
                        texts = state.minutes.map { it.label },
                        rowCount = rowCount,
                        style = textStyle,
                        color = textColor,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            enabled = false
                        ),
                        startIndex = state.selectedMinuteIndex,
                        onScrollFinished = { snappedIndex ->
                            if (snappedIndex != state.selectedMinuteIndex)
                                onAction(
                                    WheelDatePickerAction.ScrollFinished(
                                        snappedIndex,
                                        WheelDatePickerColumn.MINUTE
                                    )
                                )
                            return@WheelTextPicker snappedIndex
                        }
                    )

                }
            }
        }
    }


}







