package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
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

@Composable
fun ReservationsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reservations by viewModel.reservations.collectAsState()

    var guestName by remember { mutableStateOf("Adewale Johnson") }
    var guestPhone by remember { mutableStateOf("+27 76 987 6543") }
    var guestEmail by remember { mutableStateOf("adewale@capetown.co.za") }
    var selectedDate by remember { mutableStateOf("Tonight (10 Oct)") }
    var selectedTime by remember { mutableStateOf("19:30") }
    var numberOfGuests by remember { mutableIntStateOf(4) }
    var selectedArea by remember { mutableStateOf("Main Dining Lounge") }
    var selectedOccasion by remember { mutableStateOf("Birthday Dinner") }
    var specialRequests by remember { mutableStateOf("High table near the DJ booth, celebration cake arriving.") }
    var bookingMessage by remember { mutableStateOf<String?>(null) }

    val dates = listOf("Tonight (10 Oct)", "Tomorrow (11 Oct)", "Friday (12 Oct)", "Saturday (13 Oct)", "Sunday (14 Oct)")
    val times = listOf("18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30")
    val seatingAreas = listOf("Main Dining Lounge", "VIP Room & Booth", "Cocktail Bar Terrace")
    val occasions = listOf("Casual Dining", "Birthday Dinner", "Business Dinner", "Romantic Date", "Anniversary")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "Table Reservations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = GoldPrimary
            )
            Text(
                text = "306 Restaurant & Lounge • 306 Long Street, Cape Town",
                fontSize = 12.sp,
                color = AfricanBronze
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Reservation Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reserve a Table", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date selector
                    Text("Select Date", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(dates) { date ->
                            val isSelected = selectedDate == date
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) GoldPrimary else ObsidianElevated)
                                    .clickable { selectedDate = date }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = date,
                                    color = if (isSelected) ObsidianDark else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time selector
                    Text("Select Time Slot", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(times) { time ->
                            val isSelected = selectedTime == time
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TerracottaAccent else ObsidianElevated)
                                    .clickable { selectedTime = time }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = time,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Number of guests
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Number of Guests", fontSize = 13.sp, color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (1..8).forEach { count ->
                                val isSelected = numberOfGuests == count
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) GoldPrimary else ObsidianElevated)
                                        .clickable { numberOfGuests = count },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$count",
                                        color = if (isSelected) ObsidianDark else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Seating Area
                    Text("Preferred Lounge Area", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        seatingAreas.forEach { area ->
                            val isSelected = selectedArea == area
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ObsidianElevated else ObsidianDark)
                                    .clickable { selectedArea = area }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = area,
                                    color = if (isSelected) GoldPrimary else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact inputs
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = guestName,
                            onValueChange = { guestName = it },
                            label = { Text("Guest Name") },
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
                            value = guestPhone,
                            onValueChange = { guestPhone = it },
                            label = { Text("Phone") },
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

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = specialRequests,
                        onValueChange = { specialRequests = it },
                        label = { Text("Occasion & Special Requests") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.bookTableReservation(
                                guestName = guestName,
                                guestPhone = guestPhone,
                                guestEmail = guestEmail,
                                date = selectedDate,
                                time = selectedTime,
                                numberOfGuests = numberOfGuests,
                                seatingArea = selectedArea,
                                occasion = selectedOccasion,
                                specialRequests = specialRequests,
                                onResult = { success, msg ->
                                    bookingMessage = msg
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Confirm Table Reservation", color = ObsidianDark, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }

                    bookingMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestHerbs.copy(alpha = 0.2f))
                                .padding(10.dp)
                        ) {
                            Text(msg, color = ForestHerbs, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // WhatsApp direct VIP table booking
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("VIP Lounge & Large Group Bookings", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 13.sp)
                        Text("Chat directly with 306 management for exclusive bottle service & birthdays.", fontSize = 11.sp, color = Color.LightGray)
                    }
                    Button(
                        onClick = {
                            val url = "https://api.whatsapp.com/send?phone=27713063060&text=Hello%20306%20Lounge%2C%20I%20would%20like%20to%20enquire%20about%20a%20VIP%20table%20reservation."
                            WhatsAppHelper.openWhatsApp(context, url)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestHerbs),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Past / Confirmed Bookings
        if (reservations.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Table Bookings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(reservations) { res ->
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${res.guestName} (${res.numberOfGuests} guests)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${res.reservationDate} at ${res.reservationTime} • ${res.seatingArea}",
                                fontSize = 11.sp,
                                color = AfricanBronze
                            )
                            if (res.specialRequests.isNotEmpty()) {
                                Text(
                                    text = "Notes: ${res.specialRequests}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ForestHerbs.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(res.status, color = ForestHerbs, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
