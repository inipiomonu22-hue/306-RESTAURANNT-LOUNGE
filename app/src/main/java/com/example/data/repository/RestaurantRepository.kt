package com.example.data.repository

import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.CourierProvider
import com.example.data.model.DailySpecialEntity
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.DriverEntity
import com.example.data.model.FulfilmentType
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RestaurantRepository(private val database: AppDatabase) {

    // In-memory Shopping Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // Menu Flows
    val allMenuItems: Flow<List<MenuItemEntity>> = database.menuDao().getAllMenuItems()
    val dailySpecials: Flow<List<DailySpecialEntity>> = database.menuDao().getActiveDailySpecials()

    // Orders Flows
    val allOrders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()
    val activeOrders: Flow<List<OrderEntity>> = database.orderDao().getActiveOrders()

    // Delivery & Drivers Flows
    val deliveryZones: Flow<List<DeliveryZoneEntity>> = database.deliveryDao().getActiveDeliveryZones()
    val allDeliveryZones: Flow<List<DeliveryZoneEntity>> = database.deliveryDao().getAllDeliveryZones()
    val availableDrivers: Flow<List<DriverEntity>> = database.deliveryDao().getAvailableDrivers()
    val allDrivers: Flow<List<DriverEntity>> = database.deliveryDao().getAllDrivers()

    // Reservations & Settings Flows
    val reservations: Flow<List<ReservationEntity>> = database.reservationDao().getAllReservations()
    val settingsFlow: Flow<RestaurantSettingsEntity?> = database.settingsDao().getSettingsFlow()

    fun getOrderFlow(orderId: String): Flow<OrderEntity?> = database.orderDao().getOrderByIdFlow(orderId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = database.orderDao().getOrderItems(orderId)
    fun getOrdersForDriver(driverId: String): Flow<List<OrderEntity>> = database.orderDao().getOrdersForDriver(driverId)

    // Cart Management
    fun addToCart(cartItem: CartItem) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.menuItem.id == cartItem.menuItem.id &&
                    it.selectedProtein == cartItem.selectedProtein &&
                    it.selectedSwallow == cartItem.selectedSwallow &&
                    it.selectedSpiceLevel == cartItem.selectedSpiceLevel &&
                    it.specialInstructions == cartItem.specialInstructions
        }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + cartItem.quantity)
        } else {
            current.add(cartItem)
        }
        _cartItems.value = current
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.cartItemId == cartItemId }
        if (index >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQuantity)
            }
            _cartItems.value = current
        }
    }

    fun removeCartItem(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.cartItemId != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    val cartSubtotalZar: Double
        get() = _cartItems.value.sumOf { it.totalZar }

    // Delivery Fee Calculator
    fun calculateDeliveryFee(
        fulfilmentType: FulfilmentType,
        subtotalZar: Double,
        zone: DeliveryZoneEntity?,
        courierProvider: CourierProvider = CourierProvider.UBER_DIRECT
    ): Double {
        if (fulfilmentType == FulfilmentType.COLLECTION_PICKUP) {
            return 0.0
        }
        // Free delivery threshold check (R 550 default)
        if (subtotalZar >= 550.00 && fulfilmentType == FulfilmentType.RESTAURANT_DELIVERY) {
            return 0.0
        }
        return when (fulfilmentType) {
            FulfilmentType.COLLECTION_PICKUP -> 0.0
            FulfilmentType.RESTAURANT_DELIVERY -> zone?.feeZar ?: 45.00
            FulfilmentType.THIRD_PARTY_COURIER -> {
                // Modular courier quotation rate
                when (courierProvider) {
                    CourierProvider.UBER_DIRECT -> (zone?.feeZar ?: 45.00) + 10.00
                    CourierProvider.MR_D_FOOD -> (zone?.feeZar ?: 40.00) + 5.00
                    CourierProvider.THE_COURIER_GUY -> 65.00
                    CourierProvider.MANUAL_DISPATCH -> zone?.feeZar ?: 50.00
                }
            }
        }
    }

    // Place Order
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        fulfilmentType: FulfilmentType,
        deliveryAddress: String,
        deliveryZone: DeliveryZoneEntity?,
        deliveryInstructions: String,
        collectionTimeSlot: String,
        paymentMethod: PaymentMethod,
        specialNotes: String,
        selectedCourier: CourierProvider? = null
    ): OrderEntity {
        val orderNum = (1000..9999).random()
        val orderId = "306-$orderNum"
        val subtotal = cartSubtotalZar
        val deliveryFee = calculateDeliveryFee(
            fulfilmentType = fulfilmentType,
            subtotalZar = subtotal,
            zone = deliveryZone,
            courierProvider = selectedCourier ?: CourierProvider.UBER_DIRECT
        )
        val total = subtotal + deliveryFee

        val isPaidNow = paymentMethod in listOf(
            PaymentMethod.PAYFAST,
            PaymentMethod.YOCO,
            PaymentMethod.PEACH_PAYMENTS,
            PaymentMethod.INSTANT_EFT,
            PaymentMethod.SNAPSCAN_ZAPPER
        )

        val initialStatus = if (isPaidNow) OrderStatus.CONFIRMED.name else OrderStatus.RECEIVED.name
        val paymentStatus = if (isPaidNow) PaymentStatus.PAID.name else PaymentStatus.PENDING.name
        val paymentRef = if (isPaidNow) "PAY-306-${UUID.randomUUID().toString().take(8).uppercase()}" else ""

        val etaTime = when (fulfilmentType) {
            FulfilmentType.COLLECTION_PICKUP -> "Ready in 20-30 mins"
            FulfilmentType.RESTAURANT_DELIVERY -> "${deliveryZone?.estimatedMins ?: 35} mins (Restaurant Fleet)"
            FulfilmentType.THIRD_PARTY_COURIER -> "${(deliveryZone?.estimatedMins ?: 35) + 5} mins (${selectedCourier?.providerName ?: "Courier"})"
        }

        val estimateSource = when (fulfilmentType) {
            FulfilmentType.COLLECTION_PICKUP -> "306 Kitchen"
            FulfilmentType.RESTAURANT_DELIVERY -> "306 Driver Fleet"
            FulfilmentType.THIRD_PARTY_COURIER -> selectedCourier?.providerName ?: "External Courier"
        }

        val thirdPartyTracking = if (fulfilmentType == FulfilmentType.THIRD_PARTY_COURIER && selectedCourier != null) {
            "${selectedCourier.trackingPrefix}${UUID.randomUUID().toString().take(6).uppercase()}"
        } else null

        val order = OrderEntity(
            orderId = orderId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerEmail = customerEmail,
            fulfilmentType = fulfilmentType.name,
            deliveryAddress = if (fulfilmentType == FulfilmentType.COLLECTION_PICKUP) "Collection at 306 Long Street, Cape Town" else deliveryAddress,
            deliveryZoneName = deliveryZone?.zoneName ?: "Cape Town City Bowl",
            deliveryInstructions = deliveryInstructions,
            collectionTimeSlot = collectionTimeSlot,
            subtotalZar = subtotal,
            deliveryFeeZar = deliveryFee,
            totalZar = total,
            status = initialStatus,
            paymentMethod = paymentMethod.name,
            paymentStatus = paymentStatus,
            paymentReference = paymentRef,
            thirdPartyProvider = selectedCourier?.name,
            thirdPartyTrackingRef = thirdPartyTracking,
            thirdPartyStatus = if (thirdPartyTracking != null) "PENDING_PICKUP" else null,
            estimatedFulfilmentTime = etaTime,
            estimateSource = estimateSource,
            specialNotes = specialNotes,
            createdAt = System.currentTimeMillis()
        )

        database.orderDao().insertOrder(order)

        // Save order items
        val orderItems = _cartItems.value.map { cart ->
            OrderItemEntity(
                orderId = orderId,
                menuItemId = cart.menuItem.id,
                menuItemName = cart.menuItem.name,
                selectedPortion = cart.selectedPortion,
                selectedProtein = cart.selectedProtein,
                selectedSwallow = cart.selectedSwallow,
                selectedSpiceLevel = cart.selectedSpiceLevel,
                quantity = cart.quantity,
                unitPriceZar = cart.unitPriceZar,
                itemTotalZar = cart.totalZar
            )
        }
        database.orderDao().insertOrderItems(orderItems)

        // Clear cart after placing order
        clearCart()
        return order
    }

    // WhatsApp Message Generators
    suspend fun generateWhatsAppOrderUrl(orderId: String, directEnquiryOnly: Boolean = false): String {
        val order = database.orderDao().getOrderById(orderId) ?: return ""
        val items = database.orderDao().getOrderItemsSync(orderId)
        val settings = database.settingsDao().getSettings()

        val phoneClean = (settings?.officialWhatsApp ?: "+27 71 306 3060").replace(Regex("[^0-9]"), "")

        val sb = StringBuilder()
        sb.appendLine("🇳🇬 *306 RESTAURANT & LOUNGE — CAPE TOWN*")
        sb.appendLine("📍 306 Long Street, Cape Town City Centre")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━")
        if (directEnquiryOnly) {
            sb.appendLine("📋 *NEW ORDER REQUEST: #${order.orderId}*")
        } else {
            sb.appendLine("✅ *CONFIRMED ORDER SUMMARY: #${order.orderId}*")
            sb.appendLine("💳 Payment Status: ${order.paymentStatus} (${order.paymentMethod})")
            if (order.paymentReference.isNotEmpty()) {
                sb.appendLine("Ref: ${order.paymentReference}")
            }
        }
        sb.appendLine("👤 *Customer:* ${order.customerName}")
        sb.appendLine("📞 *Contact:* ${order.customerPhone}")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("🍽️ *ORDERED MEALS:*")

        items.forEachIndexed { i, itm ->
            sb.append("${i + 1}. *${itm.quantity}x ${itm.menuItemName}* (R ${String.format(Locale.US, "%.2f", itm.itemTotalZar)})\n")
            if (itm.selectedProtein.isNotEmpty()) sb.append("   • Protein: ${itm.selectedProtein}\n")
            if (itm.selectedSwallow.isNotEmpty()) sb.append("   • Swallow: ${itm.selectedSwallow}\n")
            if (itm.selectedSpiceLevel.isNotEmpty()) sb.append("   • Spice: ${itm.selectedSpiceLevel}\n")
        }

        sb.appendLine("━━━━━━━━━━━━━━━━━━━")
        val fType = try { FulfilmentType.valueOf(order.fulfilmentType) } catch (e: Exception) { FulfilmentType.RESTAURANT_DELIVERY }
        when (fType) {
            FulfilmentType.COLLECTION_PICKUP -> {
                sb.appendLine("🛍️ *FULFILMENT: Collection / Pickup*")
                sb.appendLine("🕒 Time Slot: ${order.collectionTimeSlot.ifEmpty { "ASAP" }}")
                sb.appendLine("📍 Pickup Desk: 306 Long Street, Cape Town")
            }
            FulfilmentType.RESTAURANT_DELIVERY -> {
                sb.appendLine("🛵 *FULFILMENT: 306 Restaurant Delivery*")
                sb.appendLine("📍 Address: ${order.deliveryAddress}")
                sb.appendLine("🏙️ Zone: ${order.deliveryZoneName}")
                if (order.deliveryInstructions.isNotEmpty()) {
                    sb.appendLine("📝 Instructions: ${order.deliveryInstructions}")
                }
            }
            FulfilmentType.THIRD_PARTY_COURIER -> {
                sb.appendLine("📦 *FULFILMENT: External Courier Delivery*")
                sb.appendLine("🚚 Provider: ${order.thirdPartyProvider ?: "Uber Direct"}")
                sb.appendLine("📍 Address: ${order.deliveryAddress}")
                if (!order.thirdPartyTrackingRef.isNullOrEmpty()) {
                    sb.appendLine("🔍 Tracking Ref: ${order.thirdPartyTrackingRef}")
                }
            }
        }

        sb.appendLine("━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Subtotal: R ${String.format(Locale.US, "%.2f", order.subtotalZar)}")
        sb.appendLine("Delivery Fee: R ${String.format(Locale.US, "%.2f", order.deliveryFeeZar)}")
        sb.appendLine("💰 *TOTAL: R ${String.format(Locale.US, "%.2f", order.totalZar)}*")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("Thank you for choosing 306 Restaurant & Lounge!")

        val encodedMessage = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.name())
        return "https://api.whatsapp.com/send?phone=$phoneClean&text=$encodedMessage"
    }

    // Driver Assignment (Staff / Admin Action)
    suspend fun assignDriver(orderId: String, driverId: String) {
        val driver = database.deliveryDao().getDriverById(driverId) ?: return
        database.orderDao().assignDriverToOrder(
            orderId = orderId,
            driverId = driver.driverId,
            driverName = driver.name,
            driverPhone = driver.phone
        )
    }

    // Third-party courier dispatch (Staff / Admin Action)
    suspend fun dispatchCourier(orderId: String, provider: CourierProvider, customTracking: String = "") {
        val trackingRef = if (customTracking.isNotBlank()) customTracking else "${provider.trackingPrefix}${UUID.randomUUID().toString().take(6).uppercase()}"
        database.orderDao().dispatchThirdPartyCourier(
            orderId = orderId,
            provider = provider.providerName,
            trackingRef = trackingRef
        )
    }

    // Order status update
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        database.orderDao().updateOrderStatus(orderId, status.name)
    }

    // Driver portal status update & notes
    suspend fun recordDriverAttempt(orderId: String, reason: String, notes: String) {
        database.orderDao().recordDeliveryAttempt(orderId, reason, notes)
    }

    // Table Reservations
    suspend fun bookTable(
        guestName: String,
        guestPhone: String,
        guestEmail: String,
        date: String,
        time: String,
        numberOfGuests: Int,
        seatingArea: String,
        occasion: String,
        specialRequests: String
    ): Result<ReservationEntity> {
        // Prevent booking conflicts (max 4 concurrent bookings per 30-min time slot)
        val existingCount = database.reservationDao().getBookingsCountForSlot(date, time)
        if (existingCount >= 5) {
            return Result.failure(Exception("Sorry, all tables for $date at $time are fully booked. Please choose another time."))
        }

        val reservation = ReservationEntity(
            id = "RES-${(100..999).random()}",
            guestName = guestName,
            guestPhone = guestPhone,
            guestEmail = guestEmail,
            reservationDate = date,
            reservationTime = time,
            numberOfGuests = numberOfGuests,
            seatingArea = seatingArea,
            occasion = occasion,
            specialRequests = specialRequests,
            status = "CONFIRMED"
        )
        database.reservationDao().insertReservation(reservation)
        return Result.success(reservation)
    }

    // Admin Settings Updates
    suspend fun updateKitchenStatus(isOpen: Boolean) {
        database.settingsDao().updateKitchenStatus(isOpen)
    }

    suspend fun updateWhatsAppNumber(number: String) {
        database.settingsDao().updateWhatsAppNumber(number)
    }

    suspend fun updateMenuItemPrice(id: String, newPrice: Double) {
        database.menuDao().updatePrice(id, newPrice)
    }

    suspend fun toggleMenuItemAvailability(id: String, isAvailable: Boolean) {
        database.menuDao().updateAvailability(id, isAvailable)
    }

    suspend fun updateDeliveryZone(zone: DeliveryZoneEntity) {
        database.deliveryDao().updateZone(zone)
    }

    suspend fun addDriver(driver: DriverEntity) {
        database.deliveryDao().insertDriver(driver)
    }

    suspend fun toggleDriverAvailability(driverId: String, isAvailable: Boolean) {
        database.deliveryDao().updateDriverAvailability(driverId, isAvailable)
    }
}
