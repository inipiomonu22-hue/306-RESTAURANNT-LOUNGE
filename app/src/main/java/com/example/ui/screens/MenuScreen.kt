package com.example.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItemEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DishCustomizeDialog
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
fun MenuScreen(
    viewModel: MainViewModel,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val dietaryFilter by viewModel.dietaryFilter.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()

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

    val dietaryTags = listOf(
        "HALAL" to "Halal",
        "SPICY" to "Spicy",
        "VEGETARIAN" to "Vegetarian",
        "SEAFOOD" to "Seafood",
        "BESTSELLER" to "Bestseller"
    )

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

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(ObsidianDark)) {
        val isWide = maxWidth >= 600.dp
        val columnsCount = if (maxWidth >= 900.dp) 3 else if (maxWidth >= 600.dp) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cartCount > 0) 140.dp else if (isWide) 40.dp else 90.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = if (isWide) 20.dp else 16.dp, end = if (isWide) 20.dp else 16.dp, top = 16.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "306 Food Menu",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "Authentic Nigerian Soups, Swallows, Rice & Grills in Cape Town",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.searchQuery.value = it
                        },
                        placeholder = { Text("Filter dishes, ingredients, or spices...", color = Color.Gray, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }

            // Categories
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

            // Dietary Filter row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWide) 20.dp else 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = AfricanBronze, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dietary:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(dietaryTags) { (tagKey, tagLabel) ->
                            val isSelected = dietaryFilter == tagKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) TerracottaAccent else ObsidianElevated)
                                    .clickable {
                                        viewModel.dietaryFilter.value = if (isSelected) null else tagKey
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tagLabel,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Items Count
            item {
                Text(
                    text = "${menuItems.size} dishes found",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = if (isWide) 20.dp else 16.dp, vertical = 6.dp)
                )
            }

            // Responsive Multi-Column Dish List
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
                                .clickable {
                                    if (dish.isAvailable) {
                                        selectedItemForCustomization = dish
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            if (dish.imageUrl.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .background(ObsidianElevated)
                                ) {
                                    AsyncImage(
                                        model = dish.imageUrl,
                                        contentDescription = dish.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
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
                                                fontSize = 15.sp
                                            )
                                            if (!dish.isAvailable) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(TerracottaAccent)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("SOLD OUT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dish.portionDescription,
                                            fontSize = 11.sp,
                                            color = AfricanBronze
                                        )
                                    }

                                    Text(
                                        text = "R ${String.format(Locale.US, "%.2f", dish.priceZar)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dish.description,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
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
                                            Text("Prep: ~${dish.preparationTimeMins}m", fontSize = 10.sp, color = Color.LightGray)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ObsidianElevated)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Spice: ${dish.spiceLevel}", fontSize = 10.sp, color = TerracottaAccent)
                                        }
                                    }

                                    Button(
                                        onClick = { selectedItemForCustomization = dish },
                                        enabled = dish.isAvailable,
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add", tint = ObsidianDark, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Select", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
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

        // Floating Bottom Cart Bar
        if (cartCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = if (isWide) 40.dp else 16.dp, vertical = if (isWide) 20.dp else 80.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCart() },
                    colors = CardDefaults.cardColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianDark)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("$cartCount items", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("View Cart & Checkout", color = ObsidianDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Text(
                            text = "R ${String.format(Locale.US, "%.2f", cartSubtotal)}",
                            color = ObsidianDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}
