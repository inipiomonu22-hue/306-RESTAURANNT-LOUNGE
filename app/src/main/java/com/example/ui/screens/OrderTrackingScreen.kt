package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FulfilmentType
import com.example.data.model.OrderStatus
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
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
fun OrderTrackingScreen(
    viewModel: MainViewModel,
    orderId: String,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val order by viewModel.trackedOrder.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()

    val currentOrder = order ?: activeOrders.firstOrNull { it.orderId == orderId } ?: activeOrders.firstOrNull()

    if (currentOrder == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "No Active Order Found",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Back to Home", color = ObsidianDark, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val fulfilmentType = try {
        FulfilmentType.valueOf(currentOrder.fulfilmentType)
    } catch (e: Exception) {
        FulfilmentType.RESTAURANT_DELIVERY
    }

    val currentStatus = try {
        OrderStatus.valueOf(currentOrder.status)
    } catch (e: Exception) {
        OrderStatus.RECEIVED
    }

    val steps = listOf(
        OrderStatus.RECEIVED to "Received",
        OrderStatus.CONFIRMED to "Confirmed",
        OrderStatus.PREPARING to "Kitchen Prep",
        (if (fulfilmentType == FulfilmentType.COLLECTION_PICKUP) OrderStatus.READY_FOR_PICKUP else OrderStatus.OUT_FOR_DELIVERY) to
                (if (fulfilmentType == FulfilmentType.COLLECTION_PICKUP) "Ready for Pickup" else "Dispatched"),
        OrderStatus.DELIVERED to "Completed"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 800.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 90.dp)
        ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${currentOrder.orderId}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "306 Restaurant & Lounge Fulfilment",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
                StatusBadge(status = currentOrder.status)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ETA Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESTIMATED FULFILMENT TIME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AfricanBronze,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentOrder.estimatedFulfilmentTime.ifEmpty { "25 - 35 mins" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estimate provided by: ${currentOrder.estimateSource}",
                        fontSize = 12.sp,
                        color = ForestHerbs,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Status Timeline
        item {
            Text(
                text = "Order Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val currentStepIndex = steps.indexOfFirst { it.first == currentStatus }.let { if (it < 0) 0 else it }

                steps.forEachIndexed { index, (statusEnum, title) ->
                    val isDone = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDone) GoldPrimary else ObsidianElevated)
                                .border(1.dp, if (isDone) GoldPrimary else ObsidianBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(18.dp))
                            } else {
                                Text("${index + 1}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isDone) Color.White else Color.Gray,
                                fontSize = 14.sp
                            )
                            if (isCurrent) {
                                Text(
                                    text = statusEnum.description,
                                    fontSize = 11.sp,
                                    color = AfricanBronze
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Fulfilment Specific Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (fulfilmentType) {
                        FulfilmentType.RESTAURANT_DELIVERY -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("306 Fleet Delivery Driver", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            if (!currentOrder.assignedDriverName.isNullOrEmpty()) {
                                Text("Assigned Driver: ${currentOrder.assignedDriverName}", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Contact: ${currentOrder.assignedDriverPhone ?: "306 Dispatch Desk"}", color = Color.LightGray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                currentOrder.assignedDriverPhone?.let { phone ->
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:$phone")
                                            }
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianElevated),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Call Driver", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Call Driver ($phone)", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Text("Driver assignment in progress at 306 kitchen.", color = AfricanBronze, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Delivery Address: ${currentOrder.deliveryAddress}", color = Color.White, fontSize = 12.sp)
                            if (currentOrder.deliveryInstructions.isNotEmpty()) {
                                Text("Instructions: ${currentOrder.deliveryInstructions}", color = Color.Gray, fontSize = 11.sp)
                            }
                        }

                        FulfilmentType.THIRD_PARTY_COURIER -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AfricanBronze, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("External Courier Integration", fontWeight = FontWeight.Bold, color = AfricanBronze, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Provider: ${currentOrder.thirdPartyProvider ?: "Uber Direct Cape Town"}", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            currentOrder.thirdPartyTrackingRef?.let { ref ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Tracking Ref: $ref", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Text("Destination: ${currentOrder.deliveryAddress}", color = Color.LightGray, fontSize = 12.sp)
                        }

                        FulfilmentType.COLLECTION_PICKUP -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = ForestHerbs, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Collection at 306 Lounge", fontWeight = FontWeight.Bold, color = ForestHerbs, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Address: 306 Long Street, Cape Town City Centre", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Time Slot: ${currentOrder.collectionTimeSlot.ifEmpty { "Ready in 25 mins" }}", color = AfricanBronze, fontSize = 12.sp)
                            Text("Please quote Order #${currentOrder.orderId} at the front bar.", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // WhatsApp Share & Support Button
        item {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val url = viewModel.repository.generateWhatsAppOrderUrl(currentOrder.orderId, directEnquiryOnly = false)
                        WhatsAppHelper.openWhatsApp(context, url)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Order Receipt on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBackToHome,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Return to Menu / Home", color = GoldPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
}
