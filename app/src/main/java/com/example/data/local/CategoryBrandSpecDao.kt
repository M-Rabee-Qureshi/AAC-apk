package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BrandEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.SpecDefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}

@Dao
interface BrandDao {
    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(brand: BrandEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(brands: List<BrandEntity>)

    @Delete
    suspend fun delete(brand: BrandEntity)

    @Query("DELETE FROM brands WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM brands")
    suspend fun getCount(): Int
}

@Dao
interface SpecDefinitionDao {
    @Query("SELECT * FROM spec_definitions ORDER BY name ASC")
    fun getAllSpecs(): Flow<List<SpecDefinitionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(spec: SpecDefinitionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(specs: List<SpecDefinitionEntity>)

    @Delete
    suspend fun delete(spec: SpecDefinitionEntity)

    @Query("DELETE FROM spec_definitions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM spec_definitions")
    suspend fun getCount(): Int
}
