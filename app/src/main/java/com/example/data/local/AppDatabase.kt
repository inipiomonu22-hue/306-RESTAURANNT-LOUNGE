package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailySpecialEntity
import com.example.data.model.DeliveryZoneEntity
import com.example.data.model.DriverEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MenuItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        DeliveryZoneEntity::class,
        DriverEntity::class,
        ReservationEntity::class,
        DailySpecialEntity::class,
        RestaurantSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun reservationDao(): ReservationDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_306_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Seed Menu Items
            val initialMenuItems = listOf(
                // Rice Dishes
                MenuItemEntity(
                    id = "rice_1",
                    name = "Smoky Nigerian Party Jollof Rice",
                    description = "Cape Town's finest woodsmoke party Jollof rice simmered in slow-roasted bell peppers, tomatoes, and Nigerian thyme. Served with sweet fried plantains (dodo).",
                    priceZar = 145.00,
                    category = "Rice Dishes",
                    portionDescription = "Generous 1-Person Serving",
                    spiceLevel = "Medium Hot",
                    availableProteins = "Grilled Chicken, Fried Beef, Goat Meat, Fried Croaker Fish",
                    dietaryTags = "HALAL,BESTSELLER",
                    preparationTimeMins = 20,
                    isDailySpecial = true,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "rice_2",
                    name = "Authentic Nigerian Fried Rice",
                    description = "Fragrant long-grain rice wok-tossed with fresh sweetcorn, green peas, carrots, liver morsels, and rich chicken broth seasoning.",
                    priceZar = 155.00,
                    category = "Rice Dishes",
                    portionDescription = "1-Person Serving with Salad",
                    spiceLevel = "Mild",
                    availableProteins = "Grilled Turkey, Fried Chicken, Peppered Beef",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 20,
                    imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "rice_3",
                    name = "Ofada Rice & Ayamase Designer Stew",
                    description = "Traditional unpolished aromatic Ofada rice paired with authentic bleached palm-oil green pepper sauce, boiled egg, diced assorted meats, and dried locust beans (iru).",
                    priceZar = 185.00,
                    category = "Rice Dishes",
                    portionDescription = "Traditional Leaf Serving",
                    spiceLevel = "Hot",
                    availableProteins = "Assorted Offal & Beef, Boiled Egg, Fried Fish",
                    dietaryTags = "HALAL,CHEF_SPECIAL",
                    preparationTimeMins = 25,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "rice_4",
                    name = "White Rice & Rich Native Stew",
                    description = "Fluffy steamed long-grain rice accompanied by traditional Nigerian pepper and tomato reduction stew with scent leaf accents.",
                    priceZar = 135.00,
                    category = "Rice Dishes",
                    portionDescription = "Standard Portion",
                    spiceLevel = "Medium",
                    availableProteins = "Beef, Chicken, Fried Fish",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 15,
                    imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=600&q=80"
                ),
                // Soups and Swallows
                MenuItemEntity(
                    id = "soup_1",
                    name = "Royal Nigerian Egusi Soup",
                    description = "Rich ground melon seed soup enriched with flaked stockfish, smoked catfish, crayfish, and tender bitterleaf/spinach. Paired with your choice of traditional swallow.",
                    priceZar = 195.00,
                    category = "Soups & Swallows",
                    portionDescription = "Bowl with 1 Swallow Portion",
                    spiceLevel = "Medium",
                    availableProteins = "Goat Meat, Beef, Assorted (Shaki/Kpomo), Dry Fish",
                    availableSwallows = "Pounded Yam, Eba (Garri), Semovita, Amala",
                    dietaryTags = "HALAL,BESTSELLER",
                    preparationTimeMins = 25,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "soup_2",
                    name = "Slippery Ogbono Soup Supreme",
                    description = "Slow-simmered wild bush mango seed soup cooked with dry prawn powder, bitterleaf, smoked catfish, and selected meat cuts.",
                    priceZar = 190.00,
                    category = "Soups & Swallows",
                    portionDescription = "Bowl with 1 Swallow Portion",
                    spiceLevel = "Medium",
                    availableProteins = "Beef, Goat Meat, Shaki, Stockfish",
                    availableSwallows = "Pounded Yam, Eba (Garri), Semovita, Amala",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 25,
                    imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "soup_3",
                    name = "Cape Coast Seafood Okra Soup",
                    description = "Freshly crushed tender okra loaded with fresh tiger prawns, calamari rings, whole crab claws, and Cape Town linefish in a savoury spiced broth.",
                    priceZar = 230.00,
                    category = "Soups & Swallows",
                    portionDescription = "Lounge Premium Bowl",
                    spiceLevel = "Medium Hot",
                    availableProteins = "Tiger Prawns, Calamari, Fresh Fish, Crabs",
                    availableSwallows = "Pounded Yam, Eba (Garri), Semovita",
                    dietaryTags = "HALAL,SEAFOOD,CHEF_SPECIAL",
                    preparationTimeMins = 30,
                    isDailySpecial = true,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "soup_4",
                    name = "Efo Riro (Yoruba Rich Spinach Stew)",
                    description = "Authentic Yoruba vegetable masterpiece featuring blanched spinach leaves tossed with shredded smoked catfish, kpomo, shaki, and locust beans.",
                    priceZar = 195.00,
                    category = "Soups & Swallows",
                    portionDescription = "Bowl with 1 Swallow Portion",
                    spiceLevel = "Hot",
                    availableProteins = "Goat Meat, Fried Beef, Smoked Fish, Kpomo",
                    availableSwallows = "Pounded Yam, Eba (Garri), Amala",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 25,
                    imageUrl = "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "soup_5",
                    name = "Authentic Calabar Afang Soup",
                    description = "Traditional shredded Ukazi (Afang) leaves pounded with fresh waterleaf, shelled periwinkles, stockfish head, and rich palm oil.",
                    priceZar = 210.00,
                    category = "Soups & Swallows",
                    portionDescription = "Rich Bowl with Swallow",
                    spiceLevel = "Medium",
                    availableProteins = "Goat Meat, Beef, Periwinkles, Stockfish",
                    availableSwallows = "Pounded Yam, Eba, Semo",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 25,
                    imageUrl = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=600&q=80"
                ),
                // Proteins & Grills
                MenuItemEntity(
                    id = "grill_1",
                    name = "Signature Lagos Beef Suya Skewers",
                    description = "Thin ribbons of prime beef marinated in roasted kuli-kuli peanut spice, ginger, and garlic, flash-grilled over hot coals. Served with red onions & Roma tomatoes.",
                    priceZar = 160.00,
                    category = "Proteins & Grills",
                    portionDescription = "Platter of 4 Large Skewers",
                    spiceLevel = "Hot",
                    availableProteins = "Prime Beef Fillet",
                    dietaryTags = "HALAL,SPICY,BESTSELLER",
                    preparationTimeMins = 15,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "grill_2",
                    name = "Spicy Roasted Goat Meat (Asun)",
                    description = "Tender bone-in goat meat charred over wood fire and tossed in a smoking hot habanero pepper, sweet bell pepper, and spring onion glaze.",
                    priceZar = 180.00,
                    category = "Proteins & Grills",
                    portionDescription = "Lounge Sharing Platter",
                    spiceLevel = "Extra Hot",
                    availableProteins = "Charred Goat Meat",
                    dietaryTags = "HALAL,EXTRA_SPICY",
                    preparationTimeMins = 20,
                    isDailySpecial = true,
                    imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "grill_3",
                    name = "Whole Grilled Peppered Croaker Fish",
                    description = "Whole Cape Atlantic Croaker fish scored and fire-roasted with 306 signature pepper glaze. Accompanied by crispy golden plantains and pickled slaw.",
                    priceZar = 250.00,
                    category = "Proteins & Grills",
                    portionDescription = "Whole Fish (Approx 700g)",
                    spiceLevel = "Medium Hot",
                    availableProteins = "Fresh Croaker Fish",
                    dietaryTags = "HALAL,SEAFOOD,CHEF_SPECIAL",
                    preparationTimeMins = 30,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "grill_4",
                    name = "Spiced Turkey & Fried Chicken Bites",
                    description = "Succulent turkey wings or drumsticks parboiled in native aromatics, deep-fried to a golden crunch, and tossed in rich tomato-habanero sauce.",
                    priceZar = 150.00,
                    category = "Proteins & Grills",
                    portionDescription = "Platter with Herb Dip",
                    spiceLevel = "Medium",
                    availableProteins = "Turkey Wings, Chicken Drumsticks",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 15,
                    imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "grill_5",
                    name = "Lagos Island Assorted Pepper Soup",
                    description = "Aromatic steaming herbal broth infused with uda seeds, calabash nutmeg, and tender cuts of goat meat, tripe, and liver.",
                    priceZar = 165.00,
                    category = "Proteins & Grills",
                    portionDescription = "Hot Tureen with Bread Roll",
                    spiceLevel = "Hot",
                    availableProteins = "Goat Meat, Beef Offal, Catfish",
                    dietaryTags = "HALAL,SPICY",
                    preparationTimeMins = 20,
                    imageUrl = "https://images.unsplash.com/photo-1574484284002-952d92456975?auto=format&fit=crop&w=600&q=80"
                ),
                // Everyday & Vegetarian
                MenuItemEntity(
                    id = "every_1",
                    name = "Ewa Aganyin & Fried Plantains",
                    description = "Slow-braised buttery honey beans mashed to velvet perfection, drenched in intense slow-fried dark Aganyin pepper sauce with golden dodo.",
                    priceZar = 140.00,
                    category = "Everyday Meals",
                    portionDescription = "Standard Serving",
                    spiceLevel = "Medium Hot",
                    availableProteins = "Fried Fish, Boiled Egg, Fried Beef",
                    dietaryTags = "HALAL,VEGETARIAN",
                    preparationTimeMins = 15,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "every_2",
                    name = "Asaro (Yam Porridge) with Smoked Fish",
                    description = "Sweet Puna yam cubes braised in red palm oil broth, enriched with smoked catfish flakes, habanero peppers, and fresh greens.",
                    priceZar = 145.00,
                    category = "Everyday Meals",
                    portionDescription = "Hearty Bowl",
                    spiceLevel = "Medium",
                    availableProteins = "Smoked Catfish, Fried Beef",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 20,
                    imageUrl = "https://images.unsplash.com/photo-1543339308-43e59d6b73a6?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "every_3",
                    name = "Lounge Gourmet Moi Moi Platter",
                    description = "Steamed bean souffle enriched with olive oil, sweet bell pepper puree, flaked smoked fish, and boiled egg slice.",
                    priceZar = 85.00,
                    category = "Everyday Meals",
                    portionDescription = "2 Wrapped Portions",
                    spiceLevel = "Mild",
                    dietaryTags = "HALAL,GLUTEN_FREE",
                    preparationTimeMins = 10,
                    imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=600&q=80"
                ),
                // Sides & Snacks
                MenuItemEntity(
                    id = "side_1",
                    name = "Dodo (Golden Fried Plantain)",
                    description = "Sweet, perfectly ripe plantain slices fried to caramelized golden bliss.",
                    priceZar = 55.00,
                    category = "Sides & Snacks",
                    portionDescription = "Side Bowl",
                    dietaryTags = "VEGETARIAN,VEGAN,BESTSELLER",
                    preparationTimeMins = 10,
                    imageUrl = "https://images.unsplash.com/photo-1528736235302-52922df5c122?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "side_2",
                    name = "Warm Spiced Puff Puff (6 Pieces)",
                    description = "Golden spheres of sweet fermented dough lightly dusted with nutmeg and cinnamon.",
                    priceZar = 60.00,
                    category = "Sides & Snacks",
                    portionDescription = "6 Pieces in Box",
                    dietaryTags = "VEGETARIAN",
                    preparationTimeMins = 10,
                    imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "side_3",
                    name = "Nigerian Flaky Meat Pies (2 Pieces)",
                    description = "Traditional buttery golden pastry pocket filled with seasoned minced beef, potatoes, and carrots.",
                    priceZar = 75.00,
                    category = "Sides & Snacks",
                    portionDescription = "2 Freshly Baked Pies",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 10,
                    imageUrl = "https://images.unsplash.com/photo-1509722747041-616f39b57569?auto=format&fit=crop&w=600&q=80"
                ),
                // Drinks & Lounge
                MenuItemEntity(
                    id = "drink_1",
                    name = "Classic 306 Chapman Mocktail",
                    description = "Nigeria's premier lounge refreshment: sparkling citrus, splash of Angostura bitters, fresh cucumber, lemon ribbons, and Maraschino cherry.",
                    priceZar = 75.00,
                    category = "Drinks & Cocktails",
                    portionDescription = "Large Chilled Glass",
                    dietaryTags = "VEGETARIAN,BESTSELLER",
                    preparationTimeMins = 5,
                    isChefRecommended = true,
                    imageUrl = "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "drink_2",
                    name = "Zobo Cooler (Hibiscus, Pineapple & Ginger)",
                    description = "Artisanal slow-brewed hibiscus flower nectar spiced with fresh ginger root, pineapple peel, and cloves. Served ice-cold.",
                    priceZar = 50.00,
                    category = "Drinks & Cocktails",
                    portionDescription = "500ml Bottle",
                    dietaryTags = "VEGAN",
                    preparationTimeMins = 5,
                    imageUrl = "https://images.unsplash.com/photo-1556881286-fc6915169721?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "drink_3",
                    name = "Ice Cold Malta Guinness",
                    description = "Imported premium African malt beverage brewed with barley and vitamin fortification.",
                    priceZar = 45.00,
                    category = "Drinks & Cocktails",
                    portionDescription = "330ml Can",
                    dietaryTags = "HALAL",
                    preparationTimeMins = 2,
                    imageUrl = "https://images.unsplash.com/photo-1608270586620-248524c67de9?auto=format&fit=crop&w=600&q=80"
                ),
                MenuItemEntity(
                    id = "drink_4",
                    name = "Fresh Sweet Palm Wine",
                    description = "Authentic naturally tapped palm nectar chilled for refreshing lounge drinking.",
                    priceZar = 85.00,
                    category = "Drinks & Cocktails",
                    portionDescription = "500ml Carafe",
                    dietaryTags = "VEGAN",
                    preparationTimeMins = 5,
                    imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=600&q=80"
                )
            )
            database.menuDao().insertMenuItems(initialMenuItems)

            // Seed Delivery Zones in Cape Town
            val initialZones = listOf(
                DeliveryZoneEntity("City Bowl & Cape Town CBD", "8001", 35.00, 150.00, 30),
                DeliveryZoneEntity("Foreshore & V&A Waterfront", "8002", 40.00, 150.00, 35),
                DeliveryZoneEntity("Green Point & Sea Point", "8005", 45.00, 180.00, 40),
                DeliveryZoneEntity("Woodstock, Salt River & Observatory", "7925", 50.00, 200.00, 45),
                DeliveryZoneEntity("Camps Bay, Clifton & Bantry Bay", "8005", 65.00, 250.00, 50),
                DeliveryZoneEntity("Southern Suburbs (Claremont / Rondebosch)", "7700", 75.00, 250.00, 55),
                DeliveryZoneEntity("Century City, Milnerton & Blouberg", "7441", 85.00, 300.00, 60)
            )
            database.deliveryDao().insertZones(initialZones)

            // Seed Restaurant-owned Drivers
            val initialDrivers = listOf(
                DriverEntity(
                    driverId = "DRV-101",
                    name = "Kofi Mensah",
                    phone = "+27 82 456 7890",
                    vehicleType = "Motorbike (Insulated Box)",
                    licensePlate = "CA 882-WP",
                    isAvailable = true
                ),
                DriverEntity(
                    driverId = "DRV-102",
                    name = "Tariq Adams",
                    phone = "+27 73 987 6543",
                    vehicleType = "Motorbike (Thermal Bag)",
                    licensePlate = "CA 401-EC",
                    isAvailable = true
                ),
                DriverEntity(
                    driverId = "DRV-103",
                    name = "Sipho Ndlovu",
                    phone = "+27 61 234 5678",
                    vehicleType = "Toyota Bakkie",
                    licensePlate = "CA 703-GP",
                    isAvailable = true
                )
            )
            database.deliveryDao().insertDrivers(initialDrivers)

            // Seed Daily Specials
            val initialSpecials = listOf(
                DailySpecialEntity(
                    id = "spec_1",
                    title = "Weekend Jollof & Asun Combo",
                    description = "Smoky Jollof Rice + Spicy Roasted Asun Goat Meat + Fried Dodo + Chapman Mocktail",
                    priceZar = 295.00,
                    bannerText = "CHEF'S WEEKEND SPECIAL"
                ),
                DailySpecialEntity(
                    id = "spec_2",
                    title = "Royal Seafood Okra Feast",
                    description = "Seafood Okra Soup with Tiger Prawns & Crab + 2 Pounded Yam swallows",
                    priceZar = 280.00,
                    bannerText = "CAPETOWN SEAFOOD SPECIAL"
                )
            )
            database.menuDao().insertDailySpecials(initialSpecials)

            // Seed Restaurant Settings
            val initialSettings = RestaurantSettingsEntity(
                id = 1,
                restaurantName = "306 Restaurant & Lounge",
                physicalAddress = "306 Long Street, Cape Town City Centre, 8001",
                officialPhone = "+27 21 306 3060",
                officialWhatsApp = "+27 71 306 3060",
                openingHours = "Mon - Sun: 12:00 PM - 11:30 PM",
                freeDeliveryThresholdZar = 550.00,
                isOpenForOrders = true,
                activePaymentProvider = "PayFast / Yoco"
            )
            database.settingsDao().insertSettings(initialSettings)
        }
    }
}
