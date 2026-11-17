package af.amir.hesabham.presentation.add_transaction

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.category.CategoryType
import af.amir.hesabham.domain.model.settings.AppCalendar
import af.amir.hesabham.domain.model.settings.AppLocale
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.usecase.account.AccountUseCase
import af.amir.hesabham.domain.usecase.category.CategoryUseCase
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.domain.util.SystemCategoryIds
import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.model.AppDateTime
import af.amir.hesabham.presentation.core.components.account_editor.AccountEditorState
import af.amir.hesabham.presentation.core.components.category_editor.CategoryEditorState
import af.amir.hesabham.presentation.core.config.AppBarConfig
import af.amir.hesabham.presentation.core.mapper.UiCategoryMapper
import af.amir.hesabham.presentation.core.model.CategorySection
import af.amir.hesabham.presentation.core.model.CategoryUiModel
import af.amir.hesabham.presentation.core.model.UiText
import af.amir.hesabham.presentation.navigation.TRANSACTION_ID
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import java.math.BigDecimal

class AddTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val calendarEngineProvider: CalendarEngineProvider,
    private val uiCategoryMapper: UiCategoryMapper,
    private val settingsUseCase: SettingsUseCase,
) : ViewModel() {


    private val transactionId: String? =
        savedStateHandle[TRANSACTION_ID]

    private val isEditMode: Boolean =
        transactionId != null

    private var tempAccountForArchive: Account? = null
    private var tempCategoryForArchive: CategoryUiModel? = null

    private val _state = MutableStateFlow(AddTransactionState())
    val state = _state.asStateFlow()

    private val _uiEffect =
        Channel<AddTransactionUiEffect>(Channel.BUFFERED)

    val uiEffect =
        _uiEffect.receiveAsFlow()


    private val dialogStack =
        MutableStateFlow<List<FullScreenDialog>>(emptyList())

    private var transactionTypeInAccountsDialog: TransactionType? = null

    private var incomeCategory =
        emptyList<CategoryUiModel>()

    private var expenseCategory =
        emptyList<CategoryUiModel>()

    private val settings =
        settingsUseCase.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private val calendar =
        settings
            .map { it?.calendar }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private val locale =
        settings
            .map { it?.locale }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    init {
        observeCategories()
        observeAccounts()
        observeDialogStack()

        if (isEditMode) {
            initializeEditMode(transactionId!!)
        } else {
            initializeAddMode()
        }

        observeSettings()
    }


    private fun initializeAddMode() {
        _state.update {
            it.copy(
                isLoading = false,
                appBarState = AppBarConfig.AddTransaction,
                transactionTabEnabled = true,
                transactionType = TransactionType.EXPENSE
            )
        }
    }


    private fun initializeEditMode(id: String) {
        viewModelScope.launch {
            settings
                .filterNotNull()
                .collectLatest {

                    val currentLocale = it.locale
                    val currentCalendar = it.calendar
                    _state.update {
                        it.copy(
                            isLoading = true,
                            appBarState = AppBarConfig.EditTransaction,
                            transactionTabEnabled = false
                        )
                    }

                    val transactionDeferred = async {
                        transactionUseCase.getTransaction(
                            TransactionId(id)
                        )
                    }

                    val accountsDeferred = async {
                        accountUseCase.get.observeAll().first()
                    }

                    val categoriesDeferred = async {
                        categoryUseCase.observeAll()
                            .first()
                            .filterNot {
                                it.isArchived ||
                                        it.categoryId.id ==
                                        SystemCategoryIds.INITIAL_CATEGORY_ID
                            }
                            .map(uiCategoryMapper::mapToUiModel)
                    }


                    val transaction =
                        transactionDeferred.await()

                    val accounts =
                        accountsDeferred.await()

                    val categories =
                        categoriesDeferred.await()


                    val incomeCategories =
                        categories.filter {
                            it.type == CategoryType.INCOME
                        }

                    val expenseCategories =
                        categories.filter {
                            it.type == CategoryType.EXPENSE
                        }


                    incomeCategory = incomeCategories
                    expenseCategory = expenseCategories


                    val transfer =
                        transaction.transferId?.let { transferId ->
                            transactionUseCase.getTransfer(transferId)
                        }

                    val selectedCategory =
                        when (transaction.type) {

                            TransactionType.INCOME ->
                                incomeCategories.firstOrNull {
                                    it.id == transaction.categoryId?.id
                                }

                            TransactionType.EXPENSE ->
                                expenseCategories.firstOrNull {
                                    it.id == transaction.categoryId?.id
                                }

                            TransactionType.TRANSFER_IN,
                            TransactionType.TRANSFER_OUT,
                            TransactionType.INITIAL_BALANCE,
                            ->
                                null
                        }


                    val selectedAccount =
                        when (transaction.type) {

                            TransactionType.INCOME,
                            TransactionType.EXPENSE,
                            ->
                                accounts.firstOrNull {
                                    it.id == transaction.accountId
                                }

                            TransactionType.TRANSFER_IN,
                            TransactionType.TRANSFER_OUT,
                            TransactionType.INITIAL_BALANCE,
                            ->
                                null
                        }


                    val transferFromAccount =
                        transfer?.let { loadedTransfer ->
                            accounts.firstOrNull {
                                it.id == loadedTransfer.fromAccount
                            }
                        }

                    val transferToAccount =
                        transfer?.let { loadedTransfer ->
                            accounts.firstOrNull {
                                it.id == loadedTransfer.toAccount
                            }
                        }


                    val calendarEngine =
                        calendarEngineProvider.get(currentCalendar)

                    val appDateTime =
                        calendarEngine.fromInstant(transaction.date)

                    val formattedDate =
                        calendarEngine.formatDateTime(
                            appDateTime,
                            currentLocale.locale
                        )




                    _state.update {
                        it.copy(
                            isLoading = false,

                            appBarState =
                            AppBarConfig.EditTransaction,

                            transactionTabEnabled = false,

                            transactionType =
                            transaction.type.toEditorTransactionType(),

                            amount =
                            transaction.amount.amount,

                            selectedCategory =
                            selectedCategory,

                            formattedSelectedDate =
                            formattedDate,

                            selectedDate =
                            appDateTime,

                            comment =
                            transaction.note,

                            selectedAccount =
                            selectedAccount,

                            transferFromAccount =
                            transferFromAccount,

                            transferToAccount =
                            transferToAccount,

                            accountList =
                            accounts,

                            isAccountsLoading =
                            false,

                            isCategoriesLoading =
                            false,

                            categories =
                            when (transaction.type) {
                                TransactionType.EXPENSE ->
                                    expenseCategories.toSections()

                                TransactionType.INCOME ->
                                    incomeCategories.toSections()

                                else ->
                                    emptyList()
                            }
                        )
                    }

                }


        }
    }


    /**
     * Converts domain transaction type to the type used by the editor.
     */
    private fun TransactionType.toEditorTransactionType(): TransactionType {
        return when (this) {

            TransactionType.INCOME ->
                TransactionType.INCOME

            TransactionType.EXPENSE ->
                TransactionType.EXPENSE

            TransactionType.TRANSFER_OUT,
            TransactionType.INITIAL_BALANCE,
            TransactionType.TRANSFER_IN,
            ->
                TransactionType.TRANSFER_IN

        }
    }


    // =========================================================================
    // Permanent observers
    // =========================================================================

    /**
     * Categories are continuously observed because they can be added,
     * edited or archived while this screen is open.
     */
    private fun observeCategories() {

        viewModelScope.launch {

            categoryUseCase.observeAll()
                .map { list ->
                    list.filterNot {
                        it.categoryId.id ==
                                SystemCategoryIds.INITIAL_CATEGORY_ID
                    }
                        .map(uiCategoryMapper::mapToUiModel)
                }
                .collectLatest { pureCategories ->

                    val categories = pureCategories.filterNot { it.isArchived }

                    incomeCategory =
                        categories.filter {
                            it.type == CategoryType.INCOME
                        }

                    expenseCategory =
                        categories.filter {
                            it.type == CategoryType.EXPENSE
                        }

                    _state.update {
                        it.copy(
                            archivedCategories = pureCategories.filter { cate -> cate.isArchived },
                            isCategoriesLoading = false
                        )
                    }

                    refreshCategorySections()
                }
        }
    }


    /**
     * Accounts are continuously observed.
     */
    private fun observeAccounts() {

        viewModelScope.launch {

            accountUseCase.get
                .observeAll()
                .collectLatest { accounts ->

                    _state.update {
                        it.copy(
                            accountList = accounts,
                            isAccountsLoading = false
                        )
                    }
                }
        }
    }


    /**
     * Full screen dialog stack observer.
     */
    private fun observeDialogStack() {

        viewModelScope.launch {

            dialogStack.collectLatest { stack ->

                val appBarState =
                    if (stack.isEmpty()) {

                        if (isEditMode) {
                            AppBarConfig.EditTransaction
                        } else {
                            AppBarConfig.AddTransaction
                        }

                    } else {

                        when (stack.last()) {

                            FullScreenDialog.Categories ->
                                AppBarConfig.Categories

                            FullScreenDialog.Accounts ->
                                AppBarConfig.AccountsDialog

                            FullScreenDialog.AddCategory -> {

                                val isUpdating =
                                    _state.value
                                        .categoryEditorState
                                        .isUpdating

                                if (isUpdating) {
                                    AppBarConfig.EditCategory
                                } else {
                                    AppBarConfig.AddCategory
                                }
                            }

                            FullScreenDialog.AccountEditor -> {

                                val isUpdating =
                                    _state.value
                                        .accountEditorState
                                        .isUpdating

                                if (isUpdating) {
                                    AppBarConfig.EditAccount
                                } else {
                                    AppBarConfig.AddAccount
                                }
                            }
                        }
                    }

                _state.update {
                    it.copy(
                        fullScreenDialogStack = stack,
                        appBarState = appBarState
                    )
                }
            }
        }
    }


    /**
     * Settings are continuously observed.
     *
     * IMPORTANT:
     *
     * We only update "today" for Add mode.
     *
     * In Edit mode the date belongs to the transaction
     * and must not be overwritten.
     */
    private fun observeSettings() {

        viewModelScope.launch {

            settingsUseCase.observe()
                .collectLatest { settings ->

                    val now =
                        Instant.now()

                    val calendarEngine =
                        calendarEngineProvider.get(
                            settings.calendar
                        )

                    val nowInAppDateTime =
                        calendarEngine.fromInstant(now)

                    val formattedDate =
                        calendarEngine.formatDateTime(
                            nowInAppDateTime,
                            settings.locale.locale
                        )


                    if (!isEditMode) {

                        _state.update {
                            it.copy(
                                selectedDate =
                                nowInAppDateTime,

                                formattedSelectedDate =
                                formattedDate
                            )
                        }
                    }
                }
        }
    }


    // =========================================================================
    // Events
    // =========================================================================

    fun onAction(action: AddTransactionAction) {

        when (action) {

            AddTransactionAction.NavigateIconClick ->
                onNavigateIconClick()

            is AddTransactionAction.AmountChanged ->
                onAmountChanged(action.amount)

            is AddTransactionAction.TransactionChanged ->
                onTransactionTypeChanged(action.ordinal)


            // Full screen dialogs

            AddTransactionAction.CloseFullScreenDialog ->
                onPopFullScreenDialog()

            AddTransactionAction.OpenCategoryDialog ->
                onChangeFullScreenDialogState(
                    FullScreenDialog.Categories
                )

            is AddTransactionAction.AddCategoryClick ->
                onOpenCategoryEditorDialog(
                    CategoryUiModel.empty(),
                    action.parent,
                    false
                )

            is AddTransactionAction.EditCategoryClick ->
                onOpenCategoryEditorDialog(
                    action.category,
                    action.parent,
                    true
                )

            AddTransactionAction.CloseAddCategoryDialog ->
                onPopFullScreenDialog()

            is AddTransactionAction.EditAccountClick ->
                onEditAccountClicked(action.account)

            is AddTransactionAction.AddAccountClick ->
                onAddAccountClicked(action.type)

            is AddTransactionAction.OpenAccountsDialog -> {

                transactionTypeInAccountsDialog =
                    action.type

                onChangeFullScreenDialogState(
                    FullScreenDialog.Accounts
                )
            }


            // Selection

            is AddTransactionAction.CategorySelected ->
                onCategorySelected(action.category)

            is AddTransactionAction.AccountChanged ->
                onAccountChanged(action.newAccount)


            // Overlay dialogs

            AddTransactionAction.CloseOverlayDialog ->
                onChangeOverlayDialogState(
                    OverlayDialog.None
                )

            AddTransactionAction.OpenDateDialog ->
                onChangeOverlayDialogState(
                    OverlayDialog.Date
                )

            is AddTransactionAction.ShowAccountArchiveConfirmDialog -> {
                tempAccountForArchive = action.account
                onChangeOverlayDialogState(OverlayDialog.AccountArchiveConfirm)
            }

            is AddTransactionAction.ShowCategoryArchiveConfirmDialog -> {
                tempCategoryForArchive = action.category
                onChangeOverlayDialogState(OverlayDialog.CategoryArchiveConfirm)
            }


            AddTransactionAction.OpenAmountInput ->
                onChangeOverlayDialogState(
                    OverlayDialog.AmountInput
                )

            AddTransactionAction.OpenCommentDialog ->
                onChangeOverlayDialogState(
                    OverlayDialog.Comment
                )

            is AddTransactionAction.CommentChanged ->
                onCommentChanged(action.comment)

            is AddTransactionAction.DatePicked ->
                onDatePicked(action.date)


            // Save

            is AddTransactionAction.SaveCategory ->
                onSaveCategory(action.category)

            is AddTransactionAction.SaveAccount ->
                onSaveAccount(action.newState)

            AddTransactionAction.SaveTransaction ->
                onSaveTransaction()


            AddTransactionAction.ArchiveAccount -> onArchiveAccount()
            AddTransactionAction.ArchiveCategory -> onArchiveCategory()
            is AddTransactionAction.RestoreCategory -> onRestoreCategory(action.category)
        }
    }


    // =========================================================================
    // Transaction
    // =========================================================================

    private fun onSaveTransaction() {

        val error =
            validate()

        if (error == EmptyError.IDLE) {

            val currentState =
                _state.value

            val instantDate = calendar.value?.let {
                calendarEngineProvider
                    .get(it)
                    .toInstant(currentState.selectedDate)
            } ?: return

            _state.update { it.copy(isSaving = true) }

            if (isEditMode)
                editTransaction(currentState, instantDate)
            else
                addTransaction(currentState, instantDate)

            return
        }


        val errorMessage =
            error.getErrorMessageResId()
                ?.let(UiText::Resource)


        if (error == EmptyError.TRANSFER_SAME_ACCOUNT) {

            viewModelScope.launch {

                errorMessage?.let {
                    _uiEffect.send(
                        AddTransactionUiEffect.ShowToast(it)
                    )
                }
            }
        }


        _state.update { currentState ->

            val resetState =
                currentState.copy(
                    amountEmptyErrorMessage = null,
                    accountEmptyErrorMessage = null,
                    categoryEmptyErrorMessage = null,
                    toAccountEmptyErrorMessage = null,
                    fromAccountEmptyErrorMessage = null
                )

            when (error) {

                EmptyError.EMPTY_AMOUNT ->
                    resetState.copy(
                        amountEmptyErrorMessage = errorMessage
                    )

                EmptyError.ACCOUNT ->
                    resetState.copy(
                        accountEmptyErrorMessage = errorMessage
                    )

                EmptyError.CATEGORY ->
                    resetState.copy(
                        categoryEmptyErrorMessage = errorMessage
                    )

                EmptyError.TO_ACCOUNT ->
                    resetState.copy(
                        toAccountEmptyErrorMessage = errorMessage
                    )

                EmptyError.FROM_ACCOUNT ->
                    resetState.copy(
                        fromAccountEmptyErrorMessage = errorMessage
                    )

                else ->
                    resetState
            }
        }
    }

    private fun editTransaction(currentState: AddTransactionState, instantDate: Instant) {
        viewModelScope.launch {
            val transaction = transactionUseCase.getTransaction(TransactionId(transactionId!!))
            if (isTypeTransfer())
                transactionUseCase.updateTransfer(
                    transfer = Transfer(
                        transaction.transferId!!,
                        currentState.transferFromAccount!!.id,
                        currentState.transferToAccount!!.id,
                        Money(currentState.amount),
                        instantDate,
                        currentState.comment,
                    ),
                    onSuccess = {
                        _state.update { it.copy(isSaving = false) }
                        viewModelScope.launch { _uiEffect.send(AddTransactionUiEffect.NavigateUp) }
                    },
                    onError = {
                        _state.update { it.copy(isSaving = false) }
                        viewModelScope.launch { _uiEffect.send(AddTransactionUiEffect.ShowToast(UiText.Dynamic("Error"))) }
                    }
                )
            else
                transactionUseCase.updateTransaction(
                    transaction = transaction.copy(
                        accountId = currentState.selectedAccount!!.id,
                        amount = Money(currentState.amount),
                        categoryId = CategoryId(currentState.selectedCategory!!.id),
                        date = instantDate,
                        note = currentState.comment
                    ), onSuccess = {
                        viewModelScope.launch { _uiEffect.send(AddTransactionUiEffect.NavigateUp) }
                    }, onError = {}
                )
        }
    }

    private fun addTransaction(
        currentState: AddTransactionState,
        instantDate: Instant,
    ) {
        if (isTypeTransfer()) {

            viewModelScope.launch {

                transactionUseCase.recordTransfer(
                    Money(currentState.amount),
                    currentState.transferFromAccount!!.id,
                    currentState.transferToAccount!!.id,
                    instantDate,
                    currentState.comment,

                    onSuccess = {
                        viewModelScope.launch {
                            _state.update { it.copy(isSaving = false) }
                            _uiEffect.send(
                                AddTransactionUiEffect.NavigateUp
                            )
                        }
                    },

                    onError = {
                        _state.update { it.copy(isSaving = false) }
                        viewModelScope.launch { _uiEffect.send(AddTransactionUiEffect.ShowToast(UiText.Dynamic("Error"))) }

                    }
                )
            }

        } else {
            viewModelScope.launch {

                transactionUseCase.recordTransaction(
                    type = currentState.transactionType,
                    amount = Money(currentState.amount),
                    category = CategoryId(
                        currentState.selectedCategory!!.id
                    ),
                    account = currentState.selectedAccount!!.id,
                    date = instantDate,
                    note = currentState.comment,

                    onSuccess = {
                        viewModelScope.launch {
                            _uiEffect.send(
                                AddTransactionUiEffect.NavigateUp
                            )
                        }
                    },
                    onError = {}
                )
            }
        }
    }


    // =========================================================================
    // Account
    // =========================================================================

    private fun onAddAccountClicked(type: AccountType) {

        val emptyEditorState =
            AccountEditorState()

        _state.update {

            it.copy(
                accountEditorState =
                emptyEditorState.copy(
                    account =
                    emptyEditorState.account.copy(
                        type = type
                    )
                )
            )
        }

        onChangeFullScreenDialogState(
            FullScreenDialog.AccountEditor
        )
    }


    private fun onArchiveAccount() {
        tempAccountForArchive?.let {
            viewModelScope.launch {
                accountUseCase.changeArchiveStatus.archive(it)
            }
        }
        onChangeOverlayDialogState(OverlayDialog.None)
    }

    private fun onSaveAccount(
        newState: AccountEditorState,
    ) {

        viewModelScope.launch {

            if (newState.isUpdating) {

                accountUseCase.update(
                    newState.account,
                    Money(newState.balance)
                )

            } else {

                accountUseCase.create(
                    newState.account,
                    Money(newState.balance)
                )
            }
        }
    }


    private fun onEditAccountClicked(
        account: Account,
    ) {

        viewModelScope.launch {

            val initialBalance =
                transactionUseCase.getInitialBalance(
                    account.id
                )

            _state.update {

                it.copy(
                    accountEditorState =
                    AccountEditorState(
                        account = account,
                        balance = initialBalance.amount.amount,
                        isUpdating = true
                    )
                )
            }

            onChangeFullScreenDialogState(
                FullScreenDialog.AccountEditor
            )
        }
    }


    private fun onAccountChanged(
        newAccount: Account,
    ) {

        transactionTypeInAccountsDialog?.let { type ->

            when (type) {

                TransactionType.INITIAL_BALANCE,
                TransactionType.INCOME,
                TransactionType.EXPENSE,
                -> {

                    _state.update {

                        it.copy(
                            selectedAccount = newAccount,
                            accountEmptyErrorMessage = null
                        )
                    }
                }

                TransactionType.TRANSFER_IN -> {

                    _state.update {

                        it.copy(
                            transferToAccount = newAccount,
                            toAccountEmptyErrorMessage = null
                        )
                    }
                }

                TransactionType.TRANSFER_OUT -> {

                    _state.update {

                        it.copy(
                            transferFromAccount = newAccount,
                            fromAccountEmptyErrorMessage = null
                        )
                    }
                }
            }
        }
    }


    // =========================================================================
    // Category
    // =========================================================================

    private fun onSaveCategory(
        category: CategoryUiModel,
    ) {

        viewModelScope.launch {

            val domainModel =
                uiCategoryMapper.mapToDomainModel(
                    category
                )

            categoryUseCase.add(domainModel)
        }
    }


    private fun onArchiveCategory() {
        tempCategoryForArchive?.let { category ->
            viewModelScope.launch {
                categoryUseCase.changeCategoryArchiveStatus.archive(
                    uiCategoryMapper.mapToDomainModel(
                        category
                    )
                )
                onChangeOverlayDialogState(OverlayDialog.None)
            }
        }
    }

    private fun onRestoreCategory(category: CategoryUiModel) {
        viewModelScope.launch {
            categoryUseCase.changeCategoryArchiveStatus.unarchive(
                uiCategoryMapper.mapToDomainModel(
                    category
                )
            )
        }
    }

    private fun onOpenCategoryEditorDialog(
        category: CategoryUiModel,
        parent: CategoryUiModel?,
        isUpdating: Boolean,
    ) {

        val incomeParents =
            incomeCategory.filter {
                it.parentId == null
            }

        val expenseParents =
            expenseCategory.filter {
                it.parentId == null
            }

        val categoryType =
            if (
                _state.value.transactionType ==
                TransactionType.INCOME
            ) {
                CategoryType.INCOME
            } else {
                CategoryType.EXPENSE
            }

        updateAddCategoryDialogState {

            CategoryEditorState(
                category.copy(
                    type = categoryType
                ),
                isUpdating,
                parent,
                incomeParents,
                expenseParents
            )
        }

        onChangeFullScreenDialogState(
            FullScreenDialog.AddCategory
        )
    }


    private fun onCategorySelected(
        category: CategoryUiModel,
    ) {

        _state.update {

            it.copy(
                selectedCategory = category,
                categoryEmptyErrorMessage = null
            )
        }
    }


    private fun onTransactionTypeChanged(
        ordinal: Int,
    ) {

        val type =
            TransactionType.entries[ordinal]

        val categoryList =
            when (type) {

                TransactionType.EXPENSE ->
                    expenseCategory

                TransactionType.INCOME ->
                    incomeCategory

                else ->
                    incomeCategory
            }

        _state.update {

            it.copy(
                transactionType = type,
                selectedCategory = null,
                categoryEmptyErrorMessage = null,
                categories = categoryList.toSections()
            )
        }
    }


    private fun refreshCategorySections() {

        val type =
            _state.value.transactionType

        val categories =
            when (type) {

                TransactionType.INCOME ->
                    incomeCategory

                TransactionType.EXPENSE ->
                    expenseCategory

                else ->
                    emptyList()
            }

        _state.update {
            it.copy(
                categories = categories.toSections()
            )
        }
    }


    // =========================================================================
    // Date / Amount / Comment
    // =========================================================================

    private fun onDatePicked(
        date: AppDateTime,
    ) {

        val calendarEngine = calendar.value?.let {
            calendarEngineProvider.get(
                it
            )
        } ?: return

        val formattedDate = locale.value?.let {
            calendarEngine.formatDateTime(
                date,
                it.locale
            )
        } ?: return

        _state.update {

            it.copy(
                selectedDate = date,
                formattedSelectedDate = formattedDate
            )
        }
    }


    private fun onCommentChanged(
        comment: String,
    ) {

        if (comment.isNotBlank()) {

            _state.update {
                it.copy(
                    comment = comment
                )
            }
        }

        onChangeOverlayDialogState(
            OverlayDialog.None
        )
    }


    private fun onAmountChanged(
        amount: BigDecimal,
    ) {

        _state.update {

            it.copy(
                amount = amount,
                amountEmptyErrorMessage = null
            )
        }

        onChangeOverlayDialogState(
            OverlayDialog.None
        )
    }


    // =========================================================================
    // Dialog
    // =========================================================================

    private fun onPopFullScreenDialog() {

        if (dialogStack.value.isEmpty()) {
            return
        }

        dialogStack.update {
            it.dropLast(1)
        }
    }


    private fun onChangeFullScreenDialogState(
        newDialog: FullScreenDialog,
    ) {

        dialogStack.update {
            it + newDialog
        }
    }


    private fun onChangeOverlayDialogState(
        newState: OverlayDialog,
    ) {

        _state.update {
            it.copy(
                overlayDialog = newState
            )
        }
    }


    private fun updateAddCategoryDialogState(
        update: (CategoryEditorState) -> CategoryEditorState,
    ) {

        _state.update {

            it.copy(
                categoryEditorState =
                update(it.categoryEditorState)
            )
        }
    }


    private fun onNavigateIconClick() {

        if (_state.value.fullScreenDialogStack.isNotEmpty()) {

            onPopFullScreenDialog()
            return
        }

        viewModelScope.launch {

            _uiEffect.send(
                AddTransactionUiEffect.NavigateUp
            )
        }
    }


    // =========================================================================
    // Helpers
    // =========================================================================

    private fun List<CategoryUiModel>.toSections():
            List<CategorySection> {

        val headers =
            filter {
                it.parentId.isNullOrBlank()
            }

        val children =
            filter {
                !it.parentId.isNullOrBlank()
            }

        val childrenGrouped =
            children.groupBy {
                it.parentId
            }

        return headers.map { header ->

            CategorySection(
                header = header,
                children =
                childrenGrouped[header.id]
                    .orEmpty()
            )
        }
    }


    private fun isTypeTransfer(): Boolean {

        return when (_state.value.transactionType) {

            TransactionType.TRANSFER_IN,
            TransactionType.TRANSFER_OUT,
            ->
                true

            else ->
                false
        }
    }


    private fun validate(): EmptyError {

        val currentState =
            _state.value

        if (currentState.amount == BigDecimal.ZERO) {
            return EmptyError.EMPTY_AMOUNT
        }

        if (isTypeTransfer()) {

            if (currentState.transferFromAccount == null) {
                return EmptyError.FROM_ACCOUNT
            }

            if (currentState.transferToAccount == null) {
                return EmptyError.TO_ACCOUNT
            }

            if (
                currentState.transferToAccount.id.id ==
                currentState.transferFromAccount.id.id
            ) {
                return EmptyError.TRANSFER_SAME_ACCOUNT
            }

        } else {

            if (currentState.selectedCategory == null) {
                return EmptyError.CATEGORY
            }

            if (currentState.selectedAccount == null) {
                return EmptyError.ACCOUNT
            }
        }

        return EmptyError.IDLE
    }


    @StringRes
    fun EmptyError.getErrorMessageResId(): Int? {

        return when (this) {

            EmptyError.EMPTY_AMOUNT ->
                R.string.error_empty_amount

            EmptyError.ACCOUNT ->
                R.string.error_select_account

            EmptyError.CATEGORY ->
                R.string.error_select_category

            EmptyError.FROM_ACCOUNT ->
                R.string.error_select_from_account

            EmptyError.TO_ACCOUNT ->
                R.string.error_select_to_account

            EmptyError.TRANSFER_SAME_ACCOUNT ->
                R.string.error_same_account

            EmptyError.IDLE ->
                null
        }
    }
}


enum class EmptyError {
    EMPTY_AMOUNT,
    ACCOUNT,
    CATEGORY,
    TO_ACCOUNT,
    FROM_ACCOUNT,
    TRANSFER_SAME_ACCOUNT,
    IDLE
}