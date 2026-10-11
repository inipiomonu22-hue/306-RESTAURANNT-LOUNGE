package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
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
fun DriverPortalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDrivers by viewModel.allDrivers.collectAsState()
    val currentDriverId by viewModel.currentDriverId.collectAsState()
    val assignedOrders by viewModel.driverAssignedOrders.collectAsState()

    var showFailedAttemptDialogForOrderId by remember { mutableStateOf<String?>(null) }
    var failedReason by remember { mutableStateOf("Customer unavailable at buzzer") }
    var failedNotes by remember { mutableStateOf("") }

    val activeDriver = allDrivers.firstOrNull { it.driverId == currentDriverId }

    // Failed attempt dialog
    showFailedAttemptDialogForOrderId?.let { orderId ->
        Dialog(onDismissRequest = { showFailedAttemptDialogForOrderId = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Record Delivery Issue (#$orderId)", fontWeight = FontWeight.Bold, color = TerracottaAccent, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Reason:", fontSize = 12.sp, color = Color.LightGray)

                    listOf(
                        "Customer unreachable by phone / gate buzzer",
                        "Incorrect address / security refused entry",
                        "Customer requested rescheduling",
                        "Severe weather / vehicle breakdown"
                    ).forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { failedReason = r }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (failedReason == r) "● $r" else "○ $r",
                                color = if (failedReason == r) GoldPrimary else Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = failedNotes,
                        onValueChange = { failedNotes = it },
                        label = { Text("Driver Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.recordDeliveryAttempt(orderId, failedReason, failedNotes)
                                showFailedAttemptDialogForOrderId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccent),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { showFailedAttemptDialogForOrderId = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Color.Gray)
                        }
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
        val isWide = maxWidth >= 600.dp
        val columnsCount = if (isWide) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = if (isWide) 24.dp else 16.dp, end = if (isWide) 24.dp else 16.dp, top = 16.dp, bottom = 90.dp)
        ) {
            // Driver Portal Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "306 Driver Portal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldPrimary
                        )
                        Text(
                            text = "Restaurant Fleet Courier Dispatch",
                            fontSize = 12.sp,
                            color = AfricanBronze
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ForestHerbs.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${assignedOrders.size} Assigned",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestHerbs
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Driver Switcher Bar
            item {
                Text("Logged In Driver Profile:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(allDrivers) { driver ->
                        val isCurrent = driver.driverId == currentDriverId
                        Card(
                            modifier = Modifier.clickable { viewModel.currentDriverId.value = driver.driverId },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) ObsidianElevated else ObsidianSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) GoldPrimary else ObsidianBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(driver.name, fontWeight = FontWeight.Bold, color = if (isCurrent) GoldPrimary else Color.White, fontSize = 13.sp)
                                Text("${driver.vehicleType} (${driver.licensePlate})", fontSize = 10.sp, color = Color.LightGray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Active Deliveries List
            if (assignedOrders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Deliveries Assigned Yet", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Staff can assign orders to ${activeDriver?.name ?: "you"} from the Restaurant Admin Dashboard.",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            } else {
                items(assignedOrders.chunked(columnsCount)) { rowOrders ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (order in rowOrders) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Order #${order.orderId}", fontWeight = FontWeight.ExtraBold, color = GoldPrimary, fontSize = 16.sp)
                                        StatusBadge(status = order.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Customer: ${order.customerName}", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 14.sp)
                                    Text("Contact: ${order.customerPhone}", color = AfricanBronze, fontSize = 12.sp)

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TerracottaAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(order.deliveryAddress, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            if (order.deliveryInstructions.isNotEmpty()) {
                                                Text("Note: ${order.deliveryInstructions}", color = Color.LightGray, fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Amount: R ${String.format(Locale.US, "%.2f", order.totalZar)} • ${order.paymentMethod}", fontSize = 12.sp, color = Color.Gray)

                                    if (!order.failedAttemptReason.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(TerracottaAccent.copy(alpha = 0.15f))
                                                .padding(8.dp)
                                        ) {
                                            Text("⚠️ Issue: ${order.failedAttemptReason}", color = TerracottaAccent, fontSize = 11.sp)
                                        }
                                    }

                                    // Driver Action Controls
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Call customer
                                        OutlinedButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${order.customerPhone}")
                                                }
                                                context.startActivity(intent)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Call", color = Color.White, fontSize = 12.sp)
                                        }

                                        // Mark Dispatched / On the Way
                                        if (order.status != OrderStatus.OUT_FOR_DELIVERY.name && order.status != OrderStatus.DELIVERED.name) {
                                            Button(
                                                onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.OUT_FOR_DELIVERY) },
                                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Start Delivery", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }

                                        // Mark Delivered
                                        if (order.status == OrderStatus.OUT_FOR_DELIVERY.name) {
                                            Button(
                                                onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.DELIVERED) },
                                                colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Delivered", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }

                                        // Report issue
                                        OutlinedButton(
                                            onClick = { showFailedAttemptDialogForOrderId = order.orderId },
                                            border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Issue", color = TerracottaAccent, fontSize = 11.sp)
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
    }
}
