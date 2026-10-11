package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status != 'DELIVERED' AND status != 'CANCELLED' ORDER BY createdAt DESC")
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE assignedDriverId = :driverId ORDER BY createdAt DESC")
    fun getOrdersForDriver(driverId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String)

    @Query("UPDATE orders SET assignedDriverId = :driverId, assignedDriverName = :driverName, assignedDriverPhone = :driverPhone, estimateSource = '306 Driver Fleet', status = 'OUT_FOR_DELIVERY' WHERE orderId = :orderId")
    suspend fun assignDriverToOrder(orderId: String, driverId: String, driverName: String, driverPhone: String)

    @Query("UPDATE orders SET thirdPartyProvider = :provider, thirdPartyTrackingRef = :trackingRef, thirdPartyStatus = 'DISPATCHED', estimateSource = :provider, status = 'OUT_FOR_DELIVERY' WHERE orderId = :orderId")
    suspend fun dispatchThirdPartyCourier(orderId: String, provider: String, trackingRef: String)

    @Query("UPDATE orders SET failedAttemptReason = :reason, specialNotes = :notes WHERE orderId = :orderId")
    suspend fun recordDeliveryAttempt(orderId: String, reason: String, notes: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsSync(orderId: String): List<OrderItemEntity>
}
