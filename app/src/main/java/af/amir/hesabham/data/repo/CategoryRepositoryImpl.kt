package af.amir.hesabham.data.repo

import af.amir.hesabham.data.db.dao.CategoryDao
import af.amir.hesabham.data.db.mapper.toDomain
import af.amir.hesabham.data.db.mapper.toEntity
import af.amir.hesabham.domain.util.SystemCategoryIds
import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.repo.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao,
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> {
        return dao.getAllFlow().map { list -> list.map { it.toDomain() } }
    }


    override suspend fun getInitialCategory(): Category? {
        val category = dao.getCategoryById(SystemCategoryIds.INITIAL_CATEGORY_ID)
        return category?.toDomain()

    }

    override suspend fun addCategory(category: Category)=dao.addCategory(category.toEntity())

    override suspend fun addCategory(categories: List<Category>)=dao.addCategory(categories.map { it.toEntity() })
    override suspend fun update(category: Category) {
        dao.update(category.toEntity())
    }


}