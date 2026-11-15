package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.*
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.ui.components.Sidebar
import tz.mtanav.ui.map.LocationNavigator
import tz.mtanav.ui.map.MapCanvas

@Composable
fun HomeMapScreen(
    repository: MtanavRepository,
    locationNavigator: LocationNavigator,
    onViewShopClicked: (businessId: Long, productId: Long?, serviceId: Long?) -> Unit,
    onOpenOwnerDashboard: () -> Unit,
    onOpenCustomerPortal: () -> Unit = {},
    onOpenAdminDashboard: () -> Unit,
    onAuthRequested: () -> Unit,
    onOpenJobsRequested: () -> Unit = {},
    onOpenPromotionsRequested: () -> Unit = {},
    onOpenEarnRequested: () -> Unit = {},
    onRegisterBusinessRequested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sidebarMode by remember { mutableStateOf<SidebarMode>(SidebarMode.Explore) }
    var searchQuery by remember { mutableStateOf("") }
    var productSearchQuery by remember { mutableStateOf("") }
    var serviceSearchQuery by remember { mutableStateOf("") }
    var filterState by remember { mutableStateOf(FilterState()) }

    var quickProducts by remember { mutableStateOf<List<QuickProductDto>>(emptyList()) }
    var quickServices by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }
    var allProducts by remember { mutableStateOf<List<ProductDto>>(emptyList()) }
    var allServices by remember { mutableStateOf<List<CategoryDto>>(emptyList()) }

    var businesses by remember { mutableStateOf<List<MapBusiness>>(emptyList()) }
    var selectedBusiness by remember { mutableStateOf<MapBusiness?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var currentUser by remember { mutableStateOf(repository.currentUser) }
    var drawerOpen by remember { mutableStateOf(false) }
    var desktopSidebarVisible by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    fun loadBusinesses(
        mode: SidebarMode = sidebarMode,
        filters: FilterState = filterState
    ) {
        coroutineScope.launch {
            isLoading = true
            error = null
            try {
                val productId: Long?
                val serviceId: Long?
                val query: String?

                when (mode) {
                    is SidebarMode.ProductResults -> {
                        productId = mode.productId
                        serviceId = null
                        query = null
                    }
                    is SidebarMode.ServiceResults -> {
                        productId = null
                        serviceId = mode.serviceId
                        query = null
                    }
                    is SidebarMode.QueryResults -> {
                        productId = null
                        serviceId = null
                        query = mode.query
                    }
                    else -> {
                        productId = null
                        serviceId = null
                        query = if (searchQuery.isNotBlank()) searchQuery else null
                    }
                }

                val dtos = repository.getMapBusinesses(
                    productId = productId,
                    serviceId = serviceId,
                    query = query,
                    minPrice = filters.minPrice,
                    maxPrice = filters.maxPrice,
                    distanceKm = filters.distanceKm,
                    sort = filters.sortOption
                )

                businesses = dtos.map { it.toMapBusiness() }
                selectedBusiness = businesses.firstOrNull()
            } catch (e: Exception) {
                error = "Failed to load businesses. Please try again."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            quickProducts = repository.getQuickProducts()
            quickServices = repository.getQuickServices()
            allProducts = repository.getAllProducts()
            allServices = repository.getAllServices()
            loadBusinesses(SidebarMode.Explore, filterState)
        } catch (e: Exception) {
            error = "Could not initialize data."
        } finally {
            isLoading = false
        }
    }

    fun handleSearchSubmit() {
        if (searchQuery.isBlank()) return
        val q = searchQuery.trim()

        val matchingProduct = allProducts.firstOrNull { it.name.equals(q, ignoreCase = true) }
        val matchingService = allServices.firstOrNull { it.name.equals(q, ignoreCase = true) }

        val newMode = when {
            matchingProduct != null -> SidebarMode.ProductResults(
                productId = matchingProduct.id,
                productName = matchingProduct.name,
                icon = matchingProduct.quickProductIcon ?: "📦"
            )
            matchingService != null -> SidebarMode.ServiceResults(
                serviceId = matchingService.id,
                serviceName = matchingService.name,
                icon = matchingService.icon ?: "⚙️"
            )
            else -> SidebarMode.QueryResults(query = q)
        }

        sidebarMode = newMode
        loadBusinesses(newMode, filterState)
    }

    fun handleBackNavigation() {
        val newMode = when (sidebarMode) {
            is SidebarMode.ProductResults -> SidebarMode.AllProducts
            is SidebarMode.ServiceResults -> SidebarMode.AllServices
            is SidebarMode.AllProducts -> SidebarMode.Explore
            is SidebarMode.AllServices -> SidebarMode.Explore
            is SidebarMode.QueryResults -> SidebarMode.Explore
            SidebarMode.Explore -> SidebarMode.Explore
        }
        sidebarMode = newMode
        loadBusinesses(newMode, filterState)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .onKeyEvent { event ->
                if (drawerOpen && event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                    drawerOpen = false
                    true
                } else {
                    false
                }
            }
    ) {
        val screenWidth = maxWidth
        val isSmallScreen = screenWidth < 600.dp
        val isMediumScreen = screenWidth >= 600.dp && screenWidth < 1024.dp
        val isSmallOrMedium = screenWidth < 1024.dp

        val drawerWidth = when {
            isSmallScreen -> minOf(320.dp, screenWidth * 0.85f)
            isMediumScreen -> minOf(340.dp, screenWidth * 0.80f)
            else -> 350.dp
        }

        if (isSmallOrMedium) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Map Canvas fills entire screen using native Google Maps Controls (ControlPosition)
                MapCanvas(
                    businesses = businesses,
                    selectedBusiness = selectedBusiness,
                    activeFilterTitle = when (val mode = sidebarMode) {
                        is SidebarMode.ProductResults -> mode.productName
                        is SidebarMode.ServiceResults -> mode.serviceName
                        is SidebarMode.QueryResults -> mode.query
                        else -> null
                    },
                    onMarkerSelected = { business ->
                        selectedBusiness = business
                    },
                    onViewShopClicked = { businessId ->
                        val activeProductId = (sidebarMode as? SidebarMode.ProductResults)?.productId
                        val activeServiceId = (sidebarMode as? SidebarMode.ServiceResults)?.serviceId
                        onViewShopClicked(businessId, activeProductId, activeServiceId)
                    },
                    onNavigateClicked = { target ->
                        locationNavigator.launchGoogleMapsNavigation(
                            latitude = target.latitude,
                            longitude = target.longitude,
                            businessName = target.name
                        )
                    },
                    onHamburgerClicked = { drawerOpen = true },
                    onSearchClicked = { drawerOpen = true },
                    modifier = Modifier.fillMaxSize()
                )

                // HAMBURGER NAVIGATION DRAWER OVERLAY (Roomify Style)
                if (drawerOpen) {
                    // Dimmed backdrop / scrim (clicking outside closes drawer)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { drawerOpen = false }
                    )

                    // Navigation Drawer Panel (Responsive for small & medium screens)
                    Surface(
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                        color = SurfaceWhite,
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .width(drawerWidth)
                            .fillMaxHeight()
                            .align(Alignment.CenterStart)
                            .clickable(enabled = false) {}
                    ) {
                        Sidebar(
                            sidebarMode = sidebarMode,
                            currentUser = currentUser,
                            repository = repository,
                            allProducts = allProducts,
                            allServices = allServices,
                            filterState = filterState,
                            searchQuery = searchQuery,
                            productSearchQuery = productSearchQuery,
                            serviceSearchQuery = serviceSearchQuery,
                            resultCount = businesses.size,
                            isLoading = isLoading,
                            error = error,
                            onCloseDrawerRequested = { drawerOpen = false },
                            onSearchQueryChange = { searchQuery = it },
                            onProductSearchQueryChange = { productSearchQuery = it },
                            onServiceSearchQueryChange = { serviceSearchQuery = it },
                            onSearchSubmitted = {
                                drawerOpen = false
                                handleSearchSubmit()
                            },
                            onModeChange = { mode ->
                                drawerOpen = false
                                sidebarMode = mode
                                loadBusinesses(mode, filterState)
                            },
                            onProductSelected = { product ->
                                drawerOpen = false
                                val newMode = SidebarMode.ProductResults(
                                    productId = product.id,
                                    productName = product.name,
                                    icon = product.quickProductIcon ?: "📦"
                                )
                                sidebarMode = newMode
                                loadBusinesses(newMode, filterState)
                            },
                            onServiceSelected = { service ->
                                drawerOpen = false
                                val newMode = SidebarMode.ServiceResults(
                                    serviceId = service.id,
                                    serviceName = service.name,
                                    icon = service.icon ?: "⚙️"
                                )
                                sidebarMode = newMode
                                loadBusinesses(newMode, filterState)
                            },
                            onSuggestionSelected = { suggestion ->
                                drawerOpen = false
                                when (suggestion.type.uppercase()) {
                                    "PRODUCT" -> {
                                        val pId = suggestion.targetId ?: suggestion.id
                                        val newMode = SidebarMode.ProductResults(
                                            productId = pId,
                                            productName = suggestion.name,
                                            icon = "📦"
                                        )
                                        sidebarMode = newMode
                                        loadBusinesses(newMode, filterState)
                                    }
                                    "SERVICE" -> {
                                        val sId = suggestion.targetId ?: suggestion.id
                                        val newMode = SidebarMode.ServiceResults(
                                            serviceId = sId,
                                            serviceName = suggestion.name,
                                            icon = "⚙️"
                                        )
                                        sidebarMode = newMode
                                        loadBusinesses(newMode, filterState)
                                    }
                                    "BUSINESS" -> {
                                        val bId = suggestion.businessId ?: suggestion.id
                                        onViewShopClicked(bId, null, null)
                                    }
                                    else -> {
                                        searchQuery = suggestion.name
                                        handleSearchSubmit()
                                    }
                                }
                            },
                            onApplyFilter = { newFilterState ->
                                drawerOpen = false
                                filterState = newFilterState
                                loadBusinesses(sidebarMode, newFilterState)
                            },
                            onClearFilters = {
                                drawerOpen = false
                                filterState = FilterState()
                                loadBusinesses(sidebarMode, FilterState())
                            },
                            onBackClicked = {
                                handleBackNavigation()
                            },
                            onAuthRequested = {
                                drawerOpen = false
                                onAuthRequested()
                            },
                            onOwnerDashboardRequested = {
                                drawerOpen = false
                                onOpenOwnerDashboard()
                            },
                            onCustomerPortalRequested = {
                                drawerOpen = false
                                onOpenCustomerPortal()
                            },
                            onAdminDashboardRequested = {
                                drawerOpen = false
                                onOpenAdminDashboard()
                            },
                            onLogoutRequested = {
                                drawerOpen = false
                                repository.authToken = null
                                repository.currentUser = null
                                currentUser = null
                            },
                            onOpenJobsRequested = {
                                drawerOpen = false
                                onOpenJobsRequested()
                            },
                            onOpenPromotionsRequested = {
                                drawerOpen = false
                                onOpenPromotionsRequested()
                            },
                            onOpenEarnRequested = {
                                drawerOpen = false
                                onOpenEarnRequested()
                            },
                            onRegisterBusinessRequested = {
                                drawerOpen = false
                                onRegisterBusinessRequested()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // VERY TOP LEFT HAMBURGER BUTTON (Mobile / Small Screen)
                if (!drawerOpen) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryDeepBlue,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart)
                            .clickable { drawerOpen = true }
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MtanavIcons.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // DESKTOP LAYOUT (>= 1024dp) - Supports Roomify-style sidebar toggle/collapse
            if (desktopSidebarVisible) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Sidebar(
                        sidebarMode = sidebarMode,
                        currentUser = currentUser,
                        repository = repository,
                        allProducts = allProducts,
                        allServices = allServices,
                        filterState = filterState,
                        searchQuery = searchQuery,
                        productSearchQuery = productSearchQuery,
                        serviceSearchQuery = serviceSearchQuery,
                        resultCount = businesses.size,
                        isLoading = isLoading,
                        error = error,
                        onCloseDrawerRequested = { desktopSidebarVisible = false },
                        onSearchQueryChange = { searchQuery = it },
                        onProductSearchQueryChange = { productSearchQuery = it },
                        onServiceSearchQueryChange = { serviceSearchQuery = it },
                        onSearchSubmitted = { handleSearchSubmit() },
                        onModeChange = { mode ->
                            sidebarMode = mode
                            loadBusinesses(mode, filterState)
                        },
                        onProductSelected = { product ->
                            val newMode = SidebarMode.ProductResults(
                                productId = product.id,
                                productName = product.name,
                                icon = product.quickProductIcon ?: "📦"
                            )
                            sidebarMode = newMode
                            loadBusinesses(newMode, filterState)
                        },
                        onServiceSelected = { service ->
                            val newMode = SidebarMode.ServiceResults(
                                serviceId = service.id,
                                serviceName = service.name,
                                icon = service.icon ?: "⚙️"
                            )
                            sidebarMode = newMode
                            loadBusinesses(newMode, filterState)
                        },
                        onSuggestionSelected = { suggestion ->
                            when (suggestion.type.uppercase()) {
                                "PRODUCT" -> {
                                    val pId = suggestion.targetId ?: suggestion.id
                                    val newMode = SidebarMode.ProductResults(
                                        productId = pId,
                                        productName = suggestion.name,
                                        icon = "📦"
                                    )
                                    sidebarMode = newMode
                                    loadBusinesses(newMode, filterState)
                                }
                                "SERVICE" -> {
                                    val sId = suggestion.targetId ?: suggestion.id
                                    val newMode = SidebarMode.ServiceResults(
                                        serviceId = sId,
                                        serviceName = suggestion.name,
                                        icon = "⚙️"
                                    )
                                    sidebarMode = newMode
                                    loadBusinesses(newMode, filterState)
                                }
                                "BUSINESS" -> {
                                    val bId = suggestion.businessId ?: suggestion.id
                                    onViewShopClicked(bId, null, null)
                                }
                                else -> {
                                    searchQuery = suggestion.name
                                    handleSearchSubmit()
                                }
                            }
                        },
                        onApplyFilter = { newFilterState ->
                            filterState = newFilterState
                            loadBusinesses(sidebarMode, newFilterState)
                        },
                        onClearFilters = {
                            filterState = FilterState()
                            loadBusinesses(sidebarMode, FilterState())
                        },
                        onBackClicked = { handleBackNavigation() },
                        onAuthRequested = onAuthRequested,
                        onOwnerDashboardRequested = onOpenOwnerDashboard,
                        onCustomerPortalRequested = onOpenCustomerPortal,
                        onAdminDashboardRequested = onOpenAdminDashboard,
                        onLogoutRequested = {
                            repository.authToken = null
                            repository.currentUser = null
                            currentUser = null
                        },
                        onOpenJobsRequested = onOpenJobsRequested,
                        onOpenPromotionsRequested = onOpenPromotionsRequested,
                        onOpenEarnRequested = onOpenEarnRequested,
                        onRegisterBusinessRequested = onRegisterBusinessRequested,
                        modifier = Modifier.width(350.dp)
                    )

                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight().width(1.dp),
                        color = Color(0xFFE2E8F0)
                    )

                    MapCanvas(
                        businesses = businesses,
                        selectedBusiness = selectedBusiness,
                        activeFilterTitle = when (val mode = sidebarMode) {
                            is SidebarMode.ProductResults -> mode.productName
                            is SidebarMode.ServiceResults -> mode.serviceName
                            is SidebarMode.QueryResults -> mode.query
                            else -> null
                        },
                        onMarkerSelected = { business ->
                            selectedBusiness = business
                        },
                        onViewShopClicked = { businessId ->
                            val activeProductId = (sidebarMode as? SidebarMode.ProductResults)?.productId
                            val activeServiceId = (sidebarMode as? SidebarMode.ServiceResults)?.serviceId
                            onViewShopClicked(businessId, activeProductId, activeServiceId)
                        },
                        onNavigateClicked = { target ->
                            locationNavigator.launchGoogleMapsNavigation(
                                latitude = target.latitude,
                                longitude = target.longitude,
                                businessName = target.name
                            )
                        },
                        onHamburgerClicked = { desktopSidebarVisible = true },
                        onSearchClicked = { desktopSidebarVisible = true },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            } else {
                // COLLAPSED DESKTOP SIDEBAR: Full Map Canvas with VERY TOP LEFT Hamburger Icon
                Box(modifier = Modifier.fillMaxSize()) {
                    MapCanvas(
                        businesses = businesses,
                        selectedBusiness = selectedBusiness,
                        activeFilterTitle = when (val mode = sidebarMode) {
                            is SidebarMode.ProductResults -> mode.productName
                            is SidebarMode.ServiceResults -> mode.serviceName
                            is SidebarMode.QueryResults -> mode.query
                            else -> null
                        },
                        onMarkerSelected = { business ->
                            selectedBusiness = business
                        },
                        onViewShopClicked = { businessId ->
                            val activeProductId = (sidebarMode as? SidebarMode.ProductResults)?.productId
                            val activeServiceId = (sidebarMode as? SidebarMode.ServiceResults)?.serviceId
                            onViewShopClicked(businessId, activeProductId, activeServiceId)
                        },
                        onNavigateClicked = { target ->
                            locationNavigator.launchGoogleMapsNavigation(
                                latitude = target.latitude,
                                longitude = target.longitude,
                                businessName = target.name
                            )
                        },
                        onHamburgerClicked = { desktopSidebarVisible = true },
                        onSearchClicked = { desktopSidebarVisible = true },
                        modifier = Modifier.fillMaxSize()
                    )

                    // VERY TOP LEFT HAMBURGER BUTTON (Collapsed Desktop Screen)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryDeepBlue,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart)
                            .clickable { desktopSidebarVisible = true }
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MtanavIcons.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
