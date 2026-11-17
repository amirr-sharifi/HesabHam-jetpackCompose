package af.amir.hesabham.domain.repo

import af.amir.hesabham.domain.model.category.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getInitialCategory(): Category?
    suspend fun addCategory(category: Category): Long
    suspend fun addCategory(categories: List<Category>): List<Long>
    suspend fun update(category: Category)
}