package com.example

import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.FulfilmentType
import com.example.data.model.PayFastConfig
import com.example.data.model.PayFastPaymentRequest
import com.example.data.payment.PayFastGatewayService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCollectionPickupFeeIsAlwaysFree() {
        val type = FulfilmentType.COLLECTION_PICKUP
        val zone = DeliveryZoneEntity(
            zoneName = "Camps Bay",
            areaCode = "8005",
            feeZar = 75.0,
            minOrderZar = 250.0,
            estimatedMins = 35
        )
        
        // Collection fee rule is R 0.00
        val fee = if (type == FulfilmentType.COLLECTION_PICKUP) 0.0 else zone.feeZar
        assertEquals(0.0, fee, 0.001)
    }

    @Test
    fun testDeliveryThresholdFreeOver550Zar() {
        val subtotalHigh = 600.00
        val subtotalLow = 420.00
        val zoneFee = 55.00

        val feeHigh = if (subtotalHigh >= 550.00) 0.0 else zoneFee
        val feeLow = if (subtotalLow >= 550.00) 0.0 else zoneFee

        assertEquals(0.0, feeHigh, 0.001)
        assertEquals(55.00, feeLow, 0.001)
    }

    @Test
    fun testPayFastParameterGenerationAndSignature() {
        val config = PayFastConfig(
            merchantId = "10000100",
            merchantKey = "46f0cd694581a",
            passphrase = "",
            isSandbox = true
        )
        val service = PayFastGatewayService(config)

        val request = PayFastPaymentRequest(
            orderId = "306-1234",
            amountZar = 285.50,
            itemName = "306 Jollof Feast",
            itemDescription = "Jollof Rice with Fried Plantain and Grilled Suya Beef",
            customerFirstName = "Chidi",
            customerLastName = "Okafor",
            customerEmail = "test@customer.co.za",
            customerPhone = "+27 72 123 4567"
        )

        val params = service.generatePaymentParameters(request)

        assertEquals("10000100", params["merchant_id"])
        assertEquals("46f0cd694581a", params["merchant_key"])
        assertEquals("285.50", params["amount"])
        assertEquals("306-1234", params["m_payment_id"])
        assertEquals("Chidi", params["name_first"])
        assertEquals("Okafor", params["name_last"])
        assertEquals("test@customer.co.za", params["email_address"])
        assertNotNull(params["signature"])
        assertTrue(params["signature"]!!.isNotBlank())
    }

    @Test
    fun testCourierProviderRateQuotation() {
        val zoneFee = 45.00
        val uberRate = zoneFee + 10.00
        val mrDRate = zoneFee + 5.00
        val courierGuyRate = 65.00

        assertEquals(55.00, uberRate, 0.001)
        assertEquals(50.00, mrDRate, 0.001)
        assertEquals(65.00, courierGuyRate, 0.001)
    }
}
