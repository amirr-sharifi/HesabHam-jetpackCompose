package af.amir.hesabham.data.db

import af.amir.hesabham.data.db.dao.CategoryDao
import af.amir.hesabham.data.db.mapper.toEntity
import af.amir.hesabham.domain.util.SystemCategoryIds
import af.amir.hesabham.data.util.readRawJsonFromAssetsFile
import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.category.CategoryType
import android.content.Context
import org.json.JSONObject

class DatabaseSeeder(
    private val context: Context,
    private val categoryDao: CategoryDao,

    ) {

    suspend fun seedSystemCategories() {
        val jsonCategories = fetchCategoriesFromJson()
        categoryDao.addCategory(jsonCategories.map { it.toEntity() })
    }


    suspend fun seedInitialCategory() {

        val initialCategory =
            Category(
                CategoryId(SystemCategoryIds.INITIAL_CATEGORY_ID),
                null,
                null,
                CategoryType.INITIAL_BALANCE,
                "category_initial",
                "Initial",
                "#8E24AA",
                true,
                isArchived = false
            )

        categoryDao.addCategory(initialCategory.toEntity())

    }

    private fun fetchCategoriesFromJson(): List<Category> {
        val rawJson = readRawJsonFromAssetsFile(context, "categories.json")
        val obj = JSONObject(rawJson)
        val categories = obj.getJSONArray("categories")
        val result = mutableListOf<Category>()

        for (i in 0 until categories.length()) {
            val categoryObject = categories.getJSONObject(i)
            val id = categoryObject.getString("id")
            val parentIdIsNull = categoryObject.isNull("parentId")
            val parentId = if(parentIdIsNull) null else categoryObject.getString("parentId")
            val type =
                if (categoryObject.getString("type") == "EXPENSE") CategoryType.EXPENSE else CategoryType.INCOME
            val nameResKey = categoryObject.getString("nameResKey")
            val iconName = categoryObject.getString("iconName")
            val color = categoryObject.getString("color")

            val category = Category(
                categoryId = CategoryId(id),
                name = null,
                parentId = parentId?.let { CategoryId(it) },
                categoryType = type,
                nameResKey = nameResKey,
                iconName = iconName,
                color = color,
                isDefault = true,
                isArchived = false
            )

            result += category

        }
        return result
    }
}