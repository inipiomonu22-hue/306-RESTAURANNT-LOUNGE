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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.components.WhatsAppHelper
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CourierProvider
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.DriverEntity
import com.example.data.model.FulfilmentType
import com.example.data.model.OrderStatus
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AfricanBronze
import com.example.ui.theme.ForestHerbs
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TerracottaAccent
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Live Orders", "Driver Fleet", "Courier & Zones", "Menu & Kitchen", "Reports")

    val activeOrders by viewModel.activeOrders.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allDrivers by viewModel.allDrivers.collectAsState()
    val availableDrivers by viewModel.availableDrivers.collectAsState()
    val deliveryZones by viewModel.allDeliveryZones.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val settings by viewModel.restaurantSettings.collectAsState()

    // Dialog state for driver assignment
    var assigningDriverForOrderId by remember { mutableStateOf<String?>(null) }
    var dispatchingCourierForOrderId by remember { mutableStateOf<String?>(null) }
    var selectedCourierToDispatch by remember { mutableStateOf(CourierProvider.UBER_DIRECT) }
    var customCourierRef by remember { mutableStateOf("") }

    // Dialog state for adding a driver
    var showAddDriverDialog by remember { mutableStateOf(false) }
    var newDriverName by remember { mutableStateOf("") }
    var newDriverPhone by remember { mutableStateOf("") }
    var newDriverVehicle by remember { mutableStateOf("Motorbike (Insulated Box)") }
    var newDriverPlate by remember { mutableStateOf("CA 306-WP") }

    // Driver Assignment Dialog
    assigningDriverForOrderId?.let { orderId ->
        Dialog(onDismissRequest = { assigningDriverForOrderId = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Assign 306 Driver to Order #$orderId", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Available Restaurant Driver:", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    allDrivers.forEach { driver ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.assignDriverToOrder(orderId, driver.driverId)
                                    assigningDriverForOrderId = null
                                }
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(driver.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("${driver.vehicleType} • ${driver.phone}", fontSize = 11.sp, color = AfricanBronze)
                                }
                                Text("Assign", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { assigningDriverForOrderId = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }

    // Third-party courier dispatch dialog
    dispatchingCourierForOrderId?.let { orderId ->
        Dialog(onDismissRequest = { dispatchingCourierForOrderId = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AfricanBronze)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Dispatch External Courier (#$orderId)", fontWeight = FontWeight.Bold, color = AfricanBronze, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Courier Provider:", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))

                    CourierProvider.values().forEach { provider ->
                        val isSelected = selectedCourierToDispatch == provider
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCourierToDispatch = provider }
                                .padding(vertical = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) ObsidianElevated else ObsidianDark
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AfricanBronze else ObsidianBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(provider.providerName, color = if (isSelected) AfricanBronze else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = AfricanBronze, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customCourierRef,
                        onValueChange = { customCourierRef = it },
                        label = { Text("Waybill / Tracking Ref (Optional)") },
                        placeholder = { Text("Auto-generates if empty") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AfricanBronze,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.dispatchThirdPartyCourier(orderId, selectedCourierToDispatch, customCourierRef)
                            dispatchingCourierForOrderId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AfricanBronze),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dispatch Courier", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Add Driver Dialog
    if (showAddDriverDialog) {
        Dialog(onDismissRequest = { showAddDriverDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Register New Driver to 306 Fleet", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newDriverName,
                        onValueChange = { newDriverName = it },
                        label = { Text("Driver Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDriverPhone,
                        onValueChange = { newDriverPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDriverVehicle,
                        onValueChange = { newDriverVehicle = it },
                        label = { Text("Vehicle Type") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDriverPlate,
                        onValueChange = { newDriverPlate = it },
                        label = { Text("License Plate") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (newDriverName.isNotBlank() && newDriverPhone.isNotBlank()) {
                                viewModel.addDriver(newDriverName, newDriverPhone, newDriverVehicle, newDriverPlate)
                                showAddDriverDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Driver to Roster", color = ObsidianDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        val isWide = maxWidth >= 650.dp
        val columnsCount = if (isWide) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = if (isWide) 24.dp else 16.dp, end = if (isWide) 24.dp else 16.dp, top = 16.dp, bottom = 90.dp)
        ) {
            // Top Staff Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "306 Restaurant Admin",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldPrimary
                        )
                        Text(
                            text = "Operations, Fleet & Courier Control • Cape Town",
                            fontSize = 12.sp,
                            color = AfricanBronze
                        )
                    }

                    // Kitchen status toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (settings?.isOpenForOrders != false) "Open" else "Closed",
                            fontSize = 11.sp,
                            color = if (settings?.isOpenForOrders != false) ForestHerbs else TerracottaAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = settings?.isOpenForOrders != false,
                            onCheckedChange = { viewModel.updateKitchenStatus(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ForestHerbs,
                                checkedTrackColor = ForestHerbs.copy(alpha = 0.3f),
                                uncheckedThumbColor = TerracottaAccent,
                                uncheckedTrackColor = TerracottaAccent.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Navigation Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ObsidianSurface,
                    contentColor = GoldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) GoldPrimary else Color.LightGray
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // TAB 0: LIVE ORDERS MANAGEMENT
            if (selectedTab == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Outstanding Orders (${activeOrders.size})", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text("● Live Updates", fontSize = 12.sp, color = ForestHerbs, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (activeOrders.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No pending active orders right now", color = Color.LightGray, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(activeOrders.chunked(columnsCount)) { rowOrders ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (order in rowOrders) {
                                val fType = try { FulfilmentType.valueOf(order.fulfilmentType) } catch (e: Exception) { FulfilmentType.RESTAURANT_DELIVERY }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Order #${order.orderId}", fontWeight = FontWeight.ExtraBold, color = GoldPrimary, fontSize = 15.sp)
                                                Text(
                                                    text = "${order.customerName} • ${order.customerPhone}",
                                                    fontSize = 12.sp,
                                                    color = Color.LightGray
                                                )
                                            }
                                            StatusBadge(status = order.status)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(
                                                onClick = {
                                                    val msg = if (fType == FulfilmentType.COLLECTION_PICKUP) {
                                                        "Hi ${order.customerName}, your order #${order.orderId} from 306 Restaurant & Lounge is READY FOR COLLECTION at 306 Long Street, Cape Town! Please present this message at the front desk."
                                                    } else {
                                                        "Hi ${order.customerName}, your order #${order.orderId} from 306 Restaurant & Lounge is OUT FOR DELIVERY to ${order.deliveryAddress}. Estimated arrival: ${order.estimatedFulfilmentTime}."
                                                    }
                                                    val cleanPhone = order.customerPhone.replace(Regex("[^0-9]"), "")
                                                    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${android.net.Uri.encode(msg)}"
                                                    WhatsAppHelper.openWhatsApp(context, url)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Call,
                                                    contentDescription = "Notify via WhatsApp",
                                                    tint = ForestHerbs,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Fulfilment: ${fType.title}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AfricanBronze
                                        )
                                        Text(
                                            text = "Address/Slot: ${if (fType == FulfilmentType.COLLECTION_PICKUP) order.collectionTimeSlot else order.deliveryAddress}",
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )

                                        if (!order.assignedDriverName.isNullOrEmpty()) {
                                            Text("Assigned Driver: ${order.assignedDriverName} (${order.assignedDriverPhone})", fontSize = 11.sp, color = ForestHerbs)
                                        }

                                        if (!order.thirdPartyTrackingRef.isNullOrEmpty()) {
                                            Text("Courier: ${order.thirdPartyProvider} • Ref: ${order.thirdPartyTrackingRef}", fontSize = 11.sp, color = GoldPrimary)
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Total: R ${String.format(Locale.US, "%.2f", order.totalZar)} • ${order.paymentStatus} (${order.paymentMethod})", fontSize = 12.sp, color = Color.Gray)

                                        // Action Buttons
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (order.status == OrderStatus.RECEIVED.name || order.status == OrderStatus.CONFIRMED.name) {
                                                Button(
                                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.PREPARING) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Prep", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }

                                            if (fType == FulfilmentType.COLLECTION_PICKUP && order.status != OrderStatus.READY_FOR_PICKUP.name && order.status != OrderStatus.DELIVERED.name) {
                                                Button(
                                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.READY_FOR_PICKUP) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Ready", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }

                                            if (fType == FulfilmentType.RESTAURANT_DELIVERY) {
                                                OutlinedButton(
                                                    onClick = { assigningDriverForOrderId = order.orderId },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(if (order.assignedDriverName.isNullOrEmpty()) "Assign Driver" else "Reassign", color = GoldPrimary, fontSize = 10.sp)
                                                }
                                            }

                                            if (fType == FulfilmentType.THIRD_PARTY_COURIER) {
                                                OutlinedButton(
                                                    onClick = { dispatchingCourierForOrderId = order.orderId },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AfricanBronze, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Dispatch Courier", color = AfricanBronze, fontSize = 10.sp)
                                                }
                                            }

                                            if (order.status != OrderStatus.DELIVERED.name) {
                                                Button(
                                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.DELIVERED) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (rowOrders.size < columnsCount) {
                                for (i in 0 until (columnsCount - rowOrders.size)) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: DRIVER FLEET MANAGEMENT
            if (selectedTab == 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("306 Restaurant Drivers (${allDrivers.size})", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Button(
                            onClick = { showAddDriverDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Driver", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(allDrivers.chunked(columnsCount)) { rowDrivers ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (driver in rowDrivers) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(driver.name, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (driver.isAvailable) ForestHerbs.copy(alpha = 0.2f) else TerracottaAccent.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (driver.isAvailable) "ON DUTY" else "OFF DUTY",
                                                    color = if (driver.isAvailable) ForestHerbs else TerracottaAccent,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Phone: ${driver.phone}", fontSize = 12.sp, color = Color.White)
                                        Text("Vehicle: ${driver.vehicleType} • Plate: ${driver.licensePlate}", fontSize = 11.sp, color = AfricanBronze)
                                    }

                                    Switch(
                                        checked = driver.isAvailable,
                                        onCheckedChange = { viewModel.toggleDriverAvailability(driver.driverId, it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                                    )
                                }
                            }
                        }

                        if (rowDrivers.size < columnsCount) {
                            for (i in 0 until (columnsCount - rowDrivers.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // TAB 2: COURIER & ZONES CONFIGURATION
            if (selectedTab == 2) {
                item {
                    Text("Cape Town Delivery Zones & Fees", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text("Configure delivery fees, minimum order amounts, and estimated arrival times.", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(deliveryZones.chunked(columnsCount)) { rowZones ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (zone in rowZones) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(zone.zoneName, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 13.sp)
                                        Text("Postal Code: ${zone.areaCode} • Est: ~${zone.estimatedMins} mins", fontSize = 11.sp, color = Color.LightGray)
                                        Text("Min Order: R ${zone.minOrderZar.toInt()}", fontSize = 11.sp, color = AfricanBronze)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("R ${String.format(Locale.US, "%.2f", zone.feeZar)}", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                                        Text(if (zone.isActive) "Active Zone" else "Paused", fontSize = 10.sp, color = if (zone.isActive) ForestHerbs else TerracottaAccent)
                                    }
                                }
                            }
                        }
                        if (rowZones.size < columnsCount) {
                            for (i in 0 until (columnsCount - rowZones.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Supported Third-Party Couriers", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(CourierProvider.values().toList().chunked(columnsCount)) { rowProviders ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (provider in rowProviders) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(provider.providerName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text("Prefix: ${provider.trackingPrefix} • API Integrated: ${provider.apiIntegrated}", fontSize = 11.sp, color = AfricanBronze)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (provider.apiIntegrated) ForestHerbs.copy(alpha = 0.2f) else AfricanBronze.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (provider.apiIntegrated) "ACTIVE API" else "MANUAL FLOW",
                                            color = if (provider.apiIntegrated) ForestHerbs else AfricanBronze,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        if (rowProviders.size < columnsCount) {
                            for (i in 0 until (columnsCount - rowProviders.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // TAB 3: MENU & KITCHEN
            if (selectedTab == 3) {
                item {
                    Text("Menu Management & Prices", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text("Toggle dishes sold-out and update live ZAR pricing.", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(menuItems.chunked(columnsCount)) { rowDishes ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (dish in rowDishes) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dish.name, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 13.sp)
                                        Text("Category: ${dish.category} • R ${String.format(Locale.US, "%.2f", dish.priceZar)}", fontSize = 11.sp, color = Color.LightGray)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (dish.isAvailable) "Available" else "Sold Out",
                                            fontSize = 11.sp,
                                            color = if (dish.isAvailable) ForestHerbs else TerracottaAccent,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Switch(
                                            checked = dish.isAvailable,
                                            onCheckedChange = { viewModel.toggleDishAvailability(dish.id, it) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                                        )
                                    }
                                }
                            }
                        }
                        if (rowDishes.size < columnsCount) {
                            for (i in 0 until (columnsCount - rowDishes.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // TAB 4: FULFILMENT & SALES REPORTS
            if (selectedTab == 4) {
                item {
                    Text("Fulfilment & Sales Analytics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val totalRevenue = allOrders.sumOf { it.totalZar }
                    val restaurantDeliveryCount = allOrders.count { it.fulfilmentType == FulfilmentType.RESTAURANT_DELIVERY.name }
                    val courierDeliveryCount = allOrders.count { it.fulfilmentType == FulfilmentType.THIRD_PARTY_COURIER.name }
                    val collectionCount = allOrders.count { it.fulfilmentType == FulfilmentType.COLLECTION_PICKUP.name }
                    val totalDeliveryFees = allOrders.sumOf { it.deliveryFeeZar }

                    // Responsive KPI Grid: 4 items in one row on wide screens!
                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("TOTAL REVENUE", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("R ${String.format(Locale.US, "%.2f", totalRevenue)}", fontWeight = FontWeight.ExtraBold, color = GoldPrimary, fontSize = 17.sp)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("ALL ORDERS", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${allOrders.size}", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 17.sp)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("306 FLEET DELIVERIES", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$restaurantDeliveryCount", fontWeight = FontWeight.ExtraBold, color = ForestHerbs, fontSize = 17.sp)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("COURIER DISPATCHES", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$courierDeliveryCount", fontWeight = FontWeight.ExtraBold, color = AfricanBronze, fontSize = 17.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("TOTAL REVENUE", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("R ${String.format(Locale.US, "%.2f", totalRevenue)}", fontWeight = FontWeight.ExtraBold, color = GoldPrimary, fontSize = 16.sp)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = ObsidianElevated)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("ALL ORDERS", fontSize = 10.sp, color = AfricanBronze, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${allOrders.size}", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Fulfilment Breakdown", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("306 Fleet Deliveries:", color = Color.White, fontSize = 13.sp)
                                Text("$restaurantDeliveryCount orders", fontWeight = FontWeight.Bold, color = ForestHerbs, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("External Courier Dispatches:", color = Color.White, fontSize = 13.sp)
                                Text("$courierDeliveryCount orders", fontWeight = FontWeight.Bold, color = AfricanBronze, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Direct Collections / Pickups:", color = Color.White, fontSize = 13.sp)
                                Text("$collectionCount orders", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ObsidianBorder))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Delivery Fees Collected:", color = Color.LightGray, fontSize = 13.sp)
                                Text("R ${String.format(Locale.US, "%.2f", totalDeliveryFees)}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
