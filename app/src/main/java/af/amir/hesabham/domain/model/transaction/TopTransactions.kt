package af.amir.hesabham.domain.model.transaction

data class TopTransactions(
    val expense :List<TransactionWithRelation>,
    val income :List<TransactionWithRelation>,
)
