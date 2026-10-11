package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {
    // Delivery Zones
    @Query("SELECT * FROM delivery_zones ORDER BY feeZar ASC")
    fun getAllDeliveryZones(): Flow<List<DeliveryZoneEntity>>

    @Query("SELECT * FROM delivery_zones WHERE isActive = 1 ORDER BY feeZar ASC")
    fun getActiveDeliveryZones(): Flow<List<DeliveryZoneEntity>>

    @Query("SELECT * FROM delivery_zones WHERE zoneName = :name")
    suspend fun getZoneByName(name: String): DeliveryZoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<DeliveryZoneEntity>)

    @Update
    suspend fun updateZone(zone: DeliveryZoneEntity)

    // Drivers
    @Query("SELECT * FROM drivers ORDER BY name ASC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailableDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE driverId = :id")
    suspend fun getDriverById(id: String): DriverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Query("UPDATE drivers SET isAvailable = :isAvailable WHERE driverId = :driverId")
    suspend fun updateDriverAvailability(driverId: String, isAvailable: Boolean)
}
