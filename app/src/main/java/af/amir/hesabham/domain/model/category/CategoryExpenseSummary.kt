package af.amir.hesabham.domain.model.category

import af.amir.hesabham.domain.model.Money

data class CategoryExpenseSummary(
    val categoryId : CategoryId,
    val name : String?,
    val nameResKey : String?,
    val color : String,
    val icon : String,
    val totalAmount : Money,
    val transactionCount : Int
)
