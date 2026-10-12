package tz.mtanav.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tz.mtanav.model.*
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@Composable
fun Sidebar(
    sidebarMode: SidebarMode,
    currentUser: UserDto?,
    repository: MtanavRepository,
    allProducts: List<ProductDto>,
    allServices: List<CategoryDto>,
    filterState: FilterState,
    searchQuery: String,
    productSearchQuery: String,
    serviceSearchQuery: String,
    resultCount: Int,
    isLoading: Boolean,
    error: String?,
    onSearchQueryChange: (String) -> Unit,
    onProductSearchQueryChange: (String) -> Unit,
    onServiceSearchQueryChange: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    onModeChange: (SidebarMode) -> Unit,
    onProductSelected: (ProductDto) -> Unit,
    onServiceSelected: (CategoryDto) -> Unit,
    onSuggestionSelected: (SearchSuggestionDto) -> Unit,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilters: () -> Unit,
    onBackClicked: () -> Unit,
    onAuthRequested: () -> Unit,
    onOwnerDashboardRequested: () -> Unit,
    onCustomerPortalRequested: () -> Unit = {},
    onAdminDashboardRequested: () -> Unit,
    onLogoutRequested: () -> Unit,
    onOpenJobsRequested: () -> Unit = {},
    onOpenPromotionsRequested: () -> Unit = {},
    onOpenEarnRequested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(SurfaceWhite)
    ) {
        // TOP SECTION: MTANAV Brand Gradient Background (#123B6D to #2563A6)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryDeepBlue, PrimaryBlue)
                    )
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (sidebarMode) {
                is SidebarMode.Explore -> {
                    ExploreTopContent(
                        searchQuery = searchQuery,
                        repository = repository,
                        onSearchQueryChange = onSearchQueryChange,
                        onSearchSubmitted = onSearchSubmitted,
                        onSuggestionSelected = onSuggestionSelected,
                        onExploreAllProductsClicked = { onModeChange(SidebarMode.AllProducts) },
                        onExploreAllServicesClicked = { onModeChange(SidebarMode.AllServices) },
                        onOpenJobsRequested = onOpenJobsRequested,
                        onOpenPromotionsRequested = onOpenPromotionsRequested,
                        onOpenEarnRequested = onOpenEarnRequested
                    )
                }

                is SidebarMode.AllProducts -> {
                    AllProductsTopContent(
                        productSearchQuery = productSearchQuery,
                        onProductSearchQueryChange = onProductSearchQueryChange,
                        allProducts = allProducts,
                        onProductSelected = onProductSelected,
                        onBackClicked = onBackClicked
                    )
                }

                is SidebarMode.AllServices -> {
                    AllServicesTopContent(
                        serviceSearchQuery = serviceSearchQuery,
                        onServiceSearchQueryChange = onServiceSearchQueryChange,
                        allServices = allServices,
                        onServiceSelected = onServiceSelected,
                        onBackClicked = onBackClicked
                    )
                }

                is SidebarMode.ProductResults -> {
                    ResultsTopContent(
                        title = sidebarMode.productName,
                        backLabel = "← All Products",
                        resultCount = resultCount,
                        filterState = filterState,
                        isService = false,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked
                    )
                }

                is SidebarMode.ServiceResults -> {
                    ResultsTopContent(
                        title = sidebarMode.serviceName,
                        backLabel = "← All Services",
                        resultCount = resultCount,
                        filterState = filterState,
                        isService = true,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked
                    )
                }

                is SidebarMode.QueryResults -> {
                    ResultsTopContent(
                        title = sidebarMode.query,
                        backLabel = "← Explore",
                        resultCount = resultCount,
                        filterState = filterState,
                        isService = false,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked
                    )
                }
            }
        }

        // BOTTOM SECTION: WHITE Background (#FFFFFF)
        Surface(
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(bottom = 12.dp))

                if (currentUser == null) {
                    Button(
                        onClick = onAuthRequested,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("REGISTER PRODUCT OR SERVICE", color = SurfaceWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = currentUser.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentUser.email,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ActionTeal.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (currentUser.roles.contains("ROLE_SUPER_ADMIN")) "SUPER ADMIN"
                                    else if (currentUser.roles.contains("ROLE_ADMIN")) "ADMIN"
                                    else if (currentUser.roles.contains("ROLE_BUSINESS_OWNER")) "OWNER"
                                    else "CLIENT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ActionTeal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (currentUser.roles.contains("ROLE_BUSINESS_OWNER")) {
                            Button(
                                onClick = onOwnerDashboardRequested,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Owner Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = onCustomerPortalRequested,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Customer Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (currentUser.roles.contains("ROLE_ADMIN") || currentUser.roles.contains("ROLE_SUPER_ADMIN")) {
                            Button(
                                onClick = onAdminDashboardRequested,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Admin Panel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = onLogoutRequested,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign out", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. EXPLORE TOP CONTENT (BRANDED BLUE)
// ==========================================

@Composable
private fun ExploreTopContent(
    searchQuery: String,
    repository: MtanavRepository,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    onSuggestionSelected: (SearchSuggestionDto) -> Unit,
    onExploreAllProductsClicked: () -> Unit,
    onExploreAllServicesClicked: () -> Unit,
    onOpenJobsRequested: () -> Unit,
    onOpenPromotionsRequested: () -> Unit,
    onOpenEarnRequested: () -> Unit
) {
    var suggestions by remember { mutableStateOf<List<SearchSuggestionDto>>(emptyList()) }
    var isSuggestionsLoading by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    // DEBOUNCED LIVE SEARCH SUGGESTIONS (250-350ms)
    LaunchedEffect(searchQuery) {
        val trimmed = searchQuery.trim()
        if (trimmed.length >= 2) {
            isSuggestionsLoading = true
            showSuggestions = true
            delay(300) // Debounce 300ms
            suggestions = repository.getSearchSuggestions(trimmed)
            isSuggestionsLoading = false
        } else {
            suggestions = emptyList()
            showSuggestions = false
            isSuggestionsLoading = false
        }
    }

    // Brand Header
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = "MTANAV",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = SurfaceWhite,
            letterSpacing = 1.sp
        )
        Text(
            text = "Find it. Get there. Earn with it.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = HighlightGold
        )
    }

    // Search Field
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceWhite,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Find products, shops, services or jobs...", fontSize = 13.sp, color = TextSecondary) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    showSuggestions = false
                    onSearchSubmitted()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            if (searchQuery.isNotBlank()) {
                IconButton(
                    onClick = {
                        showSuggestions = false
                        onSearchSubmitted()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("➔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                }
            }
        }
    }

    // LIVE SEARCH SUGGESTION PANEL
    if (showSuggestions && searchQuery.trim().length >= 2) {
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceWhite,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "SUGGESTIONS FOR \"${searchQuery.trim()}\"",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = { showSuggestions = false },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Text("✕", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                if (isSuggestionsLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PrimaryDeepBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Searching MTANAV...", fontSize = 12.sp, color = TextSecondary)
                    }
                } else if (suggestions.isEmpty()) {
                    Text(
                        text = "No matching products, services or shops found.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        suggestions.take(8).forEach { suggestion ->
                            SuggestionRowItem(
                                suggestion = suggestion,
                                onClick = {
                                    showSuggestions = false
                                    onSuggestionSelected(suggestion)
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))

                // VIEW ALL RESULTS ACTION TILE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryLight)
                        .clickable {
                            showSuggestions = false
                            onSearchSubmitted()
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🔍 View all results for \"${searchQuery.trim()}\"",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDeepBlue
                    )
                    Text("➔", fontSize = 12.sp, color = PrimaryDeepBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // EXPLORE CATEGORIES SECTION
    Text(
        text = "DISCOVERY CATEGORIES",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = SurfaceWhite.copy(alpha = 0.7f),
        letterSpacing = 1.2.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        TextNavigationTile(
            title = "Products",
            subtitle = "Spoons, rice, phones, chargers, shoes & more",
            onClick = onExploreAllProductsClicked
        )
        TextNavigationTile(
            title = "Services",
            subtitle = "Garage, car wash, salon, phone repair & banking",
            onClick = onExploreAllServicesClicked
        )
        TextNavigationTile(
            title = "Local Jobs",
            subtitle = "Delivery, sales, technical, trade & casual work",
            onClick = onOpenJobsRequested
        )
        TextNavigationTile(
            title = "Promotions",
            subtitle = "Discounts, flash sales & genuine deals",
            onClick = onOpenPromotionsRequested
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // SECONDARY ENTRY: WANT TO EARN WITH MTANAV
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = HighlightGold.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, HighlightGold),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenEarnRequested)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Want to earn with MTANAV?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
                Text(
                    text = "Referrals, creator promotions & rewards",
                    fontSize = 11.sp,
                    color = SurfaceWhite.copy(alpha = 0.8f)
                )
            }
            Text("➔", fontSize = 12.sp, color = HighlightGold, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SuggestionRowItem(
    suggestion: SearchSuggestionDto,
    onClick: () -> Unit
) {
    val (typeColor, typeBg) = when (suggestion.type.uppercase()) {
        "PRODUCT" -> Pair(Color(0xFF0284C7), Color(0xFFE0F2FE))
        "SERVICE" -> Pair(Color(0xFF7C3AED), Color(0xFFF3E8FF))
        "BUSINESS" -> Pair(Color(0xFFD97706), Color(0xFFFEF3C7))
        else -> Pair(TextSecondary, Color(0xFFF1F5F9))
    }

    val icon = when (suggestion.type.uppercase()) {
        "PRODUCT" -> "📦"
        "SERVICE" -> "⚙️"
        "BUSINESS" -> "🏢"
        else -> "🔍"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        AppThumbnail(
            imageUrl = suggestion.thumbnailUrl,
            fallbackIcon = icon,
            categoryType = suggestion.type,
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = suggestion.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = typeBg
                ) {
                    Text(
                        text = suggestion.type,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = typeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            suggestion.businessName?.let { bName ->
                Text(
                    text = bName,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }

        suggestion.price?.let { p ->
            Text(
                text = "TSh ${p.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ActionTeal,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

// ==========================================
// 2. ALL PRODUCTS TOP CONTENT
// ==========================================

@Composable
private fun AllProductsTopContent(
    productSearchQuery: String,
    onProductSearchQueryChange: (String) -> Unit,
    allProducts: List<ProductDto>,
    onProductSelected: (ProductDto) -> Unit,
    onBackClicked: () -> Unit
) {
    TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
        Text("← Back to Explore", color = HighlightGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Text("EXPLORE PRODUCTS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(8.dp))

    Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = productSearchQuery,
            onValueChange = onProductSearchQueryChange,
            placeholder = { Text("Search products...", fontSize = 13.sp, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text("SELECT A PRODUCT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite.copy(alpha = 0.7f), letterSpacing = 1.2.sp)
    Spacer(modifier = Modifier.height(8.dp))

    val filteredProducts = if (productSearchQuery.isBlank()) allProducts else allProducts.filter {
        it.name.contains(productSearchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        filteredProducts.forEach { product ->
            TextNavigationTile(
                title = product.name,
                subtitle = product.description,
                onClick = { onProductSelected(product) }
            )
        }
    }
}

// ==========================================
// 3. ALL SERVICES TOP CONTENT
// ==========================================

@Composable
private fun AllServicesTopContent(
    serviceSearchQuery: String,
    onServiceSearchQueryChange: (String) -> Unit,
    allServices: List<CategoryDto>,
    onServiceSelected: (CategoryDto) -> Unit,
    onBackClicked: () -> Unit
) {
    TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
        Text("← Back to Explore", color = HighlightGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Text("EXPLORE SERVICES", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(8.dp))

    Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = serviceSearchQuery,
            onValueChange = onServiceSearchQueryChange,
            placeholder = { Text("Search services...", fontSize = 13.sp, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text("SELECT A SERVICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite.copy(alpha = 0.7f), letterSpacing = 1.2.sp)
    Spacer(modifier = Modifier.height(8.dp))

    val filteredServices = if (serviceSearchQuery.isBlank()) allServices else allServices.filter {
        it.name.contains(serviceSearchQuery, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        filteredServices.forEach { service ->
            TextNavigationTile(
                title = service.name,
                subtitle = service.description,
                onClick = { onServiceSelected(service) }
            )
        }
    }
}

// ==========================================
// 4. RESULTS TOP CONTENT (FILTERING & RESULTS)
// ==========================================

@Composable
private fun ResultsTopContent(
    title: String,
    backLabel: String,
    resultCount: Int,
    filterState: FilterState,
    isService: Boolean,
    isLoading: Boolean,
    error: String?,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilters: () -> Unit,
    onBackClicked: () -> Unit
) {
    var draftFilter by remember(filterState) { mutableStateOf(filterState) }

    TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
        Text(backLabel, color = HighlightGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Text(title.uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(4.dp))

    Surface(shape = CircleShape, color = SurfaceWhite.copy(alpha = 0.15f)) {
        Text(
            text = "$title — $resultCount places found",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SurfaceWhite,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (isLoading) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = SurfaceWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Updating results...", fontSize = 12.sp, color = SurfaceWhite)
        }
    }

    error?.let { err ->
        Surface(shape = RoundedCornerShape(8.dp), color = ErrorRed.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(err, color = SurfaceWhite, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    FilterSection(
        filterState = draftFilter,
        _isService = isService,
        onFilterStateChange = { draftFilter = it }
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onApplyFilter(draftFilter) },
            colors = ButtonDefaults.buttonColors(containerColor = HighlightGold),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text("APPLY FILTER", color = PrimaryDeepBlue, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }

        Button(
            onClick = {
                draftFilter = FilterState()
                onClearFilters()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text("CLEAR FILTER", color = SurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// REUSABLE FILTER COMPONENTS
// ==========================================

@Composable
private fun FilterSection(
    filterState: FilterState,
    _isService: Boolean,
    onFilterStateChange: (FilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("FILTER RESULTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite.copy(alpha = 0.7f), letterSpacing = 1.2.sp)

        MaxBudgetFilter(
            maxBudget = filterState.maxPrice,
            onMaxBudgetChange = { budget ->
                onFilterStateChange(filterState.copy(maxPrice = budget))
            }
        )

        DistanceFilter(
            selectedDistance = filterState.distanceKm,
            onDistanceSelected = { dist ->
                onFilterStateChange(filterState.copy(distanceKm = dist))
            }
        )

        AvailabilityFilter(
            availableNow = filterState.availableNow,
            onAvailabilityToggle = { avail ->
                onFilterStateChange(filterState.copy(availableNow = avail))
            }
        )

        SortFilter(
            selectedSort = filterState.sortOption,
            onSortSelected = { sort ->
                onFilterStateChange(filterState.copy(sortOption = sort))
            }
        )
    }
}

@Composable
private fun MaxBudgetFilter(
    maxBudget: Double?,
    onMaxBudgetChange: (Double?) -> Unit
) {
    var budgetText by remember(maxBudget) { mutableStateOf(maxBudget?.toInt()?.toString() ?: "") }

    Column {
        Text("Maximum Budget", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        Text("Show products up to this price", fontSize = 10.sp, color = SurfaceWhite.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(4.dp))
        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite) {
            OutlinedTextField(
                value = budgetText,
                onValueChange = {
                    budgetText = it
                    val parsed = it.toDoubleOrNull()
                    onMaxBudgetChange(parsed)
                },
                placeholder = { Text("TSh e.g. 10000", fontSize = 12.sp, color = TextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun DistanceFilter(
    selectedDistance: Double?,
    onDistanceSelected: (Double?) -> Unit
) {
    Column {
        Text("Location / Distance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            val distances = listOf(1.0 to "1 km", 3.0 to "3 km", 5.0 to "5 km", null to "Any")
            distances.forEach { (dist, label) ->
                val isSelected = selectedDistance == dist
                val chipBg = if (isSelected) HighlightGold else SurfaceWhite.copy(alpha = 0.2f)
                val chipText = if (isSelected) TextPrimary else SurfaceWhite

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(chipBg)
                        .clickable { onDistanceSelected(dist) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = chipText)
                }
            }
        }
    }
}

@Composable
private fun AvailabilityFilter(
    availableNow: Boolean,
    onAvailabilityToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite.copy(alpha = 0.2f))
            .clickable { onAvailabilityToggle(!availableNow) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("Available now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        Switch(
            checked = availableNow,
            onCheckedChange = onAvailabilityToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = ActionTeal
            )
        )
    }
}

@Composable
private fun SortFilter(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    Column {
        Text("Sort by", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            SortOption.entries.forEach { option ->
                val isSelected = selectedSort == option
                val chipBg = if (isSelected) ActionTeal else SurfaceWhite.copy(alpha = 0.2f)
                val chipText = SurfaceWhite

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(chipBg)
                        .clickable { onSortSelected(option) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = option.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = chipText)
                }
            }
        }
    }
}

@Composable
private fun TextNavigationTile(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = SurfaceWhite,
                fontWeight = FontWeight.Bold
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = SurfaceWhite.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
        }
        Text("➔", fontSize = 12.sp, color = HighlightGold)
    }
}
