package com.example.data.model

enum class FulfilmentType(val title: String, val subtitle: String) {
    RESTAURANT_DELIVERY("Restaurant Fleet Delivery", "Delivered by 306 dedicated drivers in Cape Town"),
    THIRD_PARTY_COURIER("Third-Party Courier", "Dispatched via verified external courier (Uber Direct / Mr D / Courier Guy)"),
    COLLECTION_PICKUP("Collection / Pickup", "Collect directly from 306 Restaurant & Lounge, Cape Town")
}

enum class OrderStatus(val label: String, val description: String) {
    RECEIVED("Order Received", "Your order has reached 306 Lounge kitchen"),
    CONFIRMED("Confirmed", "Payment verified & order accepted"),
    PREPARING("Preparing", "Our Nigerian chefs are crafting your fresh meal"),
    READY_FOR_PICKUP("Ready for Collection", "Packaged hot & ready at 306 front desk"),
    OUT_FOR_DELIVERY("Out for Delivery", "En route to your Cape Town address"),
    DELIVERED("Delivered / Collected", "Order successfully fulfilled. Bon appétit!"),
    CANCELLED("Cancelled", "Order was cancelled")
}

enum class PaymentMethod(val displayName: String) {
    PAYFAST("PayFast (Cards & Instant EFT)"),
    YOCO("Yoco Card Checkout"),
    PEACH_PAYMENTS("Peach Payments"),
    INSTANT_EFT("Capitec Pay & Instant EFT"),
    SNAPSCAN_ZAPPER("SnapScan / Zapper QR"),
    CASH_ON_DELIVERY("Cash on Delivery (ZAR)"),
    CASH_ON_PICKUP("Pay at Restaurant on Collection")
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

enum class CourierProvider(val providerName: String, val trackingPrefix: String, val apiIntegrated: Boolean) {
    UBER_DIRECT("Uber Direct Cape Town", "UBR-CPT-", true),
    MR_D_FOOD("Mr D Merchant Delivery", "MRD-", true),
    THE_COURIER_GUY("The Courier Guy Local", "TCG-ZA-", true),
    MANUAL_DISPATCH("Manual Staff Courier Dispatch", "306-EXT-", false)
}

enum class DietaryTag(val label: String) {
    HALAL("Halal"),
    SPICY("Spicy"),
    EXTRA_SPICY("Extra Spicy"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    SEAFOOD("Seafood"),
    CHEF_SPECIAL("Chef Special"),
    BESTSELLER("Bestseller")
}

enum class ReservationStatus(val label: String) {
    CONFIRMED("Confirmed"),
    PENDING("Awaiting Confirmation"),
    SEATED("Seated"),
    CANCELLED("Cancelled")
}
