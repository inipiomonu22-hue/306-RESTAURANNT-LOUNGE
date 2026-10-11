package com.example.data.model

import java.util.UUID

data class CartItem(
    val cartItemId: String = UUID.randomUUID().toString(),
    val menuItem: MenuItemEntity,
    val selectedPortion: String = "Regular Portion",
    val selectedProtein: String = "",
    val selectedSwallow: String = "",
    val selectedSpiceLevel: String = "Medium Hot",
    val specialInstructions: String = "",
    val quantity: Int = 1,
    val unitPriceZar: Double = menuItem.priceZar
) {
    val totalZar: Double
        get() = unitPriceZar * quantity
}
