package af.amir.hesabham.data.db.mapper

import af.amir.hesabham.data.db.entity.CategoryEntity
import af.amir.hesabham.domain.model.category.Category
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.category.CategoryType

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        this.categoryId.id,
        this.parentId?.id,
        this.name,
        this.categoryType.name,
        this.nameResKey,
        this.iconName,
        this.color,
        this.isDefault,
        this.isArchived
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        CategoryId(this.categoryId),
        this.name,
        this.parentId?.let { CategoryId(it) },
        CategoryType.valueOf(this.type),
        this.nameResKey,
        this.iconName,
        this.color,
        this.isDefault,
        this.isArchived
    )
}