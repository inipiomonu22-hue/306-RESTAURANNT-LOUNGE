package com.example.data.payment

import com.example.data.model.PayFastConfig
import com.example.data.model.PayFastPaymentRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Locale

class PayFastGatewayService(private val config: PayFastConfig = PayFastConfig()) {

    fun generatePaymentParameters(request: PayFastPaymentRequest): Map<String, String> {
        val params = linkedMapOf<String, String>()

        // Merchant Details
        params["merchant_id"] = config.merchantId.trim()
        params["merchant_key"] = config.merchantKey.trim()
        params["return_url"] = config.returnUrl
        params["cancel_url"] = config.cancelUrl
        params["notify_url"] = config.notifyUrl

        // Buyer Details
        params["name_first"] = request.customerFirstName.trim()
        if (request.customerLastName.isNotBlank()) {
            params["name_last"] = request.customerLastName.trim()
        }
        if (request.customerEmail.isNotBlank()) {
            params["email_address"] = request.customerEmail.trim()
        }
        if (request.customerPhone.isNotBlank()) {
            params["cell_number"] = request.customerPhone.trim()
        }

        // Transaction Details
        params["m_payment_id"] = request.orderId.trim()
        params["amount"] = String.format(Locale.US, "%.2f", request.amountZar)
        params["item_name"] = request.itemName.take(100)
        if (request.itemDescription.isNotBlank()) {
            params["item_description"] = request.itemDescription.take(255)
        }

        // Compute PayFast MD5 signature
        val signature = generateSignature(params, config.passphrase)
        params["signature"] = signature

        return params
    }

    fun generateSignature(params: Map<String, String>, passphrase: String = ""): String {
        val sb = StringBuilder()
        for ((key, value) in params) {
            if (value.isNotBlank() && key != "signature") {
                if (sb.isNotEmpty()) sb.append("&")
                sb.append(key)
                sb.append("=")
                sb.append(URLEncoder.encode(value.trim(), StandardCharsets.UTF_8.name()).replace("+", "%20"))
            }
        }
        if (passphrase.isNotBlank()) {
            sb.append("&passphrase=")
            sb.append(URLEncoder.encode(passphrase.trim(), StandardCharsets.UTF_8.name()).replace("+", "%20"))
        }

        return md5(sb.toString())
    }

    private fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(StandardCharsets.UTF_8))
        val hex = StringBuilder()
        for (b in bytes) {
            val hexPart = Integer.toHexString(0xFF and b.toInt())
            if (hexPart.length == 1) hex.append('0')
            hex.append(hexPart)
        }
        return hex.toString()
    }

    fun buildPayFastAutoSubmitHtml(params: Map<String, String>): String {
        val formFields = StringBuilder()
        for ((k, v) in params) {
            formFields.append("<input type=\"hidden\" name=\"$k\" value=\"${v.replace("\"", "&quot;")}\" />\n")
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                        background: #140E0A;
                        color: #FFFFFF;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        height: 100vh;
                        margin: 0;
                        padding: 20px;
                        box-sizing: border-box;
                    }
                    .loader {
                        border: 4px solid #281D15;
                        border-top: 4px solid #D4AF37;
                        border-radius: 50%;
                        width: 48px;
                        height: 48px;
                        animation: spin 1s linear infinite;
                        margin-bottom: 20px;
                    }
                    @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
                    h2 { color: #D4AF37; margin: 0 0 10px 0; }
                    p { color: #D4C4B5; font-size: 14px; text-align: center; }
                    .badge {
                        background: #281D15;
                        border: 1px solid #3D2E23;
                        border-radius: 6px;
                        padding: 6px 12px;
                        font-size: 12px;
                        color: #C68A4C;
                        margin-top: 15px;
                    }
                </style>
            </head>
            <body>
                <div class="loader"></div>
                <h2>Securing Payment Session...</h2>
                <p>Connecting to PayFast South Africa Secure Checkout</p>
                <div class="badge">ZAR Gateway • Visa, Mastercard, Instant EFT, SnapScan</div>
                <form id="payfast_form" action="${config.processUrl}" method="post">
                    $formFields
                    <noscript>
                        <input type="submit" value="Click here if not redirected automatically" style="margin-top:20px;padding:10px 20px;background:#D4AF37;border:none;border-radius:6px;font-weight:bold;">
                    </noscript>
                </form>
                <script>
                    setTimeout(function() {
                        document.getElementById('payfast_form').submit();
                    }, 600);
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}
