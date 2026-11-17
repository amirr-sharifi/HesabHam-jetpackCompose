package af.amir.hesabham.presentation.add_transaction

import af.amir.hesabham.presentation.core.components.category_editor.CategoryEditorDialog
import af.amir.hesabham.R
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.presentation.add_transaction.components.fullscreendialog.AccountsFullScreenDialog
import af.amir.hesabham.presentation.add_transaction.components.AmountDisplay
import af.amir.hesabham.presentation.add_transaction.components.fullscreendialog.CategoryFullScreenDialog
import af.amir.hesabham.presentation.add_transaction.components.CommentDialog
import af.amir.hesabham.presentation.core.components.ArchiveConfirm
import af.amir.hesabham.presentation.core.components.CardWithWave
import af.amir.hesabham.presentation.core.components.ExpenseTopBar
import af.amir.hesabham.presentation.core.components.PrimaryButton
import af.amir.hesabham.presentation.core.components.amountinput.AmountInputBottomSheet
import af.amir.hesabham.presentation.core.components.bottomwavesscreen.BottomWavesScreen
import af.amir.hesabham.presentation.core.components.dialog.datepickerdialog.WheelDatePickerDialog
import af.amir.hesabham.presentation.core.components.tab_switch.TabSwitch
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorDialog
import af.amir.hesabham.presentation.core.components.dialog.BottomAttachDialog
import af.amir.hesabham.presentation.core.components.dialog.FullScreenDialogStackNavigation
import af.amir.hesabham.presentation.core.config.setting.*
import af.amir.hesabham.presentation.util.ObserveAsEffects
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AddTransactionScreen(onNavigateUp: () -> Unit) {

    val viewModel = koinViewModel<AddTransactionViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddTransactionScreenStateless(onNavigateUp, state, viewModel.uiEffect, viewModel::onAction)


}

@Composable
fun AddTransactionScreenStateless(
    onNavigateUp: () -> Unit,
    state: AddTransactionState,
    uiEffect: Flow<AddTransactionUiEffect>,
    onAction: (AddTransactionAction) -> Unit,
) {
    val currentLocale = LocalAppLocale.current
    val resources = rememberLocalizeResources(currentLocale)
    val context = LocalContext.current

    ObserveAsEffects(uiEffect) { effect ->
        when (effect) {
            AddTransactionUiEffect.NavigateUp -> onNavigateUp()
            is AddTransactionUiEffect.ShowToast -> {
                Toast.makeText(context, effect.message.asString(resources), Toast.LENGTH_SHORT).show()
            }
        }
    }

    BottomWavesScreen(
        topAppBar = {
            ExpenseTopBar(
                state.appBarState,
                onNavigateUp = { onAction(AddTransactionAction.NavigateIconClick) }
            )
        }
    ) {


        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabSwitch(
                tabs = state.transactionTabList,
                selectedTabIndex = state.transactionType.ordinal,
                isEnable = state.transactionTabEnabled,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onTabSelected = { onAction(AddTransactionAction.TransactionChanged(it)) }
            )

            Column {

                CardWithWave(Modifier.fillMaxWidth()) {
                    AmountDisplay(
                        amount = state.getAmountInString(),
                        onAmountClick = { onAction(AddTransactionAction.OpenAmountInput) }
                    )
                }

                AnimatedVisibility(visible = state.amountEmptyErrorMessage != null) {
                    state.amountEmptyErrorMessage?.let {
                        Spacer(Modifier.height(6.dp))
                        ErrorLabel(
                            it.asString(resources),
                            Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }


            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        12.dp,
                        RoundedCornerShape(32.dp),
                        spotColor = Color.Black.copy(alpha = 0.05f)
                    ),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {

                    val firstConfig = rememberDynamicFirstCardConfig(state, onAction)
                    val secondConfig = rememberDynamicSecondCardConfig(state, onAction)

                    /****** Section 1: Category/From Account *****/
                    ContentCard(
                        iconRes = firstConfig.iconRes,
                        title = resources.getString(firstConfig.titleRes),
                        subTitle = firstConfig.subTitle,
                        errorMessage = firstConfig.error,
                        isLoading = firstConfig.isLoading,
                        onClick = firstConfig.onClick
                    )

                    SoftDivider()

                    /****** Section 2: Account/To Account *****/
                    ContentCard(
                        iconRes = secondConfig.iconRes,
                        title = resources.getString(secondConfig.titleRes),
                        subTitle = secondConfig.subTitle,
                        isLoading = secondConfig.isLoading,
                        errorMessage = secondConfig.error,
                        onClick = secondConfig.onClick
                    )

                    SoftDivider()

                    /****** Section 3: Date *****/
                    ContentCard(
                        iconRes = R.drawable.ic_calendar,
                        title = resources.getString(R.string.date),
                        subTitle = state.formattedSelectedDate,
                        isLoading = false,
                        onClick = { onAction(AddTransactionAction.OpenDateDialog) }
                    )

                    SoftDivider()

                    /****** Section 4: Comment *****/
                    ContentCard(
                        iconRes = R.drawable.ic_comment,
                        title = resources.getString(R.string.comment),
                        subTitle = state.comment?.takeIf { it.isNotBlank() } ?: resources.getString(
                            R.string.no_comment
                        ),
                        isLoading = false,
                        onClick = { onAction(AddTransactionAction.OpenCommentDialog) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }



        Box(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            if (state.isSaving)
                CircularProgressIndicator()
            else
                PrimaryButton(
                    onClick = { onAction(AddTransactionAction.SaveTransaction) },
                    modifier = Modifier.fillMaxWidth(),
                    title = resources.getString(R.string.save),
                    shape = RoundedCornerShape(20.dp)
                )
        }


        FullScreenDialogStackNavigation(state.fullScreenDialogStack) { screenDialog ->
            when (screenDialog) {
                FullScreenDialog.Categories ->
                    CategoryFullScreenDialog(
                        categorySection = state.categories,
                        archivedCategories = state.archivedCategories,
                        onDismissRequest = { onAction(AddTransactionAction.CloseFullScreenDialog) },
                        onCategoryClick = {
                            onAction(AddTransactionAction.CategorySelected(it))
                        },
                        onEditCategory = { category, parent ->
                            onAction(
                                AddTransactionAction.EditCategoryClick(
                                    parent = parent,
                                    category = category
                                )
                            )
                        },
                        onAddClick = {
                            onAction(AddTransactionAction.AddCategoryClick(parent = it))
                        },
                        onArchiveCategory = {
                            onAction(AddTransactionAction.ShowCategoryArchiveConfirmDialog(it))
                        },
                        onRestoreCategory = {
                            onAction(AddTransactionAction.RestoreCategory(it))
                        }
                    )

                FullScreenDialog.Accounts ->
                    AccountsFullScreenDialog(
                        accounts = state.accountList.filterNot { it.isArchive },
                        onDismissRequest = { onAction(AddTransactionAction.CloseFullScreenDialog) },
                        onAccountSelected = { onAction(AddTransactionAction.AccountChanged(it)) },
                        onEditAccount = { onAction(AddTransactionAction.EditAccountClick(it)) },
                        onAddAccount = { onAction(AddTransactionAction.AddAccountClick(it)) },
                        onArchiveAccount = {
                            onAction(
                                AddTransactionAction.ShowAccountArchiveConfirmDialog(it)
                            )
                        })

                FullScreenDialog.AddCategory -> {
                    val dialogState = state.categoryEditorState
                    CategoryEditorDialog(
                        categoryEditorState = dialogState,
                        onDismissRequest = { onAction(AddTransactionAction.CloseAddCategoryDialog) },
                        onSave = { onAction(AddTransactionAction.SaveCategory(it)) }
                    )
                }

                FullScreenDialog.AccountEditor -> AccountEditorDialog(
                    state.accountEditorState,
                    onDismissRequest = { onAction(AddTransactionAction.CloseFullScreenDialog) },
                    onSaveClick = { onAction(AddTransactionAction.SaveAccount(it)) })
            }
        }

        //overlay screen dialog Host
        when (state.overlayDialog) {
            OverlayDialog.None -> Unit

            OverlayDialog.Date ->
                WheelDatePickerDialog(
                    initialDate = state.selectedDate,
                    onDismissDialog = { onAction(AddTransactionAction.CloseOverlayDialog) },
                    onDoneClick = {
                        onAction(AddTransactionAction.DatePicked(it))
                    }
                )

            OverlayDialog.Comment ->
                CommentDialog(
                    comment = state.comment ?: "",
                    onDismissDialog = { onAction(AddTransactionAction.CloseOverlayDialog) }) {
                    onAction(AddTransactionAction.CommentChanged(it))
                }

            OverlayDialog.AmountInput ->
                AmountInputBottomSheet(
                    defaultValue = state.amount,
                    onDismissRequest = { onAction(AddTransactionAction.CloseOverlayDialog) }) {
                    onAction(AddTransactionAction.AmountChanged(it))
                }

            OverlayDialog.AccountArchiveConfirm ->
                BottomAttachDialog(onDismiss = {
                    onAction(AddTransactionAction.CloseOverlayDialog)
                }) {
                    ArchiveConfirm(
                        isForAccount = true,
                        onCancel = { onAction(AddTransactionAction.CloseOverlayDialog) },
                        onConfirm = { onAction(AddTransactionAction.ArchiveAccount) })
                }

            OverlayDialog.CategoryArchiveConfirm ->
                BottomAttachDialog(onDismiss = {
                    onAction(AddTransactionAction.CloseOverlayDialog)
                }) {
                    ArchiveConfirm(
                        isForAccount = false,
                        onCancel = { onAction(AddTransactionAction.CloseOverlayDialog) },
                        onConfirm = { onAction(AddTransactionAction.ArchiveCategory) })
                }
        }

    }
}

@Composable
private fun ContentCard(
    @DrawableRes iconRes: Int,
    title: String,
    subTitle: String,
    isLoading: Boolean,
    errorMessage: String? = null,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        AnimatedContent(
            targetState = isLoading,
            label = "ContentCardLoading",
            modifier = Modifier.weight(1f)
        ) { loading ->

            val resources = rememberLocalizeResources(LocalAppLocale.current)
            if (loading) {
                CircularProgressIndicator(Modifier.requiredSize(24.dp), strokeWidth = 2.dp)
            } else {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(2.dp))
                    if (errorMessage == null) {
                        Text(
                            text = subTitle.ifBlank { resources.getString(R.string.select) },
                            color = if (subTitle.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.4f
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        ErrorLabel(errorMessage)
                    }
                }
            }
        }


        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SoftDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 76.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    )
}


@Composable
private fun ErrorLabel(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        "* $message",
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}


@Composable
private fun rememberDynamicFirstCardConfig(
    state: AddTransactionState,
    onAction: (AddTransactionAction) -> Unit,
): TransactionFieldConfig {
    val resources = rememberLocalizeResources(LocalAppLocale.current)

    return when (state.transactionType) {
        TransactionType.EXPENSE -> TransactionFieldConfig(
            titleRes = R.string.category,
            iconRes = R.drawable.ic_shopping,
            subTitle = state.selectedCategory?.title?.asString(resources) ?: "",
            isLoading = state.isCategoriesLoading,
            onClick = { onAction(AddTransactionAction.OpenCategoryDialog) },
            error = state.categoryEmptyErrorMessage?.asString(resources)
        )

        TransactionType.INCOME -> TransactionFieldConfig(
            titleRes = R.string.category,
            iconRes = R.drawable.ic_income,
            subTitle = state.selectedCategory?.title?.asString(resources) ?: "",
            isLoading = state.isCategoriesLoading,
            onClick = { onAction(AddTransactionAction.OpenCategoryDialog) },
            error = state.categoryEmptyErrorMessage?.asString(resources)
        )

        TransactionType.INITIAL_BALANCE,
        TransactionType.TRANSFER_IN,
        TransactionType.TRANSFER_OUT,
        -> TransactionFieldConfig(
            titleRes = R.string.from_account,
            iconRes = R.drawable.ic_wallet,
            subTitle = state.transferFromAccount?.name ?: "",
            isLoading = state.isAccountsLoading,
            onClick = { onAction(AddTransactionAction.OpenAccountsDialog(TransactionType.TRANSFER_OUT)) },
            error = state.fromAccountEmptyErrorMessage?.asString(resources)
        )
    }
}


@Composable
private fun rememberDynamicSecondCardConfig(
    state: AddTransactionState,
    onAction: (AddTransactionAction) -> Unit,
): TransactionFieldConfig {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    val selectedAccount =
        (state.selectedAccount?.name
            ?: "") + if (state.selectedAccount?.isArchive == true) " (" + resources.getString(R.string.archived) + ")" else ""

    return when (state.transactionType) {
        TransactionType.INITIAL_BALANCE,
        TransactionType.EXPENSE,
        TransactionType.INCOME,
        -> TransactionFieldConfig(
            titleRes = R.string.accounts,
            iconRes = R.drawable.ic_wallet,
            subTitle = selectedAccount,
            isLoading = state.isAccountsLoading,
            onClick = { onAction(AddTransactionAction.OpenAccountsDialog(TransactionType.INCOME)) },
            error = state.accountEmptyErrorMessage?.asString(resources)
        )

        else -> TransactionFieldConfig(
            titleRes = R.string.to_account,
            iconRes = R.drawable.ic_wallet,
            subTitle = state.transferToAccount?.name ?: "",
            isLoading = state.isAccountsLoading,
            onClick = { onAction(AddTransactionAction.OpenAccountsDialog(TransactionType.TRANSFER_IN)) },
            error = state.toAccountEmptyErrorMessage?.asString(resources)
        )
    }
}