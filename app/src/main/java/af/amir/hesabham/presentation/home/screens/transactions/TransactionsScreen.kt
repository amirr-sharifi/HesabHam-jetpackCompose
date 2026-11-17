package af.amir.hesabham.presentation.home.screens.transactions

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.presentation.core.components.dialog.BottomAttachDialog
import af.amir.hesabham.presentation.core.components.dialog.datepickerdialog.WheelDatePickerDialog
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.TransactionWithDetailUiModel
import af.amir.hesabham.presentation.core.theme.ExpenseRed
import af.amir.hesabham.presentation.core.theme.IncomeGreen
import af.amir.hesabham.presentation.home.screens.transactions.components.EmptyTransactionsState
import af.amir.hesabham.presentation.core.components.TransactionDetailsBottomSheet
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.home.screens.transactions.components.filter_bottomsheet.TransactionFilterBottomSheet
import af.amir.hesabham.presentation.util.ObserveAsEffects
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal


@Composable
fun TransactionsScreen(
    onNavigateToAddTransaction: (transactionId: String?) -> Unit,
) {
    val viewModel = koinViewModel<TransactionsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TransactionsScreenStateless(
        state,
        viewModel.uiEffect,
        viewModel::onAction,
        onNavigateToAddTransaction
    )

}

@Composable
private fun TransactionsScreenStateless(
    state: TransactionsState,
    uiEffect: Flow<TransactionsEffect>,
    onAction: (TransactionsAction) -> Unit,
    onNavigateToAddTransaction: (transactionId: String?) -> Unit,
) {
    val context = LocalContext.current
    val resources = rememberLocalizeResources(LocalAppLocale.current)


    var showingBottomSheet by remember { mutableStateOf(false) }
    var showingDeleteDialog by remember { mutableStateOf(false) }
    var showingDatePicker by remember { mutableStateOf(false) }
    var isDatePickerIsForStartTime by remember { mutableStateOf(false) }

    var transactionDetailDialogData by remember {
        mutableStateOf<TransactionWithDetailUiModel?>(
            null
        )
    }

    ObserveAsEffects(uiEffect) {
        when (it) {
            is TransactionsEffect.NavigateToAddTransaction -> onNavigateToAddTransaction(it.transactionId)
            is TransactionsEffect.OnTransactionClick -> transactionDetailDialogData = it.transaction
            TransactionsEffect.ShowDateRangeError -> Toast.makeText(
                context,
                resources.getString(R.string.invalid_date_range), Toast.LENGTH_SHORT
            ).show()

            TransactionsEffect.ShowDeleteDialog -> showingDeleteDialog = true
            TransactionsEffect.CloseDetailBottomSheet -> transactionDetailDialogData = null
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderSection(
            onSettingsClick = {
                onAction(TransactionsAction.OnSettingsClick)
                showingBottomSheet = true
            }
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (state.transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTransactionsState()
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.transactions.forEach { transactionList ->
                        item(key = transactionList.date.hashCode()) {
                            DailyGroupHeader(transactionList.date, transactionList.totalBalance)
                        }

                        itemsIndexed(
                            items = transactionList.transactions,
                            key = { _, item -> item.id }
                        ) { index, transaction ->
                            TransactionItemCard(
                                transaction = transaction,
                                time = transactionList.times[index],
                                onClick = {
                                    onAction(
                                        TransactionsAction.OnTransactionClick(transaction,)
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showingBottomSheet) {
        TransactionFilterBottomSheet(
            selectedFilter = state.selectedFilterType,
            startDate = state.tempFormattedStartDate,
            endDate = state.tempFormattedEndDate,
            onShowTimePickerDialog = {
                showingDatePicker = true
                isDatePickerIsForStartTime = it
            },
            onDismissRequest = { showingBottomSheet = false },
            onSaveFilter = {
                onAction(TransactionsAction.SaveFilterState(it))
                showingBottomSheet = false
            }
        )
    }

    if (showingDatePicker) {
        WheelDatePickerDialog(
            initialDate = if (isDatePickerIsForStartTime) state.tempStartDate else state.tempEndDate,
            showTimePicker = false,
            onDismissDialog = { showingDatePicker = false },
            onDoneClick = {
                if (isDatePickerIsForStartTime) {
                    onAction(TransactionsAction.StartTimePicked(it))
                } else {
                    onAction(TransactionsAction.EndTimePicked(it))
                }
                showingDatePicker = false
            }
        )
    }
    transactionDetailDialogData?.let { transaction ->
        TransactionDetailsBottomSheet(
            transaction = transaction,
            onEditTransactionClick = onNavigateToAddTransaction,
            onDeleteTransactionClick = {onAction(TransactionsAction.OnShowDeleteDialog(it))}
        ) { transactionDetailDialogData = null }
    }


    if (showingDeleteDialog) {
        BottomAttachDialog(onDismiss = { showingDeleteDialog = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    resources.getString(R.string.delete_transaction),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                
                Spacer(Modifier.height(22.dp))
                Text(resources.getString(R.string.delete_transaction_message), textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))

                Row (Modifier.fillMaxWidth()) {

                    TextButton(
                        onClick = {
                            showingDeleteDialog = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = resources.getString(R.string.cancel),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = {
                            onAction(TransactionsAction.OnDeleteTransaction)
                            showingDeleteDialog = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = resources.getString(R.string.delete),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }


            }
        }
    }

}


@Composable
private fun HeaderSection(
    onSettingsClick: () -> Unit,
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(contentAlignment = Alignment.TopStart) {
            Surface(
                onClick = onSettingsClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = "filter settings",
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(ExpenseRed, CircleShape)
                    .padding(2.dp)
            )

        }

        Spacer(Modifier.weight(1f))
        Text(
            resources.getString(R.string.transactions),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DailyGroupHeader(formattedDate: String, totalBalance: BigDecimal) {

    val locale = LocalAppLocale.current


    val totalColor =
        if (totalBalance < BigDecimal.ZERO) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else IncomeGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rememberFormattedAmount(totalBalance.toPlainString(), locale),
            style = MaterialTheme.typography.labelMedium,
            color = totalColor,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TransactionItemCard(
    transaction: TransactionWithDetailUiModel,
    time: String,
    onClick: () -> Unit,
) {

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(transaction.categoryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = transaction.categoryIcon?.asImageVector()
                            ?: transaction.transferIcon.asImageVector(),
                        contentDescription = null,
                        tint = transaction.categoryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = transaction.categoryTitle.asString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = transaction.accountName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val amountColor =
                    when (transaction.type) {
                        TransactionType.TRANSFER_IN,
                        TransactionType.INCOME,
                        -> IncomeGreen

                        TransactionType.TRANSFER_OUT,
                        TransactionType.INITIAL_BALANCE,
                        TransactionType.EXPENSE,
                        -> ExpenseRed
                    }
                Text(
                    text = rememberFormattedAmount(
                        transaction.amount.toPlainString(),
                        LocalAppLocale.current
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    maxLines = 1
                )
                Text(
                    text = LocalAppCurrency.current.symbol,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}