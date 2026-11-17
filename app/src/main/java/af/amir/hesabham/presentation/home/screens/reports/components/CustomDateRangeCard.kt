package af.amir.hesabham.presentation.home.screens.reports.components


import af.amir.hesabham.R
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.dialog.datepickerdialog.WheelDatePickerDialog
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomDateRangeCard(
    formattedStartDate: String,
    formattedEndDate: String,
    startDate: AppDateTime,
    endDate: AppDateTime,
    modifier: Modifier = Modifier,
    onStartDatePicked: (AppDateTime) -> Unit,
    onEndDatePicked: (AppDateTime) -> Unit,
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    var isShowingDateDialog by remember { mutableStateOf(false) }
    var isPickingStartDate by remember { mutableStateOf(true) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateField(
            label = resources.getString(R.string.from_date),
            date = formattedStartDate,
            modifier = Modifier.weight(1f)
        ) {
            isShowingDateDialog = true; isPickingStartDate = true
        }
        DateField(
            label = resources.getString(R.string.to_date),
            date = formattedEndDate,
            modifier = Modifier.weight(1f)
        ) {
            isShowingDateDialog = true; isPickingStartDate = false
        }
    }

    if (isShowingDateDialog)
        WheelDatePickerDialog(
            initialDate = if (isPickingStartDate) startDate else endDate,
            showTimePicker = false,
            onDismissDialog = { isShowingDateDialog = false }) {
            if (isPickingStartDate)
                onStartDatePicked(it)
            else
                onEndDatePicked(it)
        }
}

@Composable
private fun DateField(
    label: String,
    date: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp, onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
