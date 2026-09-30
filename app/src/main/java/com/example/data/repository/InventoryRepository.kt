package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ItemEntity
import com.example.data.model.SpecDefinitionEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val database: AppDatabase) {

    fun filterItems(
        query: String,
        category: String,
        brand: String,
        mm: Int?
    ): Flow<List<ItemEntity>> {
        return database.itemDao().filterItems(
            query = query.trim(),
            category = category,
            brand = brand,
            mm = mm
        )
    }

    fun getAllItems(): Flow<List<ItemEntity>> = database.itemDao().getAllItems()

    fun getAllCategories(): Flow<List<CategoryEntity>> = database.categoryDao().getAllCategories()

    fun getAllBrands(): Flow<List<BrandEntity>> = database.brandDao().getAllBrands()

    fun getAllSpecs(): Flow<List<SpecDefinitionEntity>> = database.specDefinitionDao().getAllSpecs()

    suspend fun insertItem(item: ItemEntity): Long {
        return database.itemDao().insert(item)
    }

    suspend fun updateItem(item: ItemEntity) {
        database.itemDao().update(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteItem(item: ItemEntity) {
        database.itemDao().delete(item)
    }

    suspend fun deleteAllItems() {
        database.itemDao().deleteAllItems()
    }

    suspend fun restoreDefaultItems() {
        database.itemDao().insertAll(com.example.data.local.DefaultData.defaultItems)
    }

    suspend fun bulkInsertItems(items: List<ItemEntity>): List<Long> {
        return database.itemDao().insertAll(items)
    }

    suspend fun addCategory(name: String): Long {
        return database.categoryDao().insert(CategoryEntity(name = name.trim()))
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        database.categoryDao().delete(category)
    }

    suspend fun addBrand(name: String): Long {
        return database.brandDao().insert(BrandEntity(name = name.trim()))
    }

    suspend fun deleteBrand(brand: BrandEntity) {
        database.brandDao().delete(brand)
    }

    suspend fun addSpec(name: String, defaultValue: String = ""): Long {
        return database.specDefinitionDao().insert(
            SpecDefinitionEntity(name = name.trim(), defaultValue = defaultValue.trim())
        )
    }

    suspend fun deleteSpec(spec: SpecDefinitionEntity) {
        database.specDefinitionDao().delete(spec)
    }
}
