package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RestaurantSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM restaurant_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<RestaurantSettingsEntity?>

    @Query("SELECT * FROM restaurant_settings WHERE id = 1")
    suspend fun getSettings(): RestaurantSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: RestaurantSettingsEntity)

    @Update
    suspend fun updateSettings(settings: RestaurantSettingsEntity)

    @Query("UPDATE restaurant_settings SET isOpenForOrders = :isOpen WHERE id = 1")
    suspend fun updateKitchenStatus(isOpen: Boolean)

    @Query("UPDATE restaurant_settings SET officialWhatsApp = :whatsapp WHERE id = 1")
    suspend fun updateWhatsAppNumber(whatsapp: String)
}
