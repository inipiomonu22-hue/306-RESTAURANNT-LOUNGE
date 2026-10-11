package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailySpecialEntity
import com.example.data.model.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items ORDER BY category ASC, priceZar ASC")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category = :category AND isAvailable = 1")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE isDailySpecial = 1")
    fun getDailySpecialItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity)

    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)

    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: String, isAvailable: Boolean)

    @Query("UPDATE menu_items SET priceZar = :newPrice WHERE id = :id")
    suspend fun updatePrice(id: String, newPrice: Double)

    // Daily Specials Table
    @Query("SELECT * FROM daily_specials WHERE isLiveToday = 1")
    fun getActiveDailySpecials(): Flow<List<DailySpecialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySpecials(specials: List<DailySpecialEntity>)
}
