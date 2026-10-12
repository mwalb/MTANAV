package tz.mtanav.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tz.mtanav.model.BusinessDto
import tz.mtanav.model.BusinessProductDto
import tz.mtanav.model.BusinessServiceDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.map.LocationNavigator

@Composable
fun ShopProductsScreen(
    businessId: Long,
    selectedProductId: Long? = null,
    selectedServiceId: Long? = null,
    repository: MtanavRepository,
    locationNavigator: LocationNavigator,
    onBackClicked: () -> Unit,
    onProductSelected: (BusinessProductDto) -> Unit,
    modifier: Modifier = Modifier
) {
    var allProducts by remember { mutableStateOf<List<BusinessProductDto>>(emptyList()) }
    var allServices by remember { mutableStateOf<List<BusinessServiceDto>>(emptyList()) }
    var businessInfo by remember { mutableStateOf<BusinessDto?>(null) }
    var showAllProducts by remember { mutableStateOf(false) }

    LaunchedEffect(businessId) {
        allProducts = repository.getBusinessProducts(businessId)
        allServices = repository.getBusinessServices(businessId)
        val myBusinesses = repository.getMyBusinesses()
        businessInfo = myBusinesses.firstOrNull { it.id == businessId }
            ?: repository.getAdminBusinesses().firstOrNull { it.id == businessId }
    }

    val displayedProducts = if (selectedProductId != null && !showAllProducts) {
        val filtered = allProducts.filter { it.productId == selectedProductId }
        if (filtered.isNotEmpty()) filtered else allProducts
    } else {
        allProducts
    }

    val displayedServices = if (selectedServiceId != null && !showAllProducts) {
        val filtered = allServices.filter { it.serviceId == selectedServiceId }
        if (filtered.isNotEmpty()) filtered else allServices
    } else {
        allServices
    }

    val shopName = businessInfo?.name ?: allProducts.firstOrNull()?.businessName ?: allServices.firstOrNull()?.businessName ?: "Demo Store"
    val shopAddress = businessInfo?.let { "${it.address}, ${it.city}" } ?: "Swahili Street, Kariakoo, Dar es Salaam"
    val shopPhone = businessInfo?.phone ?: "+255700000000"
    val shopLat = businessInfo?.latitude ?: -6.8182
    val shopLng = businessInfo?.longitude ?: 39.2778
    val isVerified = businessInfo?.verificationStatus == "VERIFIED" || businessId == 1L || businessId == 2L || businessId == 4L

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // TOP HEADER: BUSINESS STOREFRONT HEADER
        Surface(
            color = PrimaryDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClicked) {
                        Text("⬅️", fontSize = 20.sp, color = SurfaceWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shopName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                            if (isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Action
                                ) {
                                    Text(
                                        text = "VERIFIED ✓",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SurfaceWhite,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "📍 $shopAddress",
                            fontSize = 12.sp,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                        businessInfo?.description?.let { desc ->
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = PrimaryLight,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // STOREFRONT CONTACT & NAVIGATION ACTIONS BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val activeItemName = displayedProducts.firstOrNull()?.productName
                                ?: displayedServices.firstOrNull()?.serviceName
                                ?: "products"
                            val msg = "Hello $shopName, I found your shop on MTANAV and would like to order/inquire about $activeItemName."
                            locationNavigator.openWhatsApp(shopPhone, msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Action),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("💬 WHATSAPP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { locationNavigator.openPhoneCall(shopPhone) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("📞 CALL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { locationNavigator.launchGoogleMapsNavigation(shopLat, shopLng, shopName) },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("🗺️ NAVIGATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CONTEXT HEADER & VIEW ALL PRODUCTS ACTION
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val headerLabel = when {
                            selectedProductId != null && !showAllProducts -> "SELECTED PRODUCT CATALOGUE"
                            selectedServiceId != null && !showAllProducts -> "SELECTED SERVICE DETAILS"
                            else -> "FULL SHOP CATALOGUE"
                        }
                        Text(
                            text = headerLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryDark,
                            letterSpacing = 1.sp
                        )
                    }

                    if ((selectedProductId != null || selectedServiceId != null) && !showAllProducts) {
                        Button(
                            onClick = { showAllProducts = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("VIEW ALL PRODUCTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        }
                    }
                }
            }

            // DISPLAY SERVICES IF ANY
            if (displayedServices.isNotEmpty()) {
                item {
                    Text("SERVICES OFFERED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                }
                items(displayedServices) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                tz.mtanav.ui.components.AppThumbnail(
                                    imageUrl = null,
                                    fallbackIcon = service.serviceIcon ?: "⚙️",
                                    categoryType = "SERVICE",
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(service.serviceName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    service.serviceDescription?.let { Text(it, fontSize = 12.sp, color = TextSecondary) }
                                    Text("Duration: ${service.durationMinutes} mins", fontSize = 11.sp, color = Action)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TSh ${service.price.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Action)
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        val msg = "Hello $shopName, I would like to book service: ${service.serviceName} (TSh ${service.price.toInt()}) via MTANAV."
                                        locationNavigator.openWhatsApp(shopPhone, msg)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Action.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("BOOK SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Action)
                                }
                            }
                        }
                    }
                }
            }

            // DISPLAY PRODUCTS
            if (displayedProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PRODUCTS FOR SALE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                }
                items(displayedProducts) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                val icon = when {
                                    product.productName.contains("Charger", ignoreCase = true) -> "🔌"
                                    product.productName.contains("Phone", ignoreCase = true) -> "📱"
                                    product.productName.contains("Rice", ignoreCase = true) -> "🍚"
                                    product.productName.contains("Spoon", ignoreCase = true) -> "🥄"
                                    product.productName.contains("Shoe", ignoreCase = true) -> "👟"
                                    else -> "📦"
                                }

                                tz.mtanav.ui.components.AppThumbnail(
                                    imageUrl = product.imageUrl,
                                    fallbackIcon = icon,
                                    categoryType = "PRODUCT",
                                    modifier = Modifier.size(64.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = product.productName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (product.availabilityStatus == "IN_STOCK") Action.copy(alpha = 0.15f) else Accent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (product.availabilityStatus == "IN_STOCK") "In Stock" else "Available",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (product.availabilityStatus == "IN_STOCK") Action else PrimaryDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    product.productDescription?.let { desc ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = desc,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "From TSh ${product.price.toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Action
                                    )
                                }
                            }

                            if (product.variants.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Border)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "AVAILABLE VARIANTS & TYPES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                product.variants.forEach { variant ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Background,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { onProductSelected(product) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Primary.copy(alpha = 0.1f),
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("🛍️", fontSize = 14.sp)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = variant.name,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        text = "In stock: ${variant.availableQuantity} ${variant.unit}",
                                                        fontSize = 11.sp,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "TSh ${variant.price.toInt()}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Action
                                                )
                                                Text(
                                                    text = "ORDER ➔",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Primary
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onProductSelected(product) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("VIEW PRODUCT & ORDER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
