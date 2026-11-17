package af.amir.hesabham.data.db.entity

data class CategoryExpenseSummaryDbModel(
    val categoryId : String,
    val name : String?,
    val nameResKey : String?,
    val icon: String,
    val color : String,
    val totalAmount : Long,
    val transactionCount: Int
)