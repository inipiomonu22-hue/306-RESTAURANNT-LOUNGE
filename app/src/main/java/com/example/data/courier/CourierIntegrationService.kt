package com.example.data.courier

import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.OrderEntity
import java.util.UUID

data class CourierQuote(
    val provider: CourierProvider,
    val estimatedFeeZar: Double,
    val estimatedPickupMins: Int,
    val estimatedDeliveryMins: Int,
    val isAvailableInZone: Boolean = true,
    val quoteReference: String
)

data class CourierBookingResult(
    val isSuccess: Boolean,
    val trackingReference: String,
    val providerName: String,
    val courierFeeZar: Double,
    val estimatedArrival: String,
    val statusMessage: String
)

class CourierIntegrationService {

    fun getCourierQuote(
        provider: CourierProvider,
        zone: DeliveryZoneEntity?,
        subtotalZar: Double
    ): CourierQuote {
        val baseFee = zone?.feeZar ?: 45.0
        val quoteRef = "QTE-${provider.name.take(3)}-${UUID.randomUUID().toString().take(6).uppercase()}"

        return when (provider) {
            CourierProvider.UBER_DIRECT -> {
                CourierQuote(
                    provider = provider,
                    estimatedFeeZar = baseFee + 10.0,
                    estimatedPickupMins = 12,
                    estimatedDeliveryMins = (zone?.estimatedMins ?: 30),
                    quoteReference = quoteRef
                )
            }
            CourierProvider.MR_D_FOOD -> {
                CourierQuote(
                    provider = provider,
                    estimatedFeeZar = baseFee + 5.0,
                    estimatedPickupMins = 15,
                    estimatedDeliveryMins = (zone?.estimatedMins ?: 35),
                    quoteReference = quoteRef
                )
            }
            CourierProvider.THE_COURIER_GUY -> {
                CourierQuote(
                    provider = provider,
                    estimatedFeeZar = 65.0, // Standard local Cape Town same-day parcel flat rate
                    estimatedPickupMins = 25,
                    estimatedDeliveryMins = (zone?.estimatedMins ?: 45) + 10,
                    quoteReference = quoteRef
                )
            }
            CourierProvider.MANUAL_DISPATCH -> {
                CourierQuote(
                    provider = provider,
                    estimatedFeeZar = baseFee,
                    estimatedPickupMins = 15,
                    estimatedDeliveryMins = zone?.estimatedMins ?: 35,
                    quoteReference = quoteRef
                )
            }
        }
    }

    fun bookCourierDelivery(
        order: OrderEntity,
        provider: CourierProvider,
        customWaybill: String = ""
    ): CourierBookingResult {
        val trackingRef = if (customWaybill.isNotBlank()) {
            customWaybill.trim()
        } else {
            "${provider.trackingPrefix}${UUID.randomUUID().toString().take(6).uppercase()}"
        }

        val estimatedArrival = when (provider) {
            CourierProvider.UBER_DIRECT -> "30 - 45 mins"
            CourierProvider.MR_D_FOOD -> "35 - 50 mins"
            CourierProvider.THE_COURIER_GUY -> "45 - 60 mins"
            CourierProvider.MANUAL_DISPATCH -> "Staff arranging driver"
        }

        return CourierBookingResult(
            isSuccess = true,
            trackingReference = trackingRef,
            providerName = provider.providerName,
            courierFeeZar = order.deliveryFeeZar,
            estimatedArrival = estimatedArrival,
            statusMessage = "Dispatched via ${provider.providerName}. Live waybill assigned."
        )
    }
}
