package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PayFastConfig
import com.example.data.model.PayFastPaymentRequest
import com.example.data.payment.PayFastGatewayService
import com.example.ui.theme.AfricanBronze
import com.example.ui.theme.ForestHerbs
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TerracottaAccent
import java.util.Locale
import java.util.UUID

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PayFastCheckoutDialog(
    request: PayFastPaymentRequest,
    config: PayFastConfig = PayFastConfig(),
    onPaymentSuccess: (paymentReference: String) -> Unit,
    onPaymentCancelled: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val gatewayService = remember { PayFastGatewayService(config) }
    val params = remember(request) { gatewayService.generatePaymentParameters(request) }
    val autoSubmitHtml = remember(params) { gatewayService.buildPayFastAutoSubmitHtml(params) }

    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showSandboxHelper by remember { mutableStateOf(config.isSandbox) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ObsidianDark
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Gateway Security Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestHerbs.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure Lock",
                                tint = ForestHerbs,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (config.isSandbox) "PayFast Sandbox (ZAR)" else "PayFast Secure Checkout",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (config.isSandbox) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AfricanBronze)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("TEST MODE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "Order #${request.orderId} • Total: R ${String.format(Locale.US, "%.2f", request.amountZar)}",
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { webViewInstance?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color.LightGray)
                        }
                        IconButton(onClick = onPaymentCancelled) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Sandbox Test Helper Banner (Allows instant testing with sandbox cards or simulated server callback)
                if (showSandboxHelper) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianElevated)
                            .border(1.dp, ObsidianBorder)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official PayFast South Africa Test Gateway",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GoldPrimary
                                    )
                                }
                                Text(
                                    text = "Hide",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.clickable { showSandboxHelper = false }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Merchant: 10000100 (306 Restaurant & Lounge ZA) • Supports Visa, Mastercard, Instant EFT & SnapScan.",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val testRef = "PF-SANDBOX-${UUID.randomUUID().toString().take(8).uppercase()}"
                                        onPaymentSuccess(testRef)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Simulate Verified Bank Approval", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = onPaymentCancelled,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Simulate Cancel", fontSize = 11.sp, color = TerracottaAccent)
                                }
                            }
                        }
                    }
                }

                // Loading bar
                if (isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianSurface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Loading PayFast Gateway...", fontSize = 12.sp, color = Color.LightGray)
                    }
                }

                // PayFast Hosted WebView
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        isLoading = true
                                        url?.let { checkPaymentRedirect(it, onPaymentSuccess, onPaymentCancelled) }
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isLoading = false
                                        url?.let { checkPaymentRedirect(it, onPaymentSuccess, onPaymentCancelled) }
                                    }

                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val targetUrl = request?.url?.toString() ?: ""
                                        return checkPaymentRedirect(targetUrl, onPaymentSuccess, onPaymentCancelled)
                                    }
                                }

                                loadDataWithBaseURL(
                                    config.processUrl,
                                    autoSubmitHtml,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                                webViewInstance = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Bottom security badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianSurface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔒 256-Bit SSL Encrypted • Powered by PayFast South Africa (PCI-DSS Level 1)",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

private fun checkPaymentRedirect(
    url: String,
    onSuccess: (paymentReference: String) -> Unit,
    onCancelled: () -> Unit
): Boolean {
    // Check if the URL reached our return/success handler
    if (url.contains("/payment/success") || url.contains("payment-success") || url.contains("restaurant306.co.za/payment/return")) {
        val ref = if (url.contains("pf_payment_id=")) {
            url.substringAfter("pf_payment_id=").substringBefore("&")
        } else {
            "PF-ZA-${UUID.randomUUID().toString().take(8).uppercase()}"
        }
        onSuccess(ref)
        return true
    }

    // Check if the URL reached our cancel handler
    if (url.contains("/payment/cancel") || url.contains("payment-cancel")) {
        onCancelled()
        return true
    }

    return false
}
