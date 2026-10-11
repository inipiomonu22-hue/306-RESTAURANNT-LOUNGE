package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val priceZar: Double,
    val category: String, // Rice, Soups & Swallows, Everyday, Grills & Suya, Seafood, Drinks & Cocktails, Sides
    val portionDescription: String = "Regular Portion",
    val spiceLevel: String = "Medium", // Mild, Medium, Hot, Extra Hot
    val availableProteins: String = "Beef, Chicken, Goat Meat, Fish", // comma-separated
    val availableSwallows: String = "Pounded Yam, Eba, Semo, Amala", // comma-separated
    val dietaryTags: String = "HALAL,BESTSELLER", // comma-separated enum names
    val preparationTimeMins: Int = 25,
    val isAvailable: Boolean = true,
    val isDailySpecial: Boolean = false,
    val isChefRecommended: Boolean = false,
    val imageUrl: String = ""
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val fulfilmentType: String, // RESTAURANT_DELIVERY, THIRD_PARTY_COURIER, COLLECTION_PICKUP
    val deliveryAddress: String = "",
    val deliveryZoneName: String = "",
    val deliveryInstructions: String = "",
    val collectionTimeSlot: String = "", // e.g. "Today 19:30 - 20:00"
    val subtotalZar: Double,
    val deliveryFeeZar: Double,
    val discountZar: Double = 0.0,
    val totalZar: Double,
    val status: String, // RECEIVED, CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    val paymentMethod: String,
    val paymentStatus: String, // PENDING, PAID, FAILED, REFUNDED
    val paymentReference: String = "",
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val assignedDriverPhone: String? = null,
    val thirdPartyProvider: String? = null, // UBER_DIRECT, MR_D_FOOD, THE_COURIER_GUY, MANUAL_DISPATCH
    val thirdPartyTrackingRef: String? = null,
    val thirdPartyStatus: String? = null,
    val estimatedFulfilmentTime: String = "",
    val estimateSource: String = "306 Kitchen", // "306 Driver Fleet", "Uber Direct", "Mr D", "306 Kitchen"
    val specialNotes: String = "",
    val failedAttemptReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val menuItemId: String,
    val menuItemName: String,
    val selectedPortion: String = "Standard",
    val selectedProtein: String = "",
    val selectedSwallow: String = "",
    val selectedSpiceLevel: String = "",
    val quantity: Int = 1,
    val unitPriceZar: Double,
    val itemTotalZar: Double
)

@Entity(tableName = "delivery_zones")
data class DeliveryZoneEntity(
    @PrimaryKey
    val zoneName: String,
    val areaCode: String,
    val feeZar: Double,
    val minOrderZar: Double,
    val estimatedMins: Int,
    val isActive: Boolean = true
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
    val driverId: String,
    val name: String,
    val phone: String,
    val vehicleType: String, // "Motorbike", "Bakkie / Car", "Bicycle"
    val licensePlate: String = "CA 306-WP",
    val isAvailable: Boolean = true,
    val activeDeliveriesCount: Int = 0
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    val guestName: String,
    val guestPhone: String,
    val guestEmail: String,
    val reservationDate: String, // "2026-10-15"
    val reservationTime: String, // "19:30"
    val numberOfGuests: Int,
    val seatingArea: String = "Lounge & Dining", // "Main Lounge", "VIP Room", "Terrace"
    val occasion: String = "Casual Dining", // "Birthday", "Business Dinner", "Romantic Date", "Celebration"
    val specialRequests: String = "",
    val status: String = "CONFIRMED", // CONFIRMED, PENDING, SEATED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_specials")
data class DailySpecialEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val priceZar: Double,
    val bannerText: String = "TODAY'S SPECIAL",
    val isLiveToday: Boolean = true
)

@Entity(tableName = "restaurant_settings")
data class RestaurantSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val restaurantName: String = "306 Restaurant & Lounge",
    val physicalAddress: String = "306 Long Street, Cape Town City Centre, 8001",
    val officialPhone: String = "+27 21 306 3060",
    val officialWhatsApp: String = "+27 71 306 3060",
    val openingHours: String = "Mon - Sun: 12:00 PM - 11:30 PM",
    val freeDeliveryThresholdZar: Double = 500.0,
    val isOpenForOrders: Boolean = true,
    val activePaymentProvider: String = "PayFast / Yoco"
)
