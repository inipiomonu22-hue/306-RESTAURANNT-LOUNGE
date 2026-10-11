package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.data.model.PayFastPaymentRequest
import com.example.ui.components.PayFastCheckoutDialog
import java.util.UUID
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.FulfilmentType
import com.example.data.model.PaymentMethod
import com.example.ui.MainViewModel
import com.example.ui.components.WhatsAppHelper
import com.example.ui.theme.AfricanBronze
import com.example.ui.theme.ForestHerbs
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TerracottaAccent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CartCheckoutScreen(
    viewModel: MainViewModel,
    onOrderPlaced: (orderId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val deliveryZones by viewModel.deliveryZones.collectAsState()
    val selectedFulfilmentType by viewModel.selectedFulfilmentType.collectAsState()
    val selectedZone by viewModel.selectedZone.collectAsState()
    val selectedCourier by viewModel.selectedCourier.collectAsState()
    val deliveryFee by viewModel.calculatedDeliveryFee.collectAsState()
    val totalAmount by viewModel.orderTotalZar.collectAsState()

    // Form inputs
    var customerName by remember { mutableStateOf("Chidi Okafor") }
    var customerPhone by remember { mutableStateOf("+27 72 123 4567") }
    var customerEmail by remember { mutableStateOf("chidi@example.co.za") }
    var deliveryAddress by remember { mutableStateOf("45 Kloof Street, Gardens, Cape Town") }
    var deliveryInstructions by remember { mutableStateOf("Ring buzzer 4B, please deliver upstairs") }
    var selectedCollectionSlot by remember { mutableStateOf("Today: 20:00 - 20:30") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.PAYFAST) }
    var specialNotes by remember { mutableStateOf("") }
    var showPayFastDialog by remember { mutableStateOf(false) }
    var feeApprovedByUser by remember { mutableStateOf(true) }
    var feeApprovalError by remember { mutableStateOf<String?>(null) }

    val collectionSlots = listOf(
        "ASAP (~20 - 30 mins)",
        "Today: 19:30 - 20:00",
        "Today: 20:00 - 20:30",
        "Today: 20:30 - 21:00",
        "Today: 21:00 - 21:30"
    )

    val minOrderRequired = if (selectedFulfilmentType == FulfilmentType.RESTAURANT_DELIVERY) selectedZone?.minOrderZar ?: 0.0 else 0.0
    val isMinOrderMet = subtotal >= minOrderRequired

    val handleConfirmOrder: () -> Unit = {
        if (!isMinOrderMet) {
            feeApprovalError = "Minimum food subtotal for ${selectedZone?.zoneName ?: "delivery"} is R ${minOrderRequired.toInt()}. Please add R ${String.format(Locale.US, "%.2f", minOrderRequired - subtotal)} more or switch to Collection."
        } else if (!feeApprovedByUser) {
            feeApprovalError = "Please check the box below to approve the R ${String.format(Locale.US, "%.2f", deliveryFee)} fulfilment fee before payment."
        } else {
            feeApprovalError = null
            if (selectedPaymentMethod == PaymentMethod.PAYFAST) {
                showPayFastDialog = true
            } else {
                viewModel.placeOrder(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerEmail = customerEmail,
                    deliveryAddress = deliveryAddress,
                    deliveryInstructions = deliveryInstructions,
                    collectionTimeSlot = selectedCollectionSlot,
                    paymentMethod = selectedPaymentMethod,
                    specialNotes = specialNotes,
                    customPaymentRef = if (selectedPaymentMethod != PaymentMethod.CASH_ON_DELIVERY) "GATEWAY-${UUID.randomUUID().toString().take(8).uppercase()}" else null,
                    isAlreadyPaid = selectedPaymentMethod != PaymentMethod.CASH_ON_DELIVERY,
                    onSuccess = { order ->
                        onOrderPlaced(order.orderId)
                    }
                )
            }
        }
    }

    if (showPayFastDialog) {
        PayFastCheckoutDialog(
            request = PayFastPaymentRequest(
                orderId = "PRE-${System.currentTimeMillis().toString().takeLast(6)}",
                amountZar = totalAmount,
                itemName = "306 Lounge Order (${cartItems.size} items)",
                itemDescription = "Nigerian Feast: ${selectedFulfilmentType.title}",
                customerEmail = customerEmail.ifBlank { "customer@306lounge.co.za" },
                customerFirstName = customerName.split(" ").firstOrNull() ?: "Guest",
                customerLastName = customerName.split(" ").drop(1).joinToString(" ").ifBlank { "Customer" },
                customerPhone = customerPhone
            ),
            onPaymentSuccess = { paymentReference ->
                showPayFastDialog = false
                viewModel.placeOrder(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerEmail = customerEmail,
                    deliveryAddress = deliveryAddress,
                    deliveryInstructions = deliveryInstructions,
                    collectionTimeSlot = selectedCollectionSlot,
                    paymentMethod = PaymentMethod.PAYFAST,
                    specialNotes = specialNotes,
                    customPaymentRef = paymentReference,
                    isAlreadyPaid = true,
                    onSuccess = { order ->
                        onOrderPlaced(order.orderId)
                    }
                )
            },
            onPaymentCancelled = {
                showPayFastDialog = false
            },
            onDismissRequest = {
                showPayFastDialog = false
            }
        )
    }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(
                    imageVector = Icons.Default.DeliveryDining,
                    contentDescription = "Empty Cart",
                    tint = GoldPrimary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Order is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Browse our menu to select authentic Nigerian dishes.",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(ObsidianDark)) {
        val isWide = maxWidth >= 650.dp

        if (isWide) {
            // WIDE SCREEN / TABLET DUAL-PANE VIEW
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Fulfilment Option & Contact Details
                LazyColumn(
                    modifier = Modifier.weight(1.1f),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "Checkout & Fulfilment",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldPrimary
                        )
                        Text(
                            text = "306 Restaurant & Lounge • Cape Town, South Africa",
                            fontSize = 12.sp,
                            color = AfricanBronze
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fulfilment Options
                        Text(
                            text = "1. Choose Fulfilment Option",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FulfilmentType.values().forEach { type ->
                                val isSelected = selectedFulfilmentType == type
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectedFulfilmentType.value = type },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) ObsidianElevated else ObsidianSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) GoldPrimary else ObsidianBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (type) {
                                                FulfilmentType.RESTAURANT_DELIVERY -> Icons.Default.DeliveryDining
                                                FulfilmentType.THIRD_PARTY_COURIER -> Icons.Default.LocalShipping
                                                FulfilmentType.COLLECTION_PICKUP -> Icons.Default.Storefront
                                            },
                                            contentDescription = type.title,
                                            tint = if (isSelected) GoldPrimary else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = type.title,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) GoldPrimary else Color.White,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = type.subtitle,
                                                fontSize = 11.sp,
                                                color = Color.LightGray
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fulfilment details card
                        FulfilmentDetailsCard(
                            selectedFulfilmentType = selectedFulfilmentType,
                            selectedZone = selectedZone,
                            deliveryZones = deliveryZones,
                            onSelectZone = { viewModel.selectedZone.value = it },
                            selectedCourier = selectedCourier,
                            onSelectCourier = { viewModel.selectedCourier.value = it },
                            collectionSlots = collectionSlots,
                            selectedCollectionSlot = selectedCollectionSlot,
                            onSelectCollectionSlot = { selectedCollectionSlot = it },
                            deliveryAddress = deliveryAddress,
                            onAddressChange = { deliveryAddress = it },
                            deliveryInstructions = deliveryInstructions,
                            onInstructionsChange = { deliveryInstructions = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Customer Details
                        Text(
                            text = "2. Contact Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Right Column: Order Items, Payments & Checkout Action
                LazyColumn(
                    modifier = Modifier.weight(0.9f),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "3. Your Order (${cartItems.size} items)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.menuItem.name, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                    if (item.selectedProtein.isNotEmpty()) {
                                        Text("Protein: ${item.selectedProtein}", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                    if (item.selectedSwallow.isNotEmpty()) {
                                        Text("Swallow: ${item.selectedSwallow}", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                    Text("Spice: ${item.selectedSpiceLevel}", fontSize = 11.sp, color = TerracottaAccent)
                                    Text(
                                        "R ${String.format(Locale.US, "%.2f", item.unitPriceZar)} each",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateCartItemQuantity(item.cartItemId, item.quantity - 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.LightGray)
                                    }
                                    Text("${item.quantity}", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp))
                                    IconButton(
                                        onClick = { viewModel.updateCartItemQuantity(item.cartItemId, item.quantity + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = GoldPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "R ${String.format(Locale.US, "%.2f", item.totalZar)}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Payment selection & summary
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "4. Payment Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                PaymentMethod.PAYFAST to "PayFast (Cards, Capitec Pay & Instant EFT)",
                                PaymentMethod.YOCO to "Yoco Hosted Card Gateway",
                                PaymentMethod.SNAPSCAN_ZAPPER to "SnapScan / Zapper Mobile QR",
                                PaymentMethod.CASH_ON_DELIVERY to if (selectedFulfilmentType == FulfilmentType.COLLECTION_PICKUP) "Pay at Restaurant on Collection" else "Cash on Delivery"
                            ).forEach { (method, label) ->
                                val isPaySelected = selectedPaymentMethod == method
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPaymentMethod = method },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isPaySelected) ObsidianElevated else ObsidianSurface
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isPaySelected) GoldPrimary else ObsidianBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CreditCard,
                                            contentDescription = null,
                                            tint = if (isPaySelected) GoldPrimary else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            color = if (isPaySelected) GoldPrimary else Color.White,
                                            fontWeight = if (isPaySelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isPaySelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Payment Summary (ZAR)", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Food Subtotal", color = Color.LightGray, fontSize = 13.sp)
                                    Text("R ${String.format(Locale.US, "%.2f", subtotal)}", color = Color.White, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = when (selectedFulfilmentType) {
                                            FulfilmentType.COLLECTION_PICKUP -> "Collection Fee"
                                            FulfilmentType.RESTAURANT_DELIVERY -> "306 Fleet Delivery Fee"
                                            FulfilmentType.THIRD_PARTY_COURIER -> "${selectedCourier.providerName} Fee"
                                        },
                                        color = Color.LightGray,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (deliveryFee == 0.0) "FREE" else "R ${String.format(Locale.US, "%.2f", deliveryFee)}",
                                        color = if (deliveryFee == 0.0) ForestHerbs else Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ObsidianBorder))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Total Amount", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                                    Text(
                                        text = "R ${String.format(Locale.US, "%.2f", totalAmount)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPrimary,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        // Minimum Order Warning
                        if (!isMinOrderMet) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = TerracottaAccent.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = TerracottaAccent, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Minimum food subtotal for ${selectedZone?.zoneName ?: "delivery"} is R ${minOrderRequired.toInt()}. Add R ${String.format(Locale.US, "%.2f", minOrderRequired - subtotal)} more to qualify for delivery, or switch to Collection.",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Mandatory Delivery Fee Approval Checkbox
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianElevated)
                                .clickable { feeApprovedByUser = !feeApprovedByUser }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = feeApprovedByUser,
                                onCheckedChange = { feeApprovedByUser = it },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = ObsidianDark)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "I approve the ${if (deliveryFee == 0.0) "FREE" else "R ${String.format(Locale.US, "%.2f", deliveryFee)}"} fee for ${selectedFulfilmentType.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (feeApprovedByUser) Color.White else AfricanBronze
                                )
                                Text(
                                    text = if (deliveryFee == 0.0) "Complimentary collection / free delivery applied" else "Estimated & verified for ${selectedZone?.zoneName ?: selectedCourier.providerName}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        feeApprovalError?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = err, color = TerracottaAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = handleConfirmOrder,
                            colors = ButtonDefaults.buttonColors(containerColor = if (isMinOrderMet && feeApprovedByUser) GoldPrimary else AfricanBronze),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(
                                text = "Confirm Order & Pay (R ${String.format(Locale.US, "%.2f", totalAmount)})",
                                color = ObsidianDark,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.placeOrder(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    customerEmail = customerEmail,
                                    deliveryAddress = deliveryAddress,
                                    deliveryInstructions = deliveryInstructions,
                                    collectionTimeSlot = selectedCollectionSlot,
                                    paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                                    specialNotes = specialNotes,
                                    onSuccess = { order ->
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val url = viewModel.repository.generateWhatsAppOrderUrl(order.orderId, directEnquiryOnly = true)
                                            WhatsAppHelper.openWhatsApp(context, url)
                                            onOrderPlaced(order.orderId)
                                        }
                                    }
                                )
                            },
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestHerbs),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "WhatsApp", tint = ForestHerbs, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Order Directly via WhatsApp",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            // COMPACT SINGLE-COLUMN VIEW FOR PHONES
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "Checkout & Fulfilment",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "306 Restaurant & Lounge • Cape Town, South Africa",
                        fontSize = 12.sp,
                        color = AfricanBronze
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "1. Choose Fulfilment Option",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FulfilmentType.values().forEach { type ->
                            val isSelected = selectedFulfilmentType == type
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectedFulfilmentType.value = type },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ObsidianElevated else ObsidianSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else ObsidianBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (type) {
                                            FulfilmentType.RESTAURANT_DELIVERY -> Icons.Default.DeliveryDining
                                            FulfilmentType.THIRD_PARTY_COURIER -> Icons.Default.LocalShipping
                                            FulfilmentType.COLLECTION_PICKUP -> Icons.Default.Storefront
                                        },
                                        contentDescription = type.title,
                                        tint = if (isSelected) GoldPrimary else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = type.title,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) GoldPrimary else Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = type.subtitle,
                                            fontSize = 11.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    FulfilmentDetailsCard(
                        selectedFulfilmentType = selectedFulfilmentType,
                        selectedZone = selectedZone,
                        deliveryZones = deliveryZones,
                        onSelectZone = { viewModel.selectedZone.value = it },
                        selectedCourier = selectedCourier,
                        onSelectCourier = { viewModel.selectedCourier.value = it },
                        collectionSlots = collectionSlots,
                        selectedCollectionSlot = selectedCollectionSlot,
                        onSelectCollectionSlot = { selectedCollectionSlot = it },
                        deliveryAddress = deliveryAddress,
                        onAddressChange = { deliveryAddress = it },
                        deliveryInstructions = deliveryInstructions,
                        onInstructionsChange = { deliveryInstructions = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Contact Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "3. Your Order (${cartItems.size} items)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(cartItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.menuItem.name, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                if (item.selectedProtein.isNotEmpty()) {
                                    Text("Protein: ${item.selectedProtein}", fontSize = 11.sp, color = Color.LightGray)
                                }
                                if (item.selectedSwallow.isNotEmpty()) {
                                    Text("Swallow: ${item.selectedSwallow}", fontSize = 11.sp, color = Color.LightGray)
                                }
                                Text("Spice: ${item.selectedSpiceLevel}", fontSize = 11.sp, color = TerracottaAccent)
                                Text(
                                    "R ${String.format(Locale.US, "%.2f", item.unitPriceZar)} each",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartItemQuantity(item.cartItemId, item.quantity - 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.LightGray)
                                }
                                Text("${item.quantity}", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp))
                                IconButton(
                                    onClick = { viewModel.updateCartItemQuantity(item.cartItemId, item.quantity + 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = GoldPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "R ${String.format(Locale.US, "%.2f", item.totalZar)}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "4. Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            PaymentMethod.PAYFAST to "PayFast (Cards, Capitec Pay & Instant EFT)",
                            PaymentMethod.YOCO to "Yoco Hosted Card Gateway",
                            PaymentMethod.SNAPSCAN_ZAPPER to "SnapScan / Zapper Mobile QR",
                            PaymentMethod.CASH_ON_DELIVERY to if (selectedFulfilmentType == FulfilmentType.COLLECTION_PICKUP) "Pay at Restaurant on Collection" else "Cash on Delivery"
                        ).forEach { (method, label) ->
                            val isPaySelected = selectedPaymentMethod == method
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaymentMethod = method },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPaySelected) ObsidianElevated else ObsidianSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isPaySelected) GoldPrimary else ObsidianBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = if (isPaySelected) GoldPrimary else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        color = if (isPaySelected) GoldPrimary else Color.White,
                                        fontWeight = if (isPaySelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isPaySelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Payment Summary (ZAR)", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Food Subtotal", color = Color.LightGray, fontSize = 13.sp)
                                Text("R ${String.format(Locale.US, "%.2f", subtotal)}", color = Color.White, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = when (selectedFulfilmentType) {
                                        FulfilmentType.COLLECTION_PICKUP -> "Collection Fee"
                                        FulfilmentType.RESTAURANT_DELIVERY -> "306 Fleet Delivery Fee"
                                        FulfilmentType.THIRD_PARTY_COURIER -> "${selectedCourier.providerName} Fee"
                                    },
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (deliveryFee == 0.0) "FREE" else "R ${String.format(Locale.US, "%.2f", deliveryFee)}",
                                    color = if (deliveryFee == 0.0) ForestHerbs else Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ObsidianBorder))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Amount", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                                Text(
                                    text = "R ${String.format(Locale.US, "%.2f", totalAmount)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    // Minimum Order Warning
                    if (!isMinOrderMet) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = TerracottaAccent.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = TerracottaAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Minimum food subtotal for ${selectedZone?.zoneName ?: "delivery"} is R ${minOrderRequired.toInt()}. Add R ${String.format(Locale.US, "%.2f", minOrderRequired - subtotal)} more to qualify for delivery, or switch to Collection.",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Mandatory Delivery Fee Approval Checkbox
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianElevated)
                            .clickable { feeApprovedByUser = !feeApprovedByUser }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = feeApprovedByUser,
                            onCheckedChange = { feeApprovedByUser = it },
                            colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = ObsidianDark)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "I approve the ${if (deliveryFee == 0.0) "FREE" else "R ${String.format(Locale.US, "%.2f", deliveryFee)}"} fee for ${selectedFulfilmentType.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (feeApprovedByUser) Color.White else AfricanBronze
                            )
                            Text(
                                text = if (deliveryFee == 0.0) "Complimentary collection / free delivery applied" else "Estimated & verified for ${selectedZone?.zoneName ?: selectedCourier.providerName}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    feeApprovalError?.let { err ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = err, color = TerracottaAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = handleConfirmOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isMinOrderMet && feeApprovedByUser) GoldPrimary else AfricanBronze),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Confirm Order & Pay (R ${String.format(Locale.US, "%.2f", totalAmount)})",
                            color = ObsidianDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.placeOrder(
                                customerName = customerName,
                                customerPhone = customerPhone,
                                customerEmail = customerEmail,
                                deliveryAddress = deliveryAddress,
                                deliveryInstructions = deliveryInstructions,
                                collectionTimeSlot = selectedCollectionSlot,
                                paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                                specialNotes = specialNotes,
                                onSuccess = { order ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val url = viewModel.repository.generateWhatsAppOrderUrl(order.orderId, directEnquiryOnly = true)
                                        WhatsAppHelper.openWhatsApp(context, url)
                                        onOrderPlaced(order.orderId)
                                    }
                                }
                            )
                        },
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestHerbs),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "WhatsApp", tint = ForestHerbs, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Order Directly via WhatsApp",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FulfilmentDetailsCard(
    selectedFulfilmentType: FulfilmentType,
    selectedZone: DeliveryZoneEntity?,
    deliveryZones: List<DeliveryZoneEntity>,
    onSelectZone: (DeliveryZoneEntity) -> Unit,
    selectedCourier: CourierProvider,
    onSelectCourier: (CourierProvider) -> Unit,
    collectionSlots: List<String>,
    selectedCollectionSlot: String,
    onSelectCollectionSlot: (String) -> Unit,
    deliveryAddress: String,
    onAddressChange: (String) -> Unit,
    deliveryInstructions: String,
    onInstructionsChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (selectedFulfilmentType) {
                FulfilmentType.COLLECTION_PICKUP -> {
                    Text(
                        text = "Collection / Pickup Details",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = AfricanBronze, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "306 Long Street, Cape Town City Centre, 8001",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Free of delivery charge (R 0.00)\n• Estimated readiness: 20-30 mins\n• Present your order ID at the front lounge desk.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Collection Time Slot", fontWeight = FontWeight.SemiBold, color = GoldPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    collectionSlots.forEach { slot ->
                        val isSlotSelected = selectedCollectionSlot == slot
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCollectionSlot(slot) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSlotSelected) Icons.Default.CheckCircle else Icons.Default.Storefront,
                                contentDescription = null,
                                tint = if (isSlotSelected) GoldPrimary else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = slot,
                                color = if (isSlotSelected) Color.White else Color.LightGray,
                                fontSize = 13.sp,
                                fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                FulfilmentType.RESTAURANT_DELIVERY -> {
                    Text(
                        text = "Restaurant Driver Delivery",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Delivered directly by 306 Restaurant's dedicated driver fleet with insulated thermal food containers.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Select Cape Town Delivery Zone", fontWeight = FontWeight.SemiBold, color = GoldPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    deliveryZones.forEach { zone ->
                        val isZoneSelected = selectedZone?.zoneName == zone.zoneName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isZoneSelected) ObsidianElevated else Color.Transparent)
                                .clickable { onSelectZone(zone) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = zone.zoneName,
                                    fontWeight = if (isZoneSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isZoneSelected) GoldPrimary else Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "ETA: ~${zone.estimatedMins} mins • Min Order: R ${zone.minOrderZar.toInt()}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "R ${String.format(Locale.US, "%.2f", zone.feeZar)}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = onAddressChange,
                        label = { Text("Cape Town Street Address & Suburb") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deliveryInstructions,
                        onValueChange = onInstructionsChange,
                        label = { Text("Gate code / Delivery instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                FulfilmentType.THIRD_PARTY_COURIER -> {
                    Text(
                        text = "Third-Party Courier Integration",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Dispatched via verified external courier provider serving Cape Town. Real quotation & tracking reference generated.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Choose Integrated Courier Partner", fontWeight = FontWeight.SemiBold, color = GoldPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    CourierProvider.values().forEach { provider ->
                        val isProviderSelected = selectedCourier == provider
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCourier(provider) }
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isProviderSelected) ObsidianElevated else ObsidianDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isProviderSelected) GoldPrimary else ObsidianBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = provider.providerName,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isProviderSelected) GoldPrimary else Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (provider.apiIntegrated) "API Quotation & Tracking Enabled" else "Manual Staff Dispatch & Ref Booking",
                                        fontSize = 11.sp,
                                        color = if (provider.apiIntegrated) ForestHerbs else AfricanBronze
                                    )
                                }
                                Text(
                                    text = when (provider) {
                                        CourierProvider.UBER_DIRECT -> "R 55.00"
                                        CourierProvider.MR_D_FOOD -> "R 50.00"
                                        CourierProvider.THE_COURIER_GUY -> "R 65.00"
                                        CourierProvider.MANUAL_DISPATCH -> "R 50.00"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = onAddressChange,
                        label = { Text("Delivery Address in Cape Town") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
