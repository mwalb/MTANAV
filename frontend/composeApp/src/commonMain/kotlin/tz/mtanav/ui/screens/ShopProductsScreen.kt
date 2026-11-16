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
import tz.mtanav.model.ProductVariantDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.components.AppThumbnail
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.ui.map.LocationNavigator

private fun formatTsh(amount: Double): String {
    val rounded = amount.toLong()
    val str = rounded.toString()
    val result = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        if (count > 0 && count % 3 == 0) {
            result.append(',')
        }
        result.append(str[i])
        count++
    }
    return "TSh ${result.reverse()}"
}

private fun getProductFallbackIcon(productName: String): String {
    val name = productName.lowercase()
    return when {
        name.contains("spoon") -> "🥄"
        name.contains("rice") -> "🍚"
        name.contains("charger") -> "🔌"
        name.contains("phone") -> "📱"
        name.contains("shoe") -> "👟"
        name.contains("cloth") || name.contains("shirt") -> "👕"
        else -> "📦"
    }
}

@Composable
fun ShopProductsScreen(
    businessId: Long,
    selectedProductId: Long? = null,
    selectedServiceId: Long? = null,
    repository: MtanavRepository,
    locationNavigator: LocationNavigator,
    onBackClicked: () -> Unit,
    onProductSelected: (BusinessProductDto) -> Unit,
    onMessageShopOwnerClicked: (Long) -> Unit = {},
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

    val shopName = businessInfo?.name ?: allProducts.firstOrNull()?.businessName ?: allServices.firstOrNull()?.businessName ?: "Vunjabei Store"
    val shopAddress = businessInfo?.let { "${it.address}, ${it.city}" } ?: "Swahili Street, Kariakoo, Dar es Salaam"
    val shopBuilding = businessInfo?.let {
        listOfNotNull(it.buildingName, it.floor?.let { f -> "Floor $f" }, it.shopNumber?.let { s -> "Shop $s" }).joinToString(", ")
    } ?: "Kariakoo Commercial Complex, Shop A-12"
    val shopPhone = businessInfo?.phone ?: "+255711000001"
    val shopLat = businessInfo?.latitude ?: -6.8182
    val shopLng = businessInfo?.longitude ?: 39.2778
    val rating = businessInfo?.rating ?: 4.8
    val reviewCount = businessInfo?.reviewCount ?: 42
    val isVerified = businessInfo?.verificationStatus == "VERIFIED" || businessId == 1L || businessId == 2L || businessId == 4L
    val supportsDelivery = businessInfo?.supportsDelivery ?: true
    val supportsPickup = businessInfo?.supportsPickup ?: true

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        val isDesktop = maxWidth >= 1024.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // TOP STOREFRONT NAVIGATION BAR
            Surface(
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClicked) {
                        Icon(MtanavIcons.ArrowBack, contentDescription = "Back", tint = SurfaceWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shopName,
                                fontSize = if (isDesktop) 20.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                            if (isVerified) {
                                Spacer(modifier = Modifier.width(8.dp))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(MtanavIcons.LocationOn, contentDescription = null, tint = SurfaceWhite.copy(alpha = 0.85f), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$shopAddress ($shopBuilding)",
                                fontSize = 11.sp,
                                color = SurfaceWhite.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Rating Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HighlightGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, HighlightGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("★", fontSize = 12.sp, color = HighlightGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$rating ($reviewCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                        }
                    }
                }
            }

            if (isDesktop) {
                // DESKTOP TWO-COLUMN LAYOUT (65% Left Catalogue, 35% Right Sticky Shop Info & Actions)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // LEFT COLUMN: Product & Wholesale Catalogue
                    Column(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                    ) {
                        CatalogueHeader(
                            selectedProductId = selectedProductId,
                            selectedServiceId = selectedServiceId,
                            showAllProducts = showAllProducts,
                            onShowAllClicked = { showAllProducts = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Render Services if available
                            if (displayedServices.isNotEmpty()) {
                                item {
                                    Text("SERVICES OFFERED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                                }
                                items(displayedServices) { service ->
                                    ServiceCard(
                                        service = service,
                                        shopName = shopName,
                                        shopPhone = shopPhone,
                                        locationNavigator = locationNavigator
                                    )
                                }
                            }

                            // Render Products with wholesale & retail variants
                            if (displayedProducts.isNotEmpty()) {
                                item {
                                    Text("PRODUCTS & WHOLESALE BULK PACKS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                                }
                                items(displayedProducts) { product ->
                                    ProductWholesaleCard(
                                        product = product,
                                        isFeatured = selectedProductId == product.productId,
                                        isDesktop = true,
                                        onProductSelected = onProductSelected
                                    )
                                }
                            }
                        }
                    }

                    // RIGHT COLUMN: Sticky Shop Trust & Action Card
                    Column(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("SHOP INFORMATION", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PrimaryDark, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Shop Front Photo Banner / Rounded Image
                                val storefrontPhotoUrl = businessInfo?.logoUrl?.ifBlank { null }
                                    ?: "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=600&q=80"

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    tz.mtanav.ui.components.AppImage(
                                        imageUrl = storefrontPhotoUrl,
                                        fallbackIcon = "🏪",
                                        categoryType = "BUSINESS",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column {
                                    Text(shopName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(shopAddress, fontSize = 12.sp, color = TextSecondary)
                                    Text(shopBuilding, fontSize = 11.sp, color = PrimaryDark)
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Border)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("FULFILLMENT & DELIVERY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (supportsDelivery) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Action.copy(alpha = 0.12f)
                                        ) {
                                            Text("🚚 Delivery Available", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Action, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                    if (supportsPickup) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Primary.copy(alpha = 0.12f)
                                        ) {
                                            Text("🏪 Store Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                                Text("DIRECT CONTACT ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Contact Actions Column for Desktop
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = { locationNavigator.openPhoneCall(shopPhone) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("📞 CALL SHOP OWNER ($shopPhone)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val activeItemName = displayedProducts.firstOrNull()?.productName
                                                ?: displayedServices.firstOrNull()?.serviceName ?: "products"
                                            val msg = "Hello $shopName, I found your shop on MTANAV and would like to order $activeItemName."
                                            locationNavigator.openWhatsApp(shopPhone, msg)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = tz.mtanav.ui.components.MtanavIcons.Phone,
                                                contentDescription = null,
                                                tint = SurfaceWhite,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("CHAT ON WHATSAPP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = { locationNavigator.launchGoogleMapsNavigation(shopLat, shopLng, shopName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = HighlightGold),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("MAP NAVIGATE TO SHOP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                                    }

                                    Button(
                                        onClick = { onMessageShopOwnerClicked(businessId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("✉️ IN-APP MESSAGE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // MOBILE LAYOUT (< 1024.dp) WITH STICKY FIXED BOTTOM CONTACT BAR
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            CatalogueHeader(
                                selectedProductId = selectedProductId,
                                selectedServiceId = selectedServiceId,
                                showAllProducts = showAllProducts,
                                onShowAllClicked = { showAllProducts = true }
                            )
                        }

                        // Display Services
                        if (displayedServices.isNotEmpty()) {
                            item {
                                Text("SERVICES OFFERED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                            }
                            items(displayedServices) { service ->
                                ServiceCard(
                                    service = service,
                                    shopName = shopName,
                                    shopPhone = shopPhone,
                                    locationNavigator = locationNavigator
                                )
                            }
                        }

                        // Display Products with Featured first, then secondary accordion
                        if (displayedProducts.isNotEmpty()) {
                            item {
                                Text("PRODUCTS & WHOLESALE VARIANTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                            }
                            items(displayedProducts) { product ->
                                ProductWholesaleCard(
                                    product = product,
                                    isFeatured = selectedProductId == product.productId || product == displayedProducts.first(),
                                    isDesktop = false,
                                    onProductSelected = onProductSelected
                                )
                            }
                        }
                    }

                    // FIXED BOTTOM ACTION BAR (4 Clear Action Buttons)
                    Surface(
                        color = SurfaceWhite,
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // 1. CALL
                            Button(
                                onClick = { locationNavigator.openPhoneCall(shopPhone) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 2.dp)
                            ) {
                                Text("📞 CALL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // 2. WHATSAPP
                            Button(
                                onClick = {
                                    val activeItemName = displayedProducts.firstOrNull()?.productName
                                        ?: displayedServices.firstOrNull()?.serviceName ?: "products"
                                    val msg = "Hello $shopName, I found your shop on MTANAV and would like to inquire about $activeItemName."
                                    locationNavigator.openWhatsApp(shopPhone, msg)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = tz.mtanav.ui.components.MtanavIcons.Phone,
                                        contentDescription = null,
                                        tint = SurfaceWhite,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WHATSAPP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 3. NAVIGATE
                            Button(
                                onClick = { locationNavigator.launchGoogleMapsNavigation(shopLat, shopLng, shopName) },
                                colors = ButtonDefaults.buttonColors(containerColor = HighlightGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 2.dp)
                            ) {
                                Text("MAP NAV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                            }

                            // 4. MESSAGE
                            Button(
                                onClick = { onMessageShopOwnerClicked(businessId) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 2.dp)
                            ) {
                                Text("✉️ MESSAGE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogueHeader(
    selectedProductId: Long?,
    selectedServiceId: Long?,
    showAllProducts: Boolean,
    onShowAllClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = when {
            selectedProductId != null && !showAllProducts -> "SELECTED PRODUCT & VARIANTS"
            selectedServiceId != null && !showAllProducts -> "SELECTED SERVICE DETAILS"
            else -> "FULL SHOP CATALOGUE"
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = PrimaryDark,
            letterSpacing = 1.sp
        )

        if ((selectedProductId != null || selectedServiceId != null) && !showAllProducts) {
            Button(
                onClick = onShowAllClicked,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("VIEW ALL SHOP ITEMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: BusinessServiceDto,
    shopName: String,
    shopPhone: String,
    locationNavigator: LocationNavigator
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AppThumbnail(
                    imageUrl = null,
                    fallbackIcon = service.serviceIcon ?: "⚙️",
                    categoryType = "SERVICE",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(service.serviceName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    service.serviceDescription?.let { Text(it, fontSize = 11.sp, color = TextSecondary) }
                    Text("Duration: ${service.durationMinutes} mins", fontSize = 11.sp, color = Action)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatTsh(service.price), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Action)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        val msg = "Hello $shopName, I would like to book service: ${service.serviceName} (${formatTsh(service.price)}) via MTANAV."
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

@Composable
private fun ProductWholesaleCard(
    product: BusinessProductDto,
    isFeatured: Boolean,
    isDesktop: Boolean,
    onProductSelected: (BusinessProductDto) -> Unit
) {
    val fallbackIcon = getProductFallbackIcon(product.productName)
    val basePrice = product.price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductSelected(product) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFeatured) 4.dp else 2.dp),
        border = if (isFeatured) BorderStroke(1.5.dp, Primary.copy(alpha = 0.4f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main Product Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AppThumbnail(
                    imageUrl = product.imageUrl,
                    fallbackIcon = fallbackIcon,
                    categoryType = "PRODUCT",
                    modifier = Modifier.size(if (isFeatured) 72.dp else 60.dp)
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
                            fontSize = if (isFeatured) 18.sp else 16.sp,
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
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "From ${formatTsh(basePrice)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Action
                        )

                        Button(
                            onClick = { onProductSelected(product) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("View Options & Details", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = MtanavIcons.ArrowForward,
                                    contentDescription = null,
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
