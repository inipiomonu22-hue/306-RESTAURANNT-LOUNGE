package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MenuItemEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DishCustomizeDialog
import com.example.ui.components.WhatsAppHelper
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
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToMenu: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onNavigateToQRCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val menuItems by viewModel.menuItems.collectAsState()
    val dailySpecials by viewModel.dailySpecials.collectAsState()
    val settings by viewModel.restaurantSettings.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForCustomization by remember { mutableStateOf<MenuItemEntity?>(null) }

    val categories = listOf(
        "All",
        "Rice Dishes",
        "Soups & Swallows",
        "Proteins & Grills",
        "Everyday Meals",
        "Sides & Snacks",
        "Drinks & Cocktails"
    )
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    selectedItemForCustomization?.let { dish ->
        DishCustomizeDialog(
            menuItem = dish,
            onDismiss = { selectedItemForCustomization = null },
            onAddToCart = { protein, swallow, spice, notes, qty ->
                viewModel.addToCart(dish, protein = protein, swallow = swallow, spice = spice, notes = notes, quantity = qty)
                selectedItemForCustomization = null
            }
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        val isWide = maxWidth >= 600.dp
        val columnsCount = if (maxWidth >= 900.dp) 3 else if (maxWidth >= 600.dp) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (isWide) 40.dp else 90.dp)
        ) {
            // Top Branding Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_306),
                                contentDescription = "306 Restaurant & Lounge Logo",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "306 RESTAURANT & LOUNGE",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = AfricanBronze,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "306 Long St, Cape Town City Centre",
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Open Status Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(ForestHerbs.copy(alpha = 0.2f))
                                    .border(1.dp, ForestHerbs, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (settings?.isOpenForOrders != false) "● OPEN NOW" else "● KITCHEN CLOSED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestHerbs
                                )
                            }
                        }
                    }
                }
            }

            // Hero and Quick Actions Section (Responsive layout for Wide vs Compact)
            item {
                if (isWide) {
                    // Wide Screen: Hero on Left, Fulfilment & Quick Actions on Right
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left: Hero Feast Banner
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .height(260.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_feast),
                                contentDescription = "Authentic Nigerian Feast in Cape Town",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, ObsidianDark.copy(alpha = 0.95f)),
                                            startY = 80f
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(18.dp)
                            ) {
                                Text(
                                    text = "Authentic Nigerian Cuisine & Lounge",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Smoky Jollof, Suya & Native Soups",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = onNavigateToMenu,
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Explore Menu", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val url = "https://api.whatsapp.com/send?phone=27713063060&text=Hello%20306%20Restaurant%20%26%20Lounge%2C%20I%20would%20like%20to%20place%20an%20order."
                                            WhatsAppHelper.openWhatsApp(context, url)
                                        },
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestHerbs),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "WhatsApp", tint = ForestHerbs, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp Order", color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        // Right: Fulfilment options & Quick Actions
                        Column(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(260.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Fulfilment strip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("306 Fleet", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                        Text("Own Drivers", fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AfricanBronze, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Couriers", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                        Text("Uber / Mr D", fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.Storefront, contentDescription = null, tint = ForestHerbs, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("Pickup", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                        Text("Long St Desk", fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                            }

                            // Book Table Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToReservations() },
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(GoldPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Table Reservations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Book Lounge Seating & VIP Booths", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }

                            // QR Poster Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToQRCode() },
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ForestHerbs.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(ForestHerbs.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.QrCode, contentDescription = null, tint = ForestHerbs, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Table QR Code Generator", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Printable Wall & Dining Table Codes", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Compact Screen: Vertical Stack
                    Column {
                        // Hero Feast Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_feast),
                                contentDescription = "Authentic Nigerian Feast in Cape Town",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(200.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, ObsidianDark.copy(alpha = 0.92f)),
                                            startY = 60f
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Authentic Nigerian Cuisine & Lounge",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Smoky Jollof, Suya & Native Soups",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onNavigateToMenu,
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Explore Menu", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val url = "https://api.whatsapp.com/send?phone=27713063060&text=Hello%20306%20Restaurant%20%26%20Lounge%2C%20I%20would%20like%20to%20place%20an%20order."
                                            WhatsAppHelper.openWhatsApp(context, url)
                                        },
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestHerbs),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "WhatsApp", tint = ForestHerbs, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp Order", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Fulfilment Highlights Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("306 Fleet", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                    Text("Own Drivers", fontSize = 10.sp, color = Color.LightGray)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AfricanBronze, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Couriers", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                    Text("Uber/Mr D/TCG", fontSize = 10.sp, color = Color.LightGray)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = ObsidianElevated),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = ForestHerbs, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Pickup", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                    Text("Long St Desk", fontSize = 10.sp, color = Color.LightGray)
                                }
                            }
                        }

                        // Quick Actions: Table Reservation & QR Code Poster
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToReservations() },
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(GoldPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Book Table", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Lounge Seating", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToQRCode() },
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(ForestHerbs.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.QrCode, contentDescription = null, tint = ForestHerbs, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Table QR Code", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Wall & Table QR", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchQuery.value = it
                    },
                    placeholder = { Text("Search Jollof, Egusi, Suya, Chapman...", color = Color.Gray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWide) 20.dp else 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Category Horizontal Carousel
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = if (isWide) 20.dp else 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) GoldPrimary else ObsidianElevated)
                                .border(1.dp, if (isSelected) GoldPrimary else ObsidianBorder, RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectedCategory.value = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) ObsidianDark else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Today's Specials Header & Carousel
            if (dailySpecials.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isWide) 20.dp else 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Specials", tint = TerracottaAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Today's Specials",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Daily Deals",
                                fontSize = 12.sp,
                                color = GoldPrimary
                            )
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = if (isWide) 20.dp else 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(dailySpecials) { special ->
                                Card(
                                    modifier = Modifier
                                        .width(280.dp)
                                        .clickable { onNavigateToMenu() },
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccent.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(TerracottaAccent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(special.bannerText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(special.title, fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(special.description, color = Color.LightGray, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("R ${String.format(Locale.US, "%.2f", special.priceZar)}", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Featured Dishes List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = if (isWide) 20.dp else 16.dp, end = if (isWide) 20.dp else 16.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Popular Nigerian Delicacies" else selectedCategory,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${menuItems.size} dishes",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Responsive Multi-Column Dish Cards
            items(menuItems.chunked(columnsCount)) { rowDishes ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWide) 20.dp else 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (dish in rowDishes) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedItemForCustomization = dish },
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = dish.name,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldPrimary,
                                                fontSize = 14.sp
                                            )
                                            if (dish.isChefRecommended) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(TerracottaAccent)
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text("CHEF", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "• ${dish.portionDescription}",
                                            fontSize = 11.sp,
                                            color = AfricanBronze
                                        )
                                    }
                                    Text(
                                        text = "R ${String.format(Locale.US, "%.2f", dish.priceZar)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dish.description,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ObsidianElevated)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("~${dish.preparationTimeMins}m", fontSize = 10.sp, color = Color.LightGray)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ObsidianElevated)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(dish.spiceLevel, fontSize = 10.sp, color = TerracottaAccent)
                                        }
                                    }

                                    Button(
                                        onClick = { selectedItemForCustomization = dish },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("+ Add", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Spacer if odd number in row
                    if (rowDishes.size < columnsCount) {
                        for (i in 0 until (columnsCount - rowDishes.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
