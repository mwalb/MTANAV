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

enum class OwnerSection(val title: String, val isHeaderAction: Boolean = false) {
    Dashboard("Dashboard"),
    PostProductService("Post Product/Service", isHeaderAction = true),
    Messages("Customer Messages"),
    MyProducts("My Products"),
    MyServices("My Services"),
    MyBusiness("My Business"),
    Jobs("Post Job / Manage Jobs"),
    Promotions("Promotions / Marketing"),
    Photos("Photos"),
    Orders("Orders"),
    Bookings("Bookings"),
    Payments("Payments"),
    Verification("Verification"),
    Settings("Settings")
}

@Composable
fun OwnerPortalScreen(
    repository: MtanavRepository,
    initialSection: OwnerSection = OwnerSection.Dashboard,
    targetConversationId: Long? = null,
    onNavigateToPublicMap: () -> Unit,
    onOpenPublicShop: ((Long) -> Unit)? = null,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(initialSection) }
    var myBusinesses by remember { mutableStateOf<List<BusinessDto>>(emptyList()) }
    var selectedBusiness by remember { mutableStateOf<BusinessDto?>(null) }
    var photoUsage by remember { mutableStateOf<PhotoUsageDto?>(null) }
    var orders by remember { mutableStateOf<List<OrderDto>>(emptyList()) }
    var bookings by remember { mutableStateOf<List<BookingDto>>(emptyList()) }
    var products by remember { mutableStateOf<List<BusinessProductDto>>(emptyList()) }
    var services by remember { mutableStateOf<List<BusinessServiceDto>>(emptyList()) }
    var conversations by remember { mutableStateOf<List<ConversationDto>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun loadOwnerData() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val bizList = repository.getMyBusinesses()
                myBusinesses = bizList
                val currentBiz = bizList.firstOrNull()
                selectedBusiness = currentBiz

                if (currentBiz != null) {
                    photoUsage = repository.getPhotoUsage(currentBiz.id)
                    orders = repository.getOwnerOrders(currentBiz.id)
                    bookings = repository.getOwnerBookings(currentBiz.id)
                    products = repository.getBusinessProducts(currentBiz.id)
                    services = repository.getBusinessServices(currentBiz.id)
                    conversations = repository.getConversations()
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load owner portal data."
            } finally {
                isLoading = false
            }
        }
    }

    fun updateOwnerBusinessProfile(name: String, tagline: String, address: String, phone: String, logoUrl: String) {
        coroutineScope.launch {
            selectedBusiness?.let { currentBiz ->
                try {
                    val updated = repository.updateBusinessProfile(
                        businessId = currentBiz.id,
                        name = name,
                        description = tagline,
                        address = address,
                        phone = phone,
                        logoUrl = logoUrl
                    )
                    selectedBusiness = updated
                    message = "Storefront profile updated successfully!"
                } catch (e: Exception) {
                    errorMessage = "Failed to update storefront profile."
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOwnerData()
    }

    var drawerOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        val screenWidth = maxWidth
        val isSmallOrMedium = screenWidth < 1024.dp
        val isMobile = screenWidth < 768.dp

        if (isSmallOrMedium) {
            Box(modifier = Modifier.fillMaxSize()) {
                // MAIN CONTENT AREA
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isMobile) 12.dp else 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Bar with Hamburger Trigger & Responsive Actions
                    if (isMobile) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { drawerOpen = !drawerOpen }) {
                                    Icon(
                                        imageVector = tz.mtanav.ui.components.MtanavIcons.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = PrimaryDeepBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = activeSection.title.uppercase(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryDeepBlue,
                                        letterSpacing = 0.5.sp
                                    )
                                    if (activeSection != OwnerSection.Dashboard) {
                                        selectedBusiness?.name?.let { bizName ->
                                            Text(
                                                text = bizName,
                                                fontSize = 11.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            // Mobile Stacked Action Buttons
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val link = "https://mtanav.tz/shop/${selectedBusiness?.id ?: 1}"
                                        message = "Shop link copied! Share with buyers: Check out my products on MTANAV: $link"
                                    },
                                    border = BorderStroke(1.dp, HighlightGold),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Share,
                                            contentDescription = null,
                                            tint = PrimaryDeepBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("SHARE SHOP LINK", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = PrimaryDeepBlue)
                                    }
                                }

                                Button(
                                    onClick = { activeSection = OwnerSection.PostProductService },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Add,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("POST PRODUCT OR SERVICE", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { drawerOpen = !drawerOpen }) {
                                    Icon(
                                        imageVector = tz.mtanav.ui.components.MtanavIcons.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = PrimaryDeepBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = activeSection.title.uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryDeepBlue,
                                        letterSpacing = 1.sp
                                    )
                                    if (activeSection != OwnerSection.Dashboard) {
                                        selectedBusiness?.name?.let { bizName ->
                                            Text(
                                                text = bizName,
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val link = "https://mtanav.tz/shop/${selectedBusiness?.id ?: 1}"
                                        message = "Shop link copied! Share with buyers: Check out my products on MTANAV: $link"
                                    },
                                    border = BorderStroke(1.dp, HighlightGold),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Share,
                                            contentDescription = null,
                                            tint = PrimaryDeepBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("SHARE LINK", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryDeepBlue)
                                    }
                                }

                                Button(
                                    onClick = { activeSection = OwnerSection.PostProductService },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Add,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("POST ITEM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
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

                    // RENDER ACTIVE SECTION CONTENT
                    renderOwnerSectionContent(
                        activeSection = activeSection,
                        selectedBusiness = selectedBusiness,
                        photoUsage = photoUsage,
                        orders = orders,
                        bookings = bookings,
                        products = products,
                        services = services,
                        unreadMessagesCount = conversations.sumOf { it.unreadCount.toInt() },
                        repository = repository,
                        targetConversationId = targetConversationId,
                        onSectionChange = { activeSection = it },
                        onRefreshData = { loadOwnerData() },
                        onMessageChanged = { msg -> message = msg },
                        onOpenPublicShop = onOpenPublicShop,
                        onUpdateBusinessProfile = { name, tagline, address, phone, logoUrl ->
                            updateOwnerBusinessProfile(name, tagline, address, phone, logoUrl)
                        }
                    )
                }

                // HAMBURGER NAVIGATION DRAWER OVERLAY
                if (drawerOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { drawerOpen = false }
                    )

                    Surface(
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                        color = SurfaceWhite,
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                            .clickable(enabled = false) {}
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PrimaryDeepBlue)
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "OWNER MENU",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite,
                                    letterSpacing = 1.sp
                                )
                                IconButton(onClick = { drawerOpen = false }) {
                                    Icon(
                                        imageVector = tz.mtanav.ui.components.MtanavIcons.Close,
                                        contentDescription = "Close Menu",
                                        tint = SurfaceWhite
                                    )
                                }
                            }

                            OwnerSidebar(
                                activeSection = activeSection,
                                business = selectedBusiness,
                                onSectionSelected = { section ->
                                    activeSection = section
                                    drawerOpen = false
                                },
                                onNavigateToPublicMap = {
                                    drawerOpen = false
                                    onNavigateToPublicMap()
                                },
                                onLogoutClicked = {
                                    drawerOpen = false
                                    onLogoutClicked()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        } else {
            // DESKTOP LAYOUT (>= 1024dp)
            Row(modifier = Modifier.fillMaxSize()) {
                OwnerSidebar(
                    activeSection = activeSection,
                    business = selectedBusiness,
                    onSectionSelected = { activeSection = it },
                    onNavigateToPublicMap = onNavigateToPublicMap,
                    onLogoutClicked = onLogoutClicked,
                    modifier = Modifier.width(260.dp).fillMaxHeight()
                )

                VerticalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.fillMaxHeight().width(1.dp))

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
                                if (activeSection != OwnerSection.Dashboard) {
                                    selectedBusiness?.name?.let { bizName ->
                                        Text(
                                            text = bizName,
                                            fontSize = 13.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val link = "https://mtanav.tz/shop/${selectedBusiness?.id ?: 1}"
                                        message = "Shop link copied to clipboard! Share on WhatsApp: Check out my products and prices on MTANAV: $link"
                                    },
                                    border = BorderStroke(1.5.dp, HighlightGold),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Share,
                                            contentDescription = null,
                                            tint = PrimaryDeepBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("SHARE SHOP LINK", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryDeepBlue)
                                    }
                                }

                                Button(
                                    onClick = { activeSection = OwnerSection.PostProductService },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = tz.mtanav.ui.components.MtanavIcons.Add,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("POST PRODUCT OR SERVICE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
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

                        renderOwnerSectionContent(
                            activeSection = activeSection,
                            selectedBusiness = selectedBusiness,
                            photoUsage = photoUsage,
                            orders = orders,
                            bookings = bookings,
                            products = products,
                            services = services,
                            unreadMessagesCount = conversations.sumOf { it.unreadCount.toInt() },
                            repository = repository,
                            targetConversationId = targetConversationId,
                            onSectionChange = { activeSection = it },
                            onRefreshData = { loadOwnerData() },
                            onMessageChanged = { msg -> message = msg },
                            onOpenPublicShop = onOpenPublicShop,
                            onUpdateBusinessProfile = { name, tagline, address, phone, logoUrl ->
                                updateOwnerBusinessProfile(name, tagline, address, phone, logoUrl)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun renderOwnerSectionContent(
    activeSection: OwnerSection,
    selectedBusiness: BusinessDto?,
    photoUsage: PhotoUsageDto?,
    orders: List<OrderDto>,
    bookings: List<BookingDto>,
    products: List<BusinessProductDto>,
    services: List<BusinessServiceDto>,
    unreadMessagesCount: Int,
    repository: MtanavRepository,
    targetConversationId: Long?,
    onSectionChange: (OwnerSection) -> Unit,
    onRefreshData: () -> Unit,
    onMessageChanged: (String?) -> Unit,
    onOpenPublicShop: ((Long) -> Unit)? = null,
    onUpdateBusinessProfile: (String, String, String, String, String) -> Unit
) {
    when (activeSection) {
        OwnerSection.Dashboard -> OwnerDashboardContent(
            business = selectedBusiness,
            productsCount = products.size,
            servicesCount = services.size,
            ordersCount = orders.size,
            bookingsCount = bookings.size,
            unreadMessagesCount = unreadMessagesCount,
            onNavigateSection = onSectionChange,
            onOpenPublicShop = onOpenPublicShop,
            onUpdateBusinessProfile = onUpdateBusinessProfile
        )

        OwnerSection.Messages -> OwnerMessagesContent(
            repository = repository,
            targetConversationId = targetConversationId
        )

        OwnerSection.MyProducts -> OwnerProductsContent(
            products = products,
            business = selectedBusiness,
            onSectionChange = onSectionChange
        )

        OwnerSection.MyServices -> OwnerServicesContent(
            services = services,
            business = selectedBusiness,
            onSectionChange = onSectionChange
        )

        OwnerSection.MyBusiness -> OwnerBusinessDetailsContent(
            business = selectedBusiness,
            productsCount = products.size,
            servicesCount = services.size
        )

        OwnerSection.Jobs -> OwnerJobsContent(
            business = selectedBusiness,
            repository = repository
        )

        OwnerSection.Promotions -> OwnerPromotionsContent(
            business = selectedBusiness,
            products = products,
            services = services,
            repository = repository
        )

        OwnerSection.Photos -> OwnerPhotosContent(
            business = selectedBusiness,
            photoUsage = photoUsage,
            repository = repository,
            onPhotosUpdated = onRefreshData
        )

        OwnerSection.Orders -> OwnerOrdersContent(
            orders = orders,
            repository = repository,
            onStatusChanged = onRefreshData
        )

        OwnerSection.Bookings -> OwnerBookingsContent(
            bookings = bookings,
            repository = repository,
            onStatusChanged = onRefreshData
        )

        OwnerSection.Payments -> OwnerPaymentsContent(orders = orders, bookings = bookings)

        OwnerSection.Verification -> OwnerVerificationContent(business = selectedBusiness)

        OwnerSection.Settings -> OwnerSettingsContent()

        OwnerSection.PostProductService -> OwnerPostProductServiceContent(
            business = selectedBusiness,
            repository = repository,
            onSuccess = {
                onMessageChanged("Item posted successfully! It is now live for buyers on MTANAV.")
                onRefreshData()
                onSectionChange(OwnerSection.MyProducts)
            }
        )
    }
}

@Composable
private fun OwnerSidebar(
    activeSection: OwnerSection,
    business: BusinessDto?,
    onSectionSelected: (OwnerSection) -> Unit,
    onNavigateToPublicMap: () -> Unit,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(SurfaceWhite)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(colors = listOf(PrimaryDeepBlue, PrimaryBlue)))
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Text("MTANAV", fontSize = 24.sp, fontWeight = FontWeight.Black, color = SurfaceWhite, letterSpacing = 1.2.sp)
            Text("BUSINESS OWNER PORTAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HighlightGold, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            business?.let {
                Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite.copy(alpha = 0.15f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = tz.mtanav.ui.components.MtanavIcons.Storefront,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(it.name, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OwnerSection.entries.forEach { section ->
                val isSelected = activeSection == section
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) PrimaryDeepBlue else Color.Transparent,
                    modifier = Modifier.fillMaxWidth().clickable { onSectionSelected(section) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
                    ) {
                        Text(
                            text = section.title,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SurfaceWhite else TextPrimary
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))

        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onNavigateToPublicMap,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                border = BorderStroke(1.dp, PrimaryDeepBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = tz.mtanav.ui.components.MtanavIcons.Map,
                        contentDescription = null,
                        tint = PrimaryDeepBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Public Map Discovery", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                }
            }

            TextButton(onClick = onLogoutClicked, modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = tz.mtanav.ui.components.MtanavIcons.ExitToApp,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign out", color = ErrorRed, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OwnerDashboardContent(
    business: BusinessDto?,
    productsCount: Int,
    servicesCount: Int,
    ordersCount: Int,
    bookingsCount: Int,
    unreadMessagesCount: Int = 0,
    onNavigateSection: (OwnerSection) -> Unit,
    onOpenPublicShop: ((Long) -> Unit)? = null,
    onUpdateBusinessProfile: (String, String, String, String, String) -> Unit
) {
    var shopName by remember(business) { mutableStateOf(business?.name ?: "") }
    var shopTagline by remember(business) { mutableStateOf(business?.description ?: "") }
    var shopAddress by remember(business) { mutableStateOf(business?.address ?: "") }
    var shopPhone by remember(business) { mutableStateOf(business?.phone ?: "") }
    var shopLogoUrl by remember(business) { mutableStateOf(business?.logoUrl ?: "") }
    var isSaveSuccess by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // STOREFRONT PROFILE & CUSTOMER PREVIEW CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header with Merchant Name & Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STOREFRONT PROFILE & CUSTOMER PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDeepBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (shopName.isNotBlank()) shopName else "Set Your Shop Name",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        if (shopTagline.isNotBlank()) {
                            Text(
                                text = shopTagline,
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryLight
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            val isVerified = business?.verificationStatus == "VERIFIED"
                            Icon(
                                imageVector = if (isVerified) tz.mtanav.ui.components.MtanavIcons.CheckCircle else tz.mtanav.ui.components.MtanavIcons.Storefront,
                                contentDescription = null,
                                tint = PrimaryDeepBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isVerified) "VERIFIED SHOP" else "ACTIVE MERCHANT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDeepBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PROMINENT "PREVIEW MY SHOP AS CUSTOMER" ACTION BANNER
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryDeepBlue.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, PrimaryDeepBlue.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Public Customer Storefront View",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDeepBlue
                            )
                            Text(
                                text = "Inspect how buyers view your products, services, address, and direct WhatsApp contact buttons.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val bizId = business?.id ?: 1L
                                if (onOpenPublicShop != null) {
                                    onOpenPublicShop(bizId)
                                } else {
                                    onNavigateSection(OwnerSection.MyBusiness)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HighlightGold, contentColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tz.mtanav.ui.components.MtanavIcons.Storefront,
                                    contentDescription = null,
                                    tint = PrimaryDeepBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Preview My Shop as Customer",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                // EDITABLE STOREFRONT FIELDS
                Text(
                    text = "CUSTOMIZE STOREFRONT DETAILS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    BoxWithConstraints {
                        if (maxWidth < 640.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("SHOP / BUSINESS NAME", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopName,
                                        onValueChange = { shopName = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Vunjabel Household Shop", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("SHOP CATEGORY & TAGLINE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopTagline,
                                        onValueChange = { shopTagline = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Wholesale & Retail Kitchenware — Kariakoo", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SHOP / BUSINESS NAME", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopName,
                                        onValueChange = { shopName = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Vunjabel Household Shop", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SHOP CATEGORY & TAGLINE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopTagline,
                                        onValueChange = { shopTagline = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Wholesale & Retail Kitchenware — Kariakoo", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        }
                    }

                    BoxWithConstraints {
                        if (maxWidth < 640.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("STREET & LANDMARK ADDRESS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopAddress,
                                        onValueChange = { shopAddress = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Swahili Street, near Kariakoo Market", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("DIRECT CONTACT / WHATSAPP NUMBER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopPhone,
                                        onValueChange = { shopPhone = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. +255 700 000 000", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("STREET & LANDMARK ADDRESS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopAddress,
                                        onValueChange = { shopAddress = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. Swahili Street, near Kariakoo Market", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("DIRECT CONTACT / WHATSAPP NUMBER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = shopPhone,
                                        onValueChange = { shopPhone = it; isSaveSuccess = false },
                                        placeholder = { Text("e.g. +255 700 000 000", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("STOREFRONT BANNER / LOGO URL", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = shopLogoUrl,
                            onValueChange = { shopLogoUrl = it; isSaveSuccess = false },
                            placeholder = { Text("e.g. https://images.unsplash.com/... or paste image URL", fontSize = 12.5.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryDeepBlue, unfocusedBorderColor = Color(0xFFCBD5E1))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSaveSuccess) {
                            Text(
                                text = "Storefront profile saved! ✓",
                                color = SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                onUpdateBusinessProfile(shopName, shopTagline, shopAddress, shopPhone, shopLogoUrl)
                                isSaveSuccess = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(tz.mtanav.ui.components.MtanavIcons.Check, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Profile Changes", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 5 DASHBOARD METRIC CARDS (RESPONSIVE GRID)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "REVENUE & ACTIVITY SUMMARY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val containerWidth = maxWidth
                if (containerWidth < 640.dp) {
                    // Mobile (< 640px): 1 Column
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OwnerStatCard(
                            title = "PRODUCTS",
                            value = "$productsCount items",
                            subtitle = "Listed for sale",
                            color = ActionTeal,
                            actionLabel = "+ Add Product",
                            onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateSection(OwnerSection.MyProducts) }
                        )
                        OwnerStatCard(
                            title = "SERVICES",
                            value = "$servicesCount services",
                            subtitle = "Available for booking",
                            color = PrimaryBlue,
                            actionLabel = "+ Add Service",
                            onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateSection(OwnerSection.MyServices) }
                        )
                        OwnerStatCard(
                            title = "ORDERS",
                            value = "$ordersCount pending",
                            subtitle = "Product orders",
                            color = PrimaryDeepBlue,
                            actionLabel = "View Orders",
                            onActionClicked = { onNavigateSection(OwnerSection.Orders) },
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateSection(OwnerSection.Orders) }
                        )
                        OwnerStatCard(
                            title = "BOOKINGS",
                            value = "$bookingsCount scheduled",
                            subtitle = "Service appointments",
                            color = ActionTeal,
                            actionLabel = "View Bookings",
                            onActionClicked = { onNavigateSection(OwnerSection.Bookings) },
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateSection(OwnerSection.Bookings) }
                        )
                        OwnerStatCard(
                            title = "MESSAGES",
                            value = if (unreadMessagesCount > 0) "$unreadMessagesCount unread" else "Active Chat",
                            subtitle = "Customer inquiries",
                            color = PrimaryDeepBlue,
                            actionLabel = "Open Chat",
                            onActionClicked = { onNavigateSection(OwnerSection.Messages) },
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateSection(OwnerSection.Messages) }
                        )
                    }
                } else if (containerWidth < 1024.dp) {
                    // Tablet (640px to 1024px): 2 Columns
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OwnerStatCard(
                                title = "PRODUCTS",
                                value = "$productsCount items",
                                subtitle = "Listed for sale",
                                color = ActionTeal,
                                actionLabel = "+ Add Product",
                                onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                                modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyProducts) }
                            )
                            OwnerStatCard(
                                title = "SERVICES",
                                value = "$servicesCount services",
                                subtitle = "Available for booking",
                                color = PrimaryBlue,
                                actionLabel = "+ Add Service",
                                onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                                modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyServices) }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OwnerStatCard(
                                title = "ORDERS",
                                value = "$ordersCount pending",
                                subtitle = "Product orders",
                                color = PrimaryDeepBlue,
                                actionLabel = "View Orders",
                                onActionClicked = { onNavigateSection(OwnerSection.Orders) },
                                modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Orders) }
                            )
                            OwnerStatCard(
                                title = "BOOKINGS",
                                value = "$bookingsCount scheduled",
                                subtitle = "Service appointments",
                                color = ActionTeal,
                                actionLabel = "View Bookings",
                                onActionClicked = { onNavigateSection(OwnerSection.Bookings) },
                                modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Bookings) }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OwnerStatCard(
                                title = "MESSAGES",
                                value = if (unreadMessagesCount > 0) "$unreadMessagesCount unread" else "Active Chat",
                                subtitle = "Customer inquiries",
                                color = PrimaryDeepBlue,
                                actionLabel = "Open Chat",
                                onActionClicked = { onNavigateSection(OwnerSection.Messages) },
                                modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Messages) }
                            )
                        }
                    }
                } else {
                    // Desktop (>= 1024px): 5 Columns Full Horizontal Alignment
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OwnerStatCard(
                            title = "PRODUCTS",
                            value = "$productsCount items",
                            subtitle = "Listed for sale",
                            color = ActionTeal,
                            actionLabel = "+ Add Product",
                            onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                            modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyProducts) }
                        )
                        OwnerStatCard(
                            title = "SERVICES",
                            value = "$servicesCount services",
                            subtitle = "Available for booking",
                            color = PrimaryBlue,
                            actionLabel = "+ Add Service",
                            onActionClicked = { onNavigateSection(OwnerSection.PostProductService) },
                            modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyServices) }
                        )
                        OwnerStatCard(
                            title = "ORDERS",
                            value = "$ordersCount pending",
                            subtitle = "Product orders",
                            color = PrimaryDeepBlue,
                            actionLabel = "View Orders",
                            onActionClicked = { onNavigateSection(OwnerSection.Orders) },
                            modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Orders) }
                        )
                        OwnerStatCard(
                            title = "BOOKINGS",
                            value = "$bookingsCount scheduled",
                            subtitle = "Service appointments",
                            color = ActionTeal,
                            actionLabel = "View Bookings",
                            onActionClicked = { onNavigateSection(OwnerSection.Bookings) },
                            modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Bookings) }
                        )
                        OwnerStatCard(
                            title = "MESSAGES",
                            value = if (unreadMessagesCount > 0) "$unreadMessagesCount unread" else "Active Chat",
                            subtitle = "Customer inquiries",
                            color = PrimaryDeepBlue,
                            actionLabel = "Open Chat",
                            onActionClicked = { onNavigateSection(OwnerSection.Messages) },
                            modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Messages) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    actionLabel: String? = null,
    onActionClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = color)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }

            if (actionLabel != null && onActionClicked != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onActionClicked,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(22.dp)
                ) {
                    Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighlightGold)
                }
            }
        }
    }
}

// ==========================================
// OWNER JOBS MANAGEMENT
// ==========================================

@Composable
private fun OwnerJobsContent(business: BusinessDto?, repository: MtanavRepository) {
    var ownerJobs by remember { mutableStateOf<List<JobDto>>(emptyList()) }
    var selectedJobForApps by remember { mutableStateOf<JobDto?>(null) }
    var applicationsForJob by remember { mutableStateOf<List<JobApplicationDto>>(emptyList()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Delivery and logistics") }
    var descInput by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf(business?.address ?: "Dar es Salaam") }
    var salaryInput by remember { mutableStateOf("350000") }
    var qualificationsInput by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun loadJobs() {
        coroutineScope.launch {
            isLoading = true
            try {
                ownerJobs = repository.getOwnerJobs()
            } catch (e: Exception) { /* ignored */ }
            finally { isLoading = false }
        }
    }

    fun loadAppsForJob(j: JobDto) {
        coroutineScope.launch {
            selectedJobForApps = j
            try {
                applicationsForJob = repository.getJobApplicationsForJob(j.id)
            } catch (e: Exception) { /* ignored */ }
        }
    }

    LaunchedEffect(Unit) {
        loadJobs()
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("POSTED JOBS MANAGEMENT (${ownerJobs.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Action),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tz.mtanav.ui.components.MtanavIcons.Add, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post New Job", fontWeight = FontWeight.Bold)
                }
            }
        }

        message?.let {
            Text(it, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        if (isLoading) {
            CircularProgressIndicator(color = Primary)
        } else if (ownerJobs.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💼", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No jobs posted yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Recruit local workers or drivers by clicking 'Post New Job'.", fontSize = 12.5.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isDesktop = maxWidth >= 768.dp
                val chunks = if (isDesktop) ownerJobs.chunked(2) else ownerJobs.map { listOf(it) }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    chunks.forEach { rowJobs ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            rowJobs.forEach { job ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                                Text("${job.category} • ${job.location}", fontSize = 12.sp, color = TextSecondary)
                                                Text("Budget: TSh ${job.budgetAmount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Action)
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (job.publicationStatus == "PUBLISHED") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                                            ) {
                                                Text(job.publicationStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (job.publicationStatus == "PUBLISHED") SuccessGreen else ErrorRed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { loadAppsForJob(job) },
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Applications", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        val nextStatus = if (job.publicationStatus == "PUBLISHED") "PAUSED" else "PUBLISHED"
                                                        repository.updateJobStatus(job.id, nextStatus)
                                                        loadJobs()
                                                    }
                                                },
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(if (job.publicationStatus == "PUBLISHED") "Pause" else "Publish", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                            if (isDesktop && rowJobs.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // VIEW APPLICATIONS FOR JOB MODAL
        selectedJobForApps?.let { currentJob ->
            AlertDialog(
                onDismissRequest = { selectedJobForApps = null },
                title = { Text("Applications for: ${currentJob.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        if (applicationsForJob.isEmpty()) {
                            Text("No applications received yet for this job position.", fontSize = 13.sp, color = TextSecondary)
                        } else {
                            applicationsForJob.forEach { app ->
                                Card(colors = CardDefaults.cardColors(containerColor = Background), modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(app.applicantName ?: "Applicant", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Email: ${app.applicantEmail} • Phone: ${app.applicantPhone}", fontSize = 11.sp, color = TextSecondary)
                                        Text("Proposed Rate: TSh ${app.proposedRate.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Action)
                                        Text("Cover Note: ${app.coverNote ?: "None"}", fontSize = 12.sp)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            listOf("SHORTLISTED", "ACCEPTED", "REJECTED").forEach { st ->
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            repository.updateJobApplicationStatus(currentJob.id, app.id, st)
                                                            loadAppsForJob(currentJob)
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = when (st) {
                                                            "ACCEPTED" -> SuccessGreen
                                                            "REJECTED" -> ErrorRed
                                                            else -> PrimaryBlue
                                                        }
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(st, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedJobForApps = null }) {
                        Text("Close")
                    }
                }
            )
        }

        // CREATE JOB MODAL
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Post New Job Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Job Title") }, placeholder = { Text("e.g. Delivery Motorbike Driver") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("Category") }, placeholder = { Text("e.g. Delivery & Logistics") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Description") }, placeholder = { Text("e.g. Responsible for delivering customer orders around Kariakoo & Kinondoni.") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = locationInput, onValueChange = { locationInput = it }, label = { Text("Location") }, placeholder = { Text("e.g. Kariakoo, Dar es Salaam") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = salaryInput, onValueChange = { salaryInput = it }, label = { Text("Monthly Budget / Salary (TZS)") }, placeholder = { Text("e.g. 350000") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qualificationsInput, onValueChange = { qualificationsInput = it }, label = { Text("Required Qualifications") }, placeholder = { Text("e.g. Valid Class A driving license") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.createJob(
                                    CreateJobRequest(
                                        title = titleInput,
                                        category = categoryInput,
                                        description = descInput,
                                        location = locationInput,
                                        budgetAmount = salaryInput.toDoubleOrNull() ?: 350000.0,
                                        businessId = business?.id,
                                        qualifications = qualificationsInput
                                    )
                                )
                                message = "Job posted successfully!"
                                showCreateDialog = false
                                loadJobs()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Action)
                    ) {
                        Text("PUBLISH JOB", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// ==========================================
// OWNER PROMOTIONS MANAGEMENT
// ==========================================

@Composable
private fun OwnerPromotionsContent(
    business: BusinessDto?,
    products: List<BusinessProductDto>,
    services: List<BusinessServiceDto>,
    repository: MtanavRepository
) {
    var ownerPromos by remember { mutableStateOf<List<PromotionDto>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Household") }
    var origPriceInput by remember { mutableStateOf("15000") }
    var promoPriceInput by remember { mutableStateOf("12000") }
    var termsInput by remember { mutableStateOf("Valid while stocks last.") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun loadPromos() {
        coroutineScope.launch {
            isLoading = true
            try {
                ownerPromos = repository.getOwnerPromotions()
            } catch (e: Exception) { /* ignored */ }
            finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) {
        loadPromos()
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PROMOTIONAL CAMPAIGNS (${ownerPromos.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Action),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tz.mtanav.ui.components.MtanavIcons.Add, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Promotion", fontWeight = FontWeight.Bold)
                }
            }
        }

        message?.let {
            Text(it, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        if (isLoading) {
            CircularProgressIndicator(color = Primary)
        } else if (ownerPromos.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏷️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No promotional campaigns active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Create discounts to attract nearby customers instantly!", fontSize = 12.5.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            // RESPONSIVE 2-COLUMN GRID ON DESKTOP (>= 768dp)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isDesktop = maxWidth >= 768.dp
                val chunks = if (isDesktop) ownerPromos.chunked(2) else ownerPromos.map { listOf(it) }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    chunks.forEach { rowPromos ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            rowPromos.forEach { promo ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(promo.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                                Text("Category: ${promo.category} • ${promo.offerType}", fontSize = 11.5.sp, color = TextSecondary)
                                                promo.promotionalPrice?.let {
                                                    Text("Offer: TSh ${it.toInt()} (${promo.discountPercentage ?: 0}% OFF)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Action)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (promo.status == "ACTIVE") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                                            ) {
                                                Text(promo.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (promo.status == "ACTIVE") SuccessGreen else ErrorRed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    val next = if (promo.status == "ACTIVE") "PAUSED" else "ACTIVE"
                                                    repository.updatePromotionStatus(promo.id, next)
                                                    loadPromos()
                                                }
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (promo.status == "ACTIVE") "Pause Campaign" else "Activate Campaign", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                            if (isDesktop && rowPromos.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create Promotion Campaign", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Promotion Title") }, placeholder = { Text("e.g. Special Discount on Super Rice") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Description") }, placeholder = { Text("e.g. Get 20% discount on orders over TSh 50,000 this week.") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("Category") }, placeholder = { Text("e.g. Groceries") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = origPriceInput, onValueChange = { origPriceInput = it }, label = { Text("Original Price (TZS)") }, placeholder = { Text("e.g. 15,000") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = promoPriceInput, onValueChange = { promoPriceInput = it }, label = { Text("Promotional Price (TZS)") }, placeholder = { Text("e.g. 12,000") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = termsInput, onValueChange = { termsInput = it }, label = { Text("Terms & Conditions") }, placeholder = { Text("e.g. Valid while stocks last.") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                business?.let { b ->
                                    repository.createPromotion(
                                        CreatePromotionRequest(
                                            businessId = b.id,
                                            title = titleInput,
                                            description = descInput,
                                            category = categoryInput,
                                            originalPrice = origPriceInput.toDoubleOrNull(),
                                            promotionalPrice = promoPriceInput.toDoubleOrNull(),
                                            termsAndConditions = termsInput,
                                            isSponsored = true
                                        )
                                    )
                                    message = "Promotion created successfully!"
                                    showCreateDialog = false
                                    loadPromos()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Action)
                    ) {
                        Text("PUBLISH PROMOTION", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// ==========================================
// OTHER OWNER CONTENT SECTIONS & EMPTY STATES
// ==========================================

@Composable
private fun OwnerProductsContent(
    products: List<BusinessProductDto>,
    business: BusinessDto?,
    onSectionChange: (OwnerSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MY PRODUCTS (${products.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { onSectionChange(OwnerSection.PostProductService) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tz.mtanav.ui.components.MtanavIcons.Add, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Add Product", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }
        }

        if (products.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📦", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No products listed yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Get discovered by local buyers instantly! List your inventory with prices and stock levels.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(max = 420.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { onSectionChange(OwnerSection.PostProductService) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text("+ Add First Product", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isDesktop = maxWidth >= 768.dp
                val chunks = if (isDesktop) products.chunked(2) else products.map { listOf(it) }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chunks.forEach { rowProducts ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            rowProducts.forEach { p ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(p.productName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("SKU: ${p.sku ?: "MTN-${p.id}"} • Available: ${p.availableQuantity}", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        Text("TSh ${p.price.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 15.sp)
                                    }
                                }
                            }
                            if (isDesktop && rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerServicesContent(
    services: List<BusinessServiceDto>,
    business: BusinessDto?,
    onSectionChange: (OwnerSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MY SERVICES (${services.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { onSectionChange(OwnerSection.PostProductService) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tz.mtanav.ui.components.MtanavIcons.Add, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Add Service", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }
        }

        if (services.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚙️", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No services listed yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Allow local customers in your city to discover and book your services online! Add your service packages now.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(max = 420.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { onSectionChange(OwnerSection.PostProductService) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text("+ Add First Service", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isDesktop = maxWidth >= 768.dp
                val chunks = if (isDesktop) services.chunked(2) else services.map { listOf(it) }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chunks.forEach { rowServices ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            rowServices.forEach { s ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(s.serviceName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Duration: ${s.durationMinutes} mins", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        Text("TSh ${s.price.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 15.sp)
                                    }
                                }
                            }
                            if (isDesktop && rowServices.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun OwnerBusinessDetailsContent(
    business: BusinessDto?,
    productsCount: Int = 0,
    servicesCount: Int = 0
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // PROFILE COMPLETION BANNER
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("SHOP PROFILE DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text(business?.name ?: "My Shop / Service Yard", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (business?.verificationStatus == "VERIFIED") SuccessGreen.copy(alpha = 0.15f) else HighlightGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (business?.verificationStatus == "VERIFIED") "✓ VERIFIED SHOP" else "⏳ VERIFICATION PENDING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (business?.verificationStatus == "VERIFIED") SuccessGreen else PrimaryDeepBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Street Address: ${business?.address ?: "Kariakoo Street, Dar es Salaam"}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("• City: ${business?.city ?: "Dar es Salaam"}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("• Contact Phone: ${business?.phone ?: "+255 700 000 000"}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("• Inventory Listed: $productsCount products, $servicesCount services", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable private fun OwnerPhotosContent(business: BusinessDto?, photoUsage: PhotoUsageDto?, repository: MtanavRepository, onPhotosUpdated: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("PHOTO ALLOWANCE & MANAGEMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Current Photos Uploaded: ${photoUsage?.currentPhotoCount ?: 1} / ${photoUsage?.maxAllowedPhotos ?: 5}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("High-quality photos increase buyer inquiries on MTANAV by up to 3x.", fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable private fun OwnerOrdersContent(orders: List<OrderDto>, repository: MtanavRepository, onStatusChanged: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("CUSTOMER ORDERS (${orders.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (orders.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(28.dp).fillMaxWidth()) {
                    Text("No customer orders received yet. Share your shop link to start selling!", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            orders.forEach { ord ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Order #${ord.id} • ${ord.customerName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("TSh ${ord.totalAmount.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal)
                        }
                        Text("Status: ${ord.orderStatus}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable private fun OwnerBookingsContent(bookings: List<BookingDto>, repository: MtanavRepository, onStatusChanged: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("SERVICE BOOKINGS (${bookings.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (bookings.isEmpty()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(28.dp).fillMaxWidth()) {
                    Text("No service bookings received yet.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            bookings.forEach { b ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${b.serviceName} • ${b.customerName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Date: ${b.bookingDate} at ${b.bookingTime}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable private fun OwnerPaymentsContent(orders: List<OrderDto>, bookings: List<BookingDto>) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("OWNER PAYMENTS & REVENUE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Sales Revenue: TSh ${(orders.sumOf { it.totalAmount } + bookings.sumOf { it.price }).toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ActionTeal)
        }
    }
}

@Composable private fun OwnerVerificationContent(business: BusinessDto?) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("VERIFICATION STATUS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Status: ${business?.verificationStatus ?: "VERIFIED"}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ActionTeal)
            Text("Verified badges build buyer trust on the public map.", fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable private fun OwnerSettingsContent() {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("BUSINESS SETTINGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Automatic Order Confirmation: Enabled", fontSize = 13.sp)
            Text("• WhatsApp Order Notifications: Active", fontSize = 13.sp)
        }
    }
}

@Composable private fun OwnerPostProductServiceContent(
    business: BusinessDto?,
    repository: MtanavRepository,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("50") }
    var itemType by remember { mutableStateOf("PRODUCT") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().widthIn(max = 680.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("POST NEW ITEM TO MTANAV", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PrimaryDeepBlue)
                    Text("Fill in item details to list on the public map and shop catalog.", fontSize = 12.5.sp, color = TextSecondary)
                }

                // ITEM TYPE SELECTOR
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { itemType = "PRODUCT" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (itemType == "PRODUCT") PrimaryDeepBlue else AppBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📦 Physical Product", color = if (itemType == "PRODUCT") SurfaceWhite else TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { itemType = "SERVICE" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (itemType == "SERVICE") PrimaryDeepBlue else AppBackground),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚙️ Service Package", color = if (itemType == "SERVICE") SurfaceWhite else TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // 2-COLUMN INPUT GRID FOR DESKTOP/TABLET
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val isTwoColumn = maxWidth >= 540.dp

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (isTwoColumn) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Item Name") },
                                    placeholder = { Text(if (itemType == "PRODUCT") "e.g., Kyela Super Rice - 25kg Bag" else "e.g., Engine Oil Change Service") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = category,
                                    onValueChange = { category = it },
                                    label = { Text("Category") },
                                    placeholder = { Text(if (itemType == "PRODUCT") "e.g., Cereals & Foodstuffs" else "e.g., Auto Maintenance") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = priceText,
                                    onValueChange = { priceText = it },
                                    label = { Text("Price (TZS)") },
                                    placeholder = { Text("e.g., 35,000") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = stockText,
                                    onValueChange = { stockText = it },
                                    label = { Text(if (itemType == "PRODUCT") "Available Stock Quantity" else "Duration (Minutes)") },
                                    placeholder = { Text(if (itemType == "PRODUCT") "e.g., 50" else "e.g., 45") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Item Name") },
                                placeholder = { Text(if (itemType == "PRODUCT") "e.g., Kyela Super Rice - 25kg Bag" else "e.g., Engine Oil Change Service") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category") },
                                placeholder = { Text(if (itemType == "PRODUCT") "e.g., Cereals & Foodstuffs" else "e.g., Auto Maintenance") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = { priceText = it },
                                label = { Text("Price (TZS)") },
                                placeholder = { Text("e.g., 35,000") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = stockText,
                                onValueChange = { stockText = it },
                                label = { Text(if (itemType == "PRODUCT") "Available Stock Quantity" else "Duration (Minutes)") },
                                placeholder = { Text(if (itemType == "PRODUCT") "e.g., 50" else "e.g., 45") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Description & Buyer Instructions") },
                            placeholder = { Text("e.g., Premium quality unpolished Kyela rice directly sourced from Mbeya farmers.") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            business?.let { b ->
                                val parsedPrice = priceText.replace(",", "").toDoubleOrNull() ?: 10000.0
                                val parsedQuantity = stockText.toIntOrNull() ?: 50
                                if (itemType == "PRODUCT") {
                                    repository.createProduct(b.id, b.name, name.ifBlank { "New Product Item" }, desc, parsedPrice, parsedQuantity, emptyList())
                                } else {
                                    repository.createService(b.id, b.name, name.ifBlank { "New Service Package" }, desc, parsedPrice, parsedQuantity)
                                }
                                onSuccess()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("🚀 PUBLISH ITEM TO MTANAV", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun OwnerMessagesContent(
    repository: MtanavRepository,
    targetConversationId: Long? = null
) {
    var conversations by remember { mutableStateOf<List<ConversationDto>>(emptyList()) }
    var selectedConversation by remember { mutableStateOf<ConversationDto?>(null) }
    var messages by remember { mutableStateOf<List<MessageDto>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadConversations() {
        coroutineScope.launch {
            isLoading = true
            try {
                val list = repository.getConversations()
                conversations = list

                if (selectedConversation == null && list.isNotEmpty()) {
                    selectedConversation = if (targetConversationId != null) {
                        list.firstOrNull { it.id == targetConversationId } ?: list.first()
                    } else {
                        list.first()
                    }
                }

                selectedConversation?.let { conv ->
                    messages = repository.getMessages(conv.id)
                    repository.markConversationRead(conv.id)
                }
            } catch (e: Exception) {
                /* handled */
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(targetConversationId) {
        loadConversations()
    }

    fun handleSendMessage() {
        val conv = selectedConversation ?: return
        if (messageText.isBlank()) return
        val text = messageText.trim()
        messageText = ""

        coroutineScope.launch {
            isSending = true
            try {
                repository.sendMessage(conv.id, text)
                messages = repository.getMessages(conv.id)
                val updatedList = repository.getConversations()
                conversations = updatedList
                selectedConversation = updatedList.firstOrNull { it.id == conv.id } ?: conv
            } catch (e: Exception) {
                /* handled */
            } finally {
                isSending = false
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 480.dp, max = 650.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isNarrow = maxWidth < 700.dp

            if (isNarrow) {
                if (selectedConversation == null || conversations.isEmpty()) {
                    ConversationListPanel(
                        conversations = conversations,
                        selectedConversation = selectedConversation,
                        isLoading = isLoading,
                        onSelect = { conv ->
                            selectedConversation = conv
                            coroutineScope.launch {
                                messages = repository.getMessages(conv.id)
                                repository.markConversationRead(conv.id)
                            }
                        }
                    )
                } else {
                    ChatMessageViewPanel(
                        conversation = selectedConversation!!,
                        messages = messages,
                        messageText = messageText,
                        isSending = isSending,
                        onBackToList = { selectedConversation = null },
                        onMessageTextChange = { messageText = it },
                        onSend = { handleSendMessage() },
                        isCustomer = false
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                        ConversationListPanel(
                            conversations = conversations,
                            selectedConversation = selectedConversation,
                            isLoading = isLoading,
                            onSelect = { conv ->
                                selectedConversation = conv
                                coroutineScope.launch {
                                    messages = repository.getMessages(conv.id)
                                    repository.markConversationRead(conv.id)
                                }
                            }
                        )
                    }

                    VerticalDivider(color = Color(0xFFE2E8F0))

                    Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                        if (selectedConversation != null) {
                            ChatMessageViewPanel(
                                conversation = selectedConversation!!,
                                messages = messages,
                                messageText = messageText,
                                isSending = isSending,
                                onBackToList = null,
                                onMessageTextChange = { messageText = it },
                                onSend = { handleSendMessage() },
                                isCustomer = false
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select a customer conversation to reply.", color = TextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
