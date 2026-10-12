package tz.mtanav.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.*
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.map.LocationNavigator

enum class CustomerSection(val title: String) {
    Dashboard("Dashboard"),
    Discover("Discover"),
    Orders("My Orders"),
    Bookings("My Bookings"),
    Cart("Cart & Checkout"),
    Jobs("My Job Applications"),
    SavedOffers("Saved Offers"),
    Favorites("Favorites"),
    Payments("Payments"),
    Earn("MTANAV Earn"),
    Notifications("Notifications"),
    Profile("Profile"),
    Settings("Settings")
}

@Composable
fun CustomerPortalScreen(
    repository: MtanavRepository,
    locationNavigator: LocationNavigator,
    cartItems: List<CartItem>,
    initialSection: CustomerSection = CustomerSection.Dashboard,
    onNavigateToPublicMap: () -> Unit,
    onLogoutClicked: () -> Unit,
    onCartUpdated: (List<CartItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(initialSection) }
    var orders by remember { mutableStateOf<List<OrderDto>>(emptyList()) }
    var bookings by remember { mutableStateOf<List<BookingDto>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun loadCustomerData() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orders = repository.getCustomerOrders()
                bookings = repository.getCustomerBookings()
            } catch (e: Exception) {
                errorMessage = "Failed to load customer portal data."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCustomerData()
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // CUSTOMER SIDEBAR
        CustomerSidebar(
            activeSection = activeSection,
            user = repository.currentUser,
            cartItemCount = cartItems.sumOf { it.quantity },
            onSectionSelected = { activeSection = it },
            onNavigateToPublicMap = onNavigateToPublicMap,
            onLogoutClicked = onLogoutClicked,
            modifier = Modifier.width(300.dp).fillMaxHeight()
        )

        VerticalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.fillMaxHeight().width(1.dp))

        // CUSTOMER CONTENT AREA (NO GOOGLE MAP)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(AppBackground)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Column {
                        Text(
                            text = activeSection.title.uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryDeepBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Customer Account • ${repository.currentUser?.fullName ?: "Customer"}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = onNavigateToPublicMap,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🔍 EXPLORE PRODUCTS & SERVICES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                message?.let { msg ->
                    Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text(msg, color = SuccessGreen, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                errorMessage?.let { err ->
                    Surface(shape = RoundedCornerShape(8.dp), color = ErrorRed.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text(err, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                // RIGHT-SIDE CONTENT ROUTING
                when (activeSection) {
                    CustomerSection.Dashboard -> CustomerDashboardContent(
                        user = repository.currentUser,
                        ordersCount = orders.size,
                        bookingsCount = bookings.size,
                        cartCount = cartItems.size,
                        onNavigateSection = { activeSection = it },
                        onExploreClick = onNavigateToPublicMap
                    )

                    CustomerSection.Orders -> CustomerOrdersContent(
                        orders = orders,
                        locationNavigator = locationNavigator
                    )

                    CustomerSection.Bookings -> CustomerBookingsContent(
                        bookings = bookings,
                        locationNavigator = locationNavigator
                    )

                    CustomerSection.Cart -> CustomerCartContent(
                        cartItems = cartItems,
                        repository = repository,
                        onCartUpdated = onCartUpdated,
                        onOrderPlacedSuccess = {
                            message = "Order placed successfully! Transaction complete."
                            onCartUpdated(emptyList())
                            loadCustomerData()
                            activeSection = CustomerSection.Orders
                        }
                    )

                    CustomerSection.Jobs -> CustomerJobApplicationsContent(
                        repository = repository
                    )

                    CustomerSection.SavedOffers -> CustomerSavedOffersContent(
                        repository = repository,
                        onExploreClick = onNavigateToPublicMap
                    )

                    CustomerSection.Payments -> CustomerPaymentsContent(
                        orders = orders,
                        bookings = bookings
                    )

                    CustomerSection.Favorites -> CustomerFavoritesContent(
                        onExploreClick = onNavigateToPublicMap
                    )

                    CustomerSection.Notifications -> CustomerNotificationsContent()

                    CustomerSection.Profile -> CustomerProfileContent(
                        user = repository.currentUser
                    )

                    CustomerSection.Settings -> CustomerSettingsContent()

                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun CustomerSidebar(
    activeSection: CustomerSection,
    user: UserDto?,
    cartItemCount: Int,
    onSectionSelected: (CustomerSection) -> Unit,
    onNavigateToPublicMap: () -> Unit,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryDeepBlue, PrimaryBlue)
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text = "MTANAV",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = SurfaceWhite,
                letterSpacing = 1.sp
            )
            Text(
                text = "CUSTOMER PORTAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HighlightGold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            user?.let {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceWhite.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "👤 ${it.fullName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CustomerSection.entries.forEach { section ->
                val isSelected = activeSection == section

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PrimaryDeepBlue else Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSectionSelected(section) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = section.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SurfaceWhite else TextPrimary
                        )

                        if (section == CustomerSection.Cart && cartItemCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ActionTeal
                            ) {
                                Text(
                                    text = "$cartItemCount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))

        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
                onClick = onNavigateToPublicMap,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, PrimaryDeepBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🗺️ Public Map Discovery", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
            }

            TextButton(
                onClick = onLogoutClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign out", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CustomerDashboardContent(
    user: UserDto?,
    ordersCount: Int,
    bookingsCount: Int,
    cartCount: Int,
    onNavigateSection: (CustomerSection) -> Unit,
    onExploreClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("WELCOME BACK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(user?.fullName ?: "Valued Customer", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text("Browse products & services or view your order history below", fontSize = 13.sp, color = TextSecondary)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            CustomerStatCard("ACTIVE ORDERS", "$ordersCount orders", "Products & items", ActionTeal, modifier = Modifier.weight(1f).clickable { onNavigateSection(CustomerSection.Orders) })
            CustomerStatCard("BOOKINGS", "$bookingsCount service bookings", "Garage, Salon & Repairs", PrimaryBlue, modifier = Modifier.weight(1f).clickable { onNavigateSection(CustomerSection.Bookings) })
            CustomerStatCard("CART ITEMS", "$cartCount items", "Pending checkout", PrimaryDeepBlue, modifier = Modifier.weight(1f).clickable { onNavigateSection(CustomerSection.Cart) })
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PrimaryDeepBlue.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("DISCOVER PRODUCTS & SERVICES ON MTANAV MAP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                Text("Use public discovery map to find local shops, spoon, rice, phone chargers, shoes, garage, car wash, salon and phone repair near you.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onExploreClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue), shape = RoundedCornerShape(8.dp)) {
                    Text("OPEN PUBLIC DISCOVERY MAP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CustomerStatCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun CustomerOrdersContent(orders: List<OrderDto>, locationNavigator: LocationNavigator) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY PRODUCT ORDERS (${orders.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        if (orders.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("No product orders placed yet.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            orders.forEach { ord ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Order #${ord.id} • ${ord.businessName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Delivery address: ${ord.deliveryAddress ?: "Store pickup"}", fontSize = 12.sp, color = TextSecondary)
                                Text("Ordered on: ${ord.createdAt}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text("TSh ${ord.totalAmount.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = ActionTeal.copy(alpha = 0.15f)) {
                                Text("STATUS: ${ord.orderStatus}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ActionTeal, modifier = Modifier.padding(6.dp))
                            }

                            OutlinedButton(
                                onClick = {
                                    locationNavigator.launchGoogleMapsNavigation(
                                        latitude = -6.8182,
                                        longitude = 39.2778,
                                        businessName = ord.businessName
                                    )
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("NAVIGATE TO SHOP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerBookingsContent(bookings: List<BookingDto>, locationNavigator: LocationNavigator) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY SERVICE BOOKINGS (${bookings.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        if (bookings.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("No service bookings yet.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            bookings.forEach { book ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("${book.serviceName} • ${book.businessName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Booking Date: ${book.bookingDate} at ${book.bookingTime}", fontSize = 12.sp, color = TextSecondary)
                            }
                            Text("TSh ${book.price.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = PrimaryBlue.copy(alpha = 0.12f)) {
                                Text("STATUS: ${book.bookingStatus}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.padding(6.dp))
                            }

                            OutlinedButton(
                                onClick = {
                                    locationNavigator.launchGoogleMapsNavigation(
                                        latitude = -6.7580,
                                        longitude = 39.2450,
                                        businessName = book.businessName
                                    )
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("NAVIGATE TO SERVICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerCartContent(
    cartItems: List<CartItem>,
    repository: MtanavRepository,
    onCartUpdated: (List<CartItem>) -> Unit,
    onOrderPlacedSuccess: () -> Unit
) {
    var deliveryOption by remember { mutableStateOf("DELIVERY") }
    var deliveryAddress by remember { mutableStateOf("Kariakoo, Dar es Salaam") }
    var paymentMethod by remember { mutableStateOf("M-PESA") }
    var isPlacing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val totalAmount = cartItems.sumOf { it.unitPrice * it.quantity }

    if (cartItems.isEmpty()) {
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                Text("🛒", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Cart is Empty", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Browse products from shops on the public discovery map to add items to cart.", fontSize = 12.sp, color = TextSecondary)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SELECTED ITEMS SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    cartItems.forEachIndexed { index, item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("${item.productName} (${item.variantName ?: "Standard"})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${item.businessName} • Qty: ${item.quantity}", fontSize = 12.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("TSh ${(item.unitPrice * item.quantity).toInt()}", fontWeight = FontWeight.Black, color = ActionTeal)
                                IconButton(onClick = {
                                    val newList = cartItems.toMutableList()
                                    newList.removeAt(index)
                                    onCartUpdated(newList)
                                }) {
                                    Text("❌", fontSize = 12.sp)
                                }
                            }
                        }
                        if (index < cartItems.size - 1) HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("DELIVERY & TRANSACTION METHOD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { deliveryOption = "DELIVERY" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (deliveryOption == "DELIVERY") PrimaryBlue else AppBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚚 Delivery", color = if (deliveryOption == "DELIVERY") SurfaceWhite else TextPrimary)
                        }
                        Button(
                            onClick = { deliveryOption = "PICKUP" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (deliveryOption == "PICKUP") PrimaryBlue else AppBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🏪 Store Pickup", color = if (deliveryOption == "PICKUP") SurfaceWhite else TextPrimary)
                        }
                    }

                    if (deliveryOption == "DELIVERY") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Delivery Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("M-PESA", "TIGO PESA", "AIRTEL", "CASH").forEach { method ->
                            val isSelected = paymentMethod == method
                            Button(
                                onClick = { paymentMethod = method },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) ActionTeal else AppBackground),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(method, color = if (isSelected) SurfaceWhite else TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Total Amount Due:", fontSize = 12.sp, color = TextSecondary)
                            Text("TSh ${totalAmount.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = ActionTeal)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isPlacing = true
                                    try {
                                        repository.placeOrder(cartItems, deliveryAddress, deliveryOption)
                                        onOrderPlacedSuccess()
                                    } catch (_: Exception) {
                                    } finally {
                                        isPlacing = false
                                    }
                                }
                            },
                            enabled = !isPlacing,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            if (isPlacing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                            } else {
                                Text("CONFIRM & PAY TRANSACTION", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// MY JOB APPLICATIONS CONTENT
// ==========================================

@Composable
private fun CustomerJobApplicationsContent(repository: MtanavRepository) {
    var applications by remember { mutableStateOf<List<JobApplicationDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadApps() {
        coroutineScope.launch {
            isLoading = true
            try {
                applications = repository.getMyJobApplications()
            } catch (e: Exception) { /* ignored */ }
            finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) {
        loadApps()
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY JOB APPLICATIONS (${applications.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        if (isLoading) {
            CircularProgressIndicator(color = Primary)
        } else if (applications.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("You have not submitted any job applications yet.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            applications.forEach { app ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.jobTitle ?: "Job #${app.jobId}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text("Proposed Rate: TSh ${app.proposedRate.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Action)
                                app.createdAt?.let { Text("Applied on: $it", fontSize = 11.sp, color = TextSecondary) }
                            }

                            val statusColor = when (app.status) {
                                "ACCEPTED" -> SuccessGreen
                                "REJECTED", "WITHDRAWN" -> ErrorRed
                                else -> Primary
                            }

                            Surface(shape = RoundedCornerShape(6.dp), color = statusColor.copy(alpha = 0.15f)) {
                                Text(app.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        app.coverNote?.let { note ->
                            if (note.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Cover Note: $note", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        if (app.status != "WITHDRAWN" && app.status != "REJECTED" && app.status != "ACCEPTED") {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            repository.withdrawJobApplication(app.id)
                                            loadApps()
                                        } catch (e: Exception) { /* ignored */ }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Withdraw Application", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SAVED OFFERS CONTENT
// ==========================================

@Composable
private fun CustomerSavedOffersContent(repository: MtanavRepository, onExploreClick: () -> Unit) {
    var savedOffers by remember { mutableStateOf<List<PromotionDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadSaved() {
        coroutineScope.launch {
            isLoading = true
            try {
                savedOffers = repository.getSavedPromotions()
            } catch (e: Exception) { /* ignored */ }
            finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) {
        loadSaved()
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("SAVED OFFERS & PROMOTIONS (${savedOffers.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        if (isLoading) {
            CircularProgressIndicator(color = Primary)
        } else if (savedOffers.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("🏷️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No saved promotional offers yet.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onExploreClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue)) {
                        Text("Discover Promotions & Offers", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            savedOffers.forEach { promo ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(promo.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Text(promo.businessName ?: "Business", fontSize = 12.sp, color = Action, fontWeight = FontWeight.Bold)
                            }
                            promo.promotionalPrice?.let {
                                Text("TSh ${it.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Action)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(promo.description, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.unsavePromotion(promo.id)
                                        loadSaved()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Remove", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PAYMENTS CONTENT
// ==========================================

@Composable
private fun CustomerPaymentsContent(orders: List<OrderDto>, bookings: List<BookingDto>) {
    val totalSpent = orders.sumOf { it.totalAmount } + bookings.sumOf { it.price }

    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("TRANSACTION & PAYMENT HISTORY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Total Transactions Completed: ${orders.size + bookings.size}", fontSize = 14.sp)
            Text("Total Amount Paid: TSh ${totalSpent.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ActionTeal)
        }
    }
}

// ==========================================
// FAVORITES CONTENT
// ==========================================

@Composable
private fun CustomerFavoritesContent(onExploreClick: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("FAVORITE SHOPS & SERVICES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("No saved favorites yet.", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onExploreClick, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue)) {
                Text("Browse Public Discovery Map", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// NOTIFICATIONS CONTENT
// ==========================================

@Composable
private fun CustomerNotificationsContent() {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("NOTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("🔔 Welcome to MTANAV! Find items, apply for local jobs, and navigate to verified shops easily.", fontSize = 13.sp)
        }
    }
}

// ==========================================
// PROFILE CONTENT
// ==========================================

@Composable
private fun CustomerProfileContent(user: UserDto?) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("CUSTOMER PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Full Name: ${user?.fullName ?: "N/A"}")
            Text("Email: ${user?.email ?: "N/A"}")
            Text("Phone: ${user?.phoneNumber ?: "+255700000000"}")
        }
    }
}

// ==========================================
// SETTINGS CONTENT
// ==========================================

@Composable
private fun CustomerSettingsContent() {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("ACCOUNT PREFERENCES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("• Order Notifications: Active")
            Text("• Location Permissions: Enabled for discovery")
        }
    }
}
