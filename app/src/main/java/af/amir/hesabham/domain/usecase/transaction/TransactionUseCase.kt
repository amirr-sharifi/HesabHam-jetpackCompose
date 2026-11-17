package af.amir.hesabham.domain.usecase.transaction

data class TransactionUseCase(
    val recordOpeningBalance: RecordOpeningBalanceTransactionUseCase,
    val recordTransfer: RecordTransferTransactionUseCase,
    val recordTransaction: RecordTransactionUseCase,
    val updateTransaction : UpdateTransactionUseCase,
    val updateTransfer : UpdateTransferUseCase,
    val deleteTransaction : DeleteTransactionUseCase,
    val getInitialBalance : GetInitialBalanceUseCase,
    val getTransfer: GetTransferUseCase,
    val getTransaction: GetTransactionUseCase,
    val monthlySummary: ObserveMonthlySummaryUseCase,
    val transactionWithRelations: ObserveTransactionWithRelationsUseCase,
    val categoryExpenseSummary : ObserveCategoryExpenseSummary,
    val topTransactions: ObserveTopTransactionsUseCase
)
