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
    onOpenOwnerDashboard: () -> Unit = {},
    onOpenCustomerPortal: () -> Unit = {},
    onOpenAdminDashboard: () -> Unit = {},
    onAuthRequested: () -> Unit = {},
    onOpenJobsRequested: () -> Unit = {},
    onOpenPromotionsRequested: () -> Unit = {},
    onOpenEarnRequested: () -> Unit = {},
    onRegisterBusinessRequested: () -> Unit = {},
    onRegisterProductRequested: () -> Unit = {},
    onRegisterServiceRequested: () -> Unit = {},
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
            .onKeyEvent { event ->
                if (drawerOpen && event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                    drawerOpen = false
                    true
                } else {
                    false
                }
            }
    ) {
        val isDesktop = maxWidth >= 1024.dp

        if (isDesktop) {
            // DESKTOP LAYOUT (>= 1024px):
            // In HTML flexbox, #compose-root.with-map is fixed 384px on left, #map-root is flex: 1 on right.
            // Compose canvas fills #compose-root (384px) rendering ONLY the Sidebar!
            // MapCanvas initializes native Google Maps in #map-root. ZERO Compose overlay or icons on the map!
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
                    onMarkerSelected = { business -> selectedBusiness = business },
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
                    onHamburgerClicked = {},
                    onSearchClicked = {},
                    onOpenProductsRequested = {
                        sidebarMode = SidebarMode.AllProducts
                        loadBusinesses(SidebarMode.AllProducts, filterState)
                    },
                    onOpenServicesRequested = {
                        sidebarMode = SidebarMode.AllServices
                        loadBusinesses(SidebarMode.AllServices, filterState)
                    },
                    onOpenPromotionsRequested = onOpenPromotionsRequested,
                    onOpenJobsRequested = onOpenJobsRequested,
                    onOpenEarnRequested = onOpenEarnRequested,
                    onSearchSubmitted = { query ->
                        if (query.isNotBlank()) {
                            searchQuery = query
                            handleSearchSubmit()
                        }
                    },
                    onAuthRequested = onAuthRequested,
                    isDrawerOpen = true,
                    modifier = Modifier.fillMaxSize()
                )

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
                    onCloseDrawerRequested = null,
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // MOBILE / SMALL SCREEN LAYOUT (< 1024px):
            // Native Google Maps fills #map-root. Native Google Maps control renders native Hamburger Button.
            // NO Compose icons or layers on top of the map when drawer is closed.
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
                    onMarkerSelected = { business -> selectedBusiness = business },
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
                    onOpenProductsRequested = {
                        sidebarMode = SidebarMode.AllProducts
                        loadBusinesses(SidebarMode.AllProducts, filterState)
                    },
                    onOpenServicesRequested = {
                        sidebarMode = SidebarMode.AllServices
                        loadBusinesses(SidebarMode.AllServices, filterState)
                    },
                    onOpenPromotionsRequested = onOpenPromotionsRequested,
                    onOpenJobsRequested = onOpenJobsRequested,
                    onOpenEarnRequested = onOpenEarnRequested,
                    onSearchSubmitted = { query ->
                        if (query.isNotBlank()) {
                            searchQuery = query
                            handleSearchSubmit()
                        }
                    },
                    onAuthRequested = onAuthRequested,
                    isDrawerOpen = drawerOpen,
                    modifier = Modifier.fillMaxSize()
                )

                // SLIDE-OUT SIDEBAR NAVIGATION DRAWER OVERLAY (ONLY RENDERED WHEN OPEN)
                if (drawerOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { drawerOpen = false }
                    )

                    Surface(
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                        color = Color.Transparent,
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .width(384.dp)
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
                            onBackClicked = { handleBackNavigation() },
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
