package com.example.ui.navigation

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.DriverPortalScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.QRCodePosterScreen
import com.example.ui.screens.ReservationsScreen
import com.example.ui.theme.ForestHerbs
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface

enum class AppScreen(val label: String) {
    HOME("Home"),
    MENU("Menu"),
    CART("Order"),
    TRACKING("Track"),
    RESERVATIONS("Tables"),
    QR_POSTER("QR Code"),
    DRIVER_PORTAL("Driver"),
    ADMIN("Admin")
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var currentTrackingOrderId by remember { mutableStateOf<String?>(null) }
    var forceWideLayout by remember { mutableStateOf(false) }
    val cartCount by viewModel.cartItemCount.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    // Hardware & Gesture back navigation handling
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            currentScreen = AppScreen.HOME
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(ObsidianDark)) {
        val isWideScreen = maxWidth >= 580.dp || forceWideLayout

        if (isWideScreen) {
            // WIDE SCREEN / TABLET PREVIEW LAYOUT WITH NAVIGATION RAIL
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = ObsidianSurface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_306),
                                contentDescription = "306 Logo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "306",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = GoldPrimary
                            )
                            Text(
                                text = "CAPE TOWN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxHeight(),
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Home
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.HOME,
                                onClick = { currentScreen = AppScreen.HOME },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Menu
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.MENU,
                                onClick = { currentScreen = AppScreen.MENU },
                                icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu") },
                                label = { Text("Menu", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Cart / Checkout
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.CART,
                                onClick = { currentScreen = AppScreen.CART },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (cartCount > 0) {
                                                Badge(containerColor = GoldPrimary) {
                                                    Text("$cartCount", color = ObsidianDark, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                                    }
                                },
                                label = { Text("Order", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Live Tracking
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.TRACKING,
                                onClick = {
                                    if (currentTrackingOrderId == null && activeOrders.isNotEmpty()) {
                                        currentTrackingOrderId = activeOrders.first().orderId
                                    }
                                    currentScreen = AppScreen.TRACKING
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (activeOrders.isNotEmpty()) {
                                                Badge(containerColor = ForestHerbs) {
                                                    Text("${activeOrders.size}", color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Timeline, contentDescription = "Track")
                                    }
                                },
                                label = { Text("Track", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Table Reservations
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.RESERVATIONS,
                                onClick = { currentScreen = AppScreen.RESERVATIONS },
                                icon = { Icon(Icons.Default.EventSeat, contentDescription = "Tables") },
                                label = { Text("Tables", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // QR Poster
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.QR_POSTER,
                                onClick = { currentScreen = AppScreen.QR_POSTER },
                                icon = { Icon(Icons.Default.QrCode, contentDescription = "QR Code") },
                                label = { Text("QR Poster", fontSize = 9.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Driver Mode
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.DRIVER_PORTAL,
                                onClick = { currentScreen = AppScreen.DRIVER_PORTAL },
                                icon = { Icon(Icons.Default.DirectionsBike, contentDescription = "Driver Portal") },
                                label = { Text("Driver", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // Admin Dashboard
                            NavigationRailItem(
                                selected = currentScreen == AppScreen.ADMIN,
                                onClick = { currentScreen = AppScreen.ADMIN },
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                                label = { Text("Admin", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianDark,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = { forceWideLayout = false },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.padding(horizontal = 4.dp).height(28.dp)
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Phone", color = GoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                )

                // Main Content for Wide Screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianDark)
                ) {
                    RenderScreen(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        currentTrackingOrderId = currentTrackingOrderId,
                        onNavigate = { currentScreen = it },
                        onSetTrackingOrderId = { currentTrackingOrderId = it }
                    )
                }
            }
        } else {
            // COMPACT SCREEN PHONE LAYOUT WITH TOP FEATURE BAR & BOTTOM NAVIGATION
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Surface(
                        color = ObsidianSurface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_306),
                                        contentDescription = "306 Logo",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "306 RESTAURANT & LOUNGE",
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GoldPrimary,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "CAPE TOWN • FULFILMENT",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            fontSize = 8.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { forceWideLayout = true },
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DesktopWindows,
                                        contentDescription = "Toggle Big Screen",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Full Big Screen",
                                        color = GoldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Feature Switcher Chips
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(AppScreen.values()) { screen ->
                                    val isSelected = currentScreen == screen
                                    val label = when (screen) {
                                        AppScreen.CART -> if (cartCount > 0) "Order ($cartCount)" else "Order"
                                        AppScreen.TRACKING -> if (activeOrders.isNotEmpty()) "Track (${activeOrders.size})" else "Track"
                                        else -> screen.label
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) GoldPrimary else ObsidianElevated)
                                            .border(1.dp, if (isSelected) GoldPrimary else ObsidianBorder, RoundedCornerShape(14.dp))
                                            .clickable {
                                                if (screen == AppScreen.TRACKING && currentTrackingOrderId == null && activeOrders.isNotEmpty()) {
                                                    currentTrackingOrderId = activeOrders.first().orderId
                                                }
                                                currentScreen = screen
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) ObsidianDark else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = ObsidianSurface,
                        tonalElevation = 8.dp
                    ) {
                        // 1. Home
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { currentScreen = AppScreen.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        // 2. Menu
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.MENU,
                            onClick = { currentScreen = AppScreen.MENU },
                            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu") },
                            label = { Text("Menu", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        // 3. Cart
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.CART || currentScreen == AppScreen.TRACKING,
                            onClick = { currentScreen = AppScreen.CART },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (cartCount > 0) {
                                            Badge(containerColor = GoldPrimary) {
                                                Text("$cartCount", color = ObsidianDark, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                                }
                            },
                            label = { Text("Order", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        // 4. Reservations
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.RESERVATIONS,
                            onClick = { currentScreen = AppScreen.RESERVATIONS },
                            icon = { Icon(Icons.Default.EventSeat, contentDescription = "Reservations") },
                            label = { Text("Tables", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        // 5. Driver Mode
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.DRIVER_PORTAL,
                            onClick = { currentScreen = AppScreen.DRIVER_PORTAL },
                            icon = { Icon(Icons.Default.DirectionsBike, contentDescription = "Driver Portal") },
                            label = { Text("Driver", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        // 6. Admin
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN,
                            onClick = { currentScreen = AppScreen.ADMIN },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(ObsidianDark)
                ) {
                    RenderScreen(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        currentTrackingOrderId = currentTrackingOrderId,
                        onNavigate = { currentScreen = it },
                        onSetTrackingOrderId = { currentTrackingOrderId = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderScreen(
    currentScreen: AppScreen,
    viewModel: MainViewModel,
    currentTrackingOrderId: String?,
    onNavigate: (AppScreen) -> Unit,
    onSetTrackingOrderId: (String) -> Unit
) {
    when (currentScreen) {
        AppScreen.HOME -> HomeScreen(
            viewModel = viewModel,
            onNavigateToMenu = { onNavigate(AppScreen.MENU) },
            onNavigateToCart = { onNavigate(AppScreen.CART) },
            onNavigateToReservations = { onNavigate(AppScreen.RESERVATIONS) },
            onNavigateToQRCode = { onNavigate(AppScreen.QR_POSTER) }
        )

        AppScreen.MENU -> MenuScreen(
            viewModel = viewModel,
            onNavigateToCart = { onNavigate(AppScreen.CART) }
        )

        AppScreen.CART -> CartCheckoutScreen(
            viewModel = viewModel,
            onOrderPlaced = { orderId ->
                onSetTrackingOrderId(orderId)
                onNavigate(AppScreen.TRACKING)
            }
        )

        AppScreen.TRACKING -> OrderTrackingScreen(
            viewModel = viewModel,
            orderId = currentTrackingOrderId ?: "",
            onBackToHome = { onNavigate(AppScreen.HOME) }
        )

        AppScreen.RESERVATIONS -> ReservationsScreen(
            viewModel = viewModel
        )

        AppScreen.QR_POSTER -> QRCodePosterScreen(
            onNavigateBack = { onNavigate(AppScreen.HOME) }
        )

        AppScreen.DRIVER_PORTAL -> DriverPortalScreen(
            viewModel = viewModel
        )

        AppScreen.ADMIN -> AdminDashboardScreen(
            viewModel = viewModel
        )
    }
}
