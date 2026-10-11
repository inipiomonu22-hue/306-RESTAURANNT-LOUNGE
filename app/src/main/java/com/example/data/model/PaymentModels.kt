package com.example.data.model

data class PayFastConfig(
    val merchantId: String = "10000100", // Official PayFast Sandbox Test Merchant ID
    val merchantKey: String = "46f0cd694581a", // Official PayFast Sandbox Test Merchant Key
    val passphrase: String = "", // Optional salt
    val isSandbox: Boolean = true,
    val returnUrl: String = "https://restaurant306.co.za/payment/success",
    val cancelUrl: String = "https://restaurant306.co.za/payment/cancel",
    val notifyUrl: String = "https://restaurant306.co.za/api/payfast-webhook"
) {
    val processUrl: String
        get() = if (isSandbox) "https://sandbox.payfast.co.za/eng/process" else "https://www.payfast.co.za/eng/process"
}

data class PayFastPaymentRequest(
    val orderId: String,
    val amountZar: Double,
    val itemName: String,
    val itemDescription: String,
    val customerFirstName: String,
    val customerLastName: String = "",
    val customerEmail: String,
    val customerPhone: String
)

sealed class PaymentResult {
    data class Success(
        val orderId: String,
        val paymentReference: String,
        val amountZar: Double,
        val gatewayName: String = "PayFast South Africa"
    ) : PaymentResult()

    data class Cancelled(
        val orderId: String,
        val reason: String = "Customer cancelled payment checkout"
    ) : PaymentResult()

    data class Failed(
        val orderId: String,
        val errorMessage: String
    ) : PaymentResult()
}
