package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.FulfilmentType
import com.example.data.repository.RestaurantRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("306 Lounge", appName)
  }

  @Test
  fun `verify delivery fee calculations for all three fulfilment types`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    val repository = RestaurantRepository(inMemoryDb)

    val cbdZone = DeliveryZoneEntity(
      zoneName = "City Bowl & Cape Town CBD",
      areaCode = "8001",
      feeZar = 35.0,
      minOrderZar = 150.0,
      estimatedMins = 30
    )

    // 1. Collection/Pickup is always free (R 0.00)
    val pickupFee = repository.calculateDeliveryFee(
      fulfilmentType = FulfilmentType.COLLECTION_PICKUP,
      subtotalZar = 200.0,
      zone = cbdZone
    )
    assertEquals(0.0, pickupFee, 0.01)

    // 2. Restaurant Delivery below free threshold uses zone fee
    val restaurantFee = repository.calculateDeliveryFee(
      fulfilmentType = FulfilmentType.RESTAURANT_DELIVERY,
      subtotalZar = 200.0,
      zone = cbdZone
    )
    assertEquals(35.0, restaurantFee, 0.01)

    // 3. Restaurant Delivery above threshold (R 550) is free
    val freeRestaurantFee = repository.calculateDeliveryFee(
      fulfilmentType = FulfilmentType.RESTAURANT_DELIVERY,
      subtotalZar = 600.0,
      zone = cbdZone
    )
    assertEquals(0.0, freeRestaurantFee, 0.01)

    // 4. Third-party Courier calculation
    val courierFee = repository.calculateDeliveryFee(
      fulfilmentType = FulfilmentType.THIRD_PARTY_COURIER,
      subtotalZar = 200.0,
      zone = cbdZone,
      courierProvider = CourierProvider.UBER_DIRECT
    )
    assertEquals(45.0, courierFee, 0.01)

    inMemoryDb.close()
  }
}
