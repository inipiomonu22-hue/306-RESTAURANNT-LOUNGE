package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.DriverEntity
import com.example.data.model.FulfilmentType
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantSettingsEntity
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = RestaurantRepository(database)

    // Category and Search Filtering
    val selectedCategory = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")
    val dietaryFilter = MutableStateFlow<String?>(null)

    // Menu list filtered
    val menuItems: StateFlow<List<MenuItemEntity>> = combine(
        repository.allMenuItems,
        selectedCategory,
        searchQuery,
        dietaryFilter
    ) { items, cat, query, diet ->
        items.filter { item ->
            val matchesCategory = cat == "All" || item.category.equals(cat, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
            val matchesDiet = diet == null || item.dietaryTags.contains(diet, ignoreCase = true)
            matchesCategory && matchesQuery && matchesDiet
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailySpecials = repository.dailySpecials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    val cartItems = repository.cartItems
    val cartSubtotal = repository.cartItems.combine(flowOf(Unit)) { items, _ ->
        items.sumOf { it.totalZar }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount = repository.cartItems.combine(flowOf(Unit)) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Checkout Configuration
    val selectedFulfilmentType = MutableStateFlow(FulfilmentType.RESTAURANT_DELIVERY)
    val selectedZone = MutableStateFlow<DeliveryZoneEntity?>(null)
    val selectedCourier = MutableStateFlow(CourierProvider.UBER_DIRECT)

    val deliveryZones = repository.deliveryZones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDeliveryZones = repository.allDeliveryZones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calculatedDeliveryFee = combine(
        selectedFulfilmentType,
        cartSubtotal,
        selectedZone,
        selectedCourier
    ) { type, subtotal, zone, courier ->
        repository.calculateDeliveryFee(type, subtotal, zone, courier)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 45.0)

    val orderTotalZar = combine(cartSubtotal, calculatedDeliveryFee) { sub, fee ->
        sub + fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Orders Management
    val activeOrders = repository.activeOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allOrders = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tracking
    val trackedOrderId = MutableStateFlow<String?>(null)
    val trackedOrder: StateFlow<OrderEntity?> = trackedOrderId.flatMapLatest { id ->
        if (id != null) repository.getOrderFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Drivers & Fleet
    val availableDrivers = repository.availableDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDrivers = repository.allDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Driver Portal Selected Driver
    val currentDriverId = MutableStateFlow("DRV-101")
    val driverAssignedOrders: StateFlow<List<OrderEntity>> = currentDriverId.flatMapLatest { driverId ->
        repository.getOrdersForDriver(driverId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reservations & Settings
    val reservations = repository.reservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val restaurantSettings = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Dialog & Feedback State
    val uiMessage = MutableStateFlow<String?>(null)

    init {
        // Automatically default selected zone once zones are loaded
        viewModelScope.launch {
            repository.deliveryZones.collect { zones ->
                if (selectedZone.value == null && zones.isNotEmpty()) {
                    selectedZone.value = zones.first()
                }
            }
        }
    }

    fun addToCart(
        menuItem: MenuItemEntity,
        portion: String = "Regular Portion",
        protein: String = "",
        swallow: String = "",
        spice: String = "Medium Hot",
        notes: String = "",
        quantity: Int = 1
    ) {
        val cartItem = CartItem(
            menuItem = menuItem,
            selectedPortion = portion,
            selectedProtein = protein,
            selectedSwallow = swallow,
            selectedSpiceLevel = spice,
            specialInstructions = notes,
            quantity = quantity
        )
        repository.addToCart(cartItem)
        uiMessage.value = "Added ${menuItem.name} to order"
    }

    fun updateCartItemQuantity(cartItemId: String, qty: Int) {
        repository.updateCartItemQuantity(cartItemId, qty)
    }

    fun removeCartItem(cartItemId: String) {
        repository.removeCartItem(cartItemId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        deliveryAddress: String,
        deliveryInstructions: String,
        collectionTimeSlot: String,
        paymentMethod: PaymentMethod,
        specialNotes: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val order = repository.placeOrder(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerEmail = customerEmail,
                    fulfilmentType = selectedFulfilmentType.value,
                    deliveryAddress = deliveryAddress,
                    deliveryZone = selectedZone.value,
                    deliveryInstructions = deliveryInstructions,
                    collectionTimeSlot = collectionTimeSlot,
                    paymentMethod = paymentMethod,
                    specialNotes = specialNotes,
                    selectedCourier = if (selectedFulfilmentType.value == FulfilmentType.THIRD_PARTY_COURIER) selectedCourier.value else null
                )
                trackedOrderId.value = order.orderId
                uiMessage.value = "Order #${order.orderId} placed successfully!"
                onSuccess(order)
            } catch (e: Exception) {
                uiMessage.value = "Order failed: ${e.message}"
            }
        }
    }

    fun assignDriverToOrder(orderId: String, driverId: String) {
        viewModelScope.launch {
            repository.assignDriver(orderId, driverId)
            uiMessage.value = "Assigned driver to order #$orderId"
        }
    }

    fun dispatchThirdPartyCourier(orderId: String, provider: CourierProvider, trackingRef: String = "") {
        viewModelScope.launch {
            repository.dispatchCourier(orderId, provider, trackingRef)
            uiMessage.value = "Dispatched order #$orderId via ${provider.providerName}"
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            uiMessage.value = "Order #$orderId marked as ${status.label}"
        }
    }

    fun recordDeliveryAttempt(orderId: String, reason: String, notes: String) {
        viewModelScope.launch {
            repository.recordDriverAttempt(orderId, reason, notes)
            uiMessage.value = "Recorded delivery attempt for order #$orderId"
        }
    }

    fun bookTableReservation(
        guestName: String,
        guestPhone: String,
        guestEmail: String,
        date: String,
        time: String,
        numberOfGuests: Int,
        seatingArea: String,
        occasion: String,
        specialRequests: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.bookTable(
                guestName, guestPhone, guestEmail, date, time,
                numberOfGuests, seatingArea, occasion, specialRequests
            )
            result.onSuccess { res ->
                uiMessage.value = "Table booked! Booking #${res.id}"
                onResult(true, "Reservation #${res.id} confirmed for $date at $time!")
            }.onFailure { err ->
                uiMessage.value = err.message ?: "Booking error"
                onResult(false, err.message ?: "Could not complete booking.")
            }
        }
    }

    fun updateDishPrice(id: String, price: Double) {
        viewModelScope.launch {
            repository.updateMenuItemPrice(id, price)
            uiMessage.value = "Dish price updated"
        }
    }

    fun toggleDishAvailability(id: String, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.toggleMenuItemAvailability(id, isAvailable)
            uiMessage.value = if (isAvailable) "Dish is now available" else "Dish marked Sold Out"
        }
    }

    fun updateDeliveryZone(zone: DeliveryZoneEntity) {
        viewModelScope.launch {
            repository.updateDeliveryZone(zone)
            uiMessage.value = "Zone ${zone.zoneName} updated"
        }
    }

    fun addDriver(name: String, phone: String, vehicle: String, license: String) {
        viewModelScope.launch {
            val driver = DriverEntity(
                driverId = "DRV-${(104..999).random()}",
                name = name,
                phone = phone,
                vehicleType = vehicle,
                licensePlate = license,
                isAvailable = true
            )
            repository.addDriver(driver)
            uiMessage.value = "Driver ${driver.name} added to fleet"
        }
    }

    fun toggleDriverAvailability(driverId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.toggleDriverAvailability(driverId, isAvailable)
            uiMessage.value = "Driver availability updated"
        }
    }

    fun updateKitchenStatus(isOpen: Boolean) {
        viewModelScope.launch {
            repository.updateKitchenStatus(isOpen)
            uiMessage.value = if (isOpen) "Kitchen opened for orders" else "Kitchen closed"
        }
    }

    fun updateWhatsAppNumber(number: String) {
        viewModelScope.launch {
            repository.updateWhatsAppNumber(number)
            uiMessage.value = "WhatsApp contact updated to $number"
        }
    }

    fun clearUiMessage() {
        uiMessage.value = null
    }
}
