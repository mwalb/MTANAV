package tz.mtanav.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tz.mtanav.model.*
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.util.FormattingUtils

// Fresh Sidebar Brand Colors
private val SidebarPrimary = Color(0xFF176B65)
private val SidebarDark = Color(0xFF124E4A)
private val SidebarLight = Color(0xFFE4F3F0)
private val SidebarAccent = Color(0xFFE8AD4A)

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
    onCloseDrawerRequested: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(SidebarDark)
    ) {
        // SCROLLABLE TOP CONTENT AREA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (sidebarMode) {
                is SidebarMode.Explore -> {
                    MainDiscoveryContent(
                        searchQuery = searchQuery,
                        repository = repository,
                        onSearchQueryChange = onSearchQueryChange,
                        onSearchSubmitted = onSearchSubmitted,
                        onSuggestionSelected = onSuggestionSelected,
                        onOpenProducts = { onModeChange(SidebarMode.AllProducts) },
                        onOpenServices = { onModeChange(SidebarMode.AllServices) },
                        onOpenPromotions = onOpenPromotionsRequested,
                        onOpenJobs = onOpenJobsRequested,
                        onOpenEarn = onOpenEarnRequested,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }

                is SidebarMode.AllProducts -> {
                    AllProductsContent(
                        searchQuery = productSearchQuery,
                        allProducts = allProducts,
                        onSearchChange = onProductSearchQueryChange,
                        onProductSelected = onProductSelected,
                        onBackClicked = onBackClicked,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }

                is SidebarMode.AllServices -> {
                    AllServicesContent(
                        searchQuery = serviceSearchQuery,
                        allServices = allServices,
                        onSearchChange = onServiceSearchQueryChange,
                        onServiceSelected = onServiceSelected,
                        onBackClicked = onBackClicked,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }

                is SidebarMode.ProductResults -> {
                    ResultsContent(
                        title = sidebarMode.productName,
                        backLabel = "All Products",
                        resultCount = resultCount,
                        filterState = filterState,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }

                is SidebarMode.ServiceResults -> {
                    ResultsContent(
                        title = sidebarMode.serviceName,
                        backLabel = "All Services",
                        resultCount = resultCount,
                        filterState = filterState,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }

                is SidebarMode.QueryResults -> {
                    ResultsContent(
                        title = sidebarMode.query,
                        backLabel = "Explore",
                        resultCount = resultCount,
                        filterState = filterState,
                        isLoading = isLoading,
                        error = error,
                        onApplyFilter = onApplyFilter,
                        onClearFilters = onClearFilters,
                        onBackClicked = onBackClicked,
                        onCloseDrawerRequested = onCloseDrawerRequested
                    )
                }
            }
        }

        // ACCOUNT / AUTHENTICATION FOOTER
        Surface(
            color = SidebarDark,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                HorizontalDivider(color = SidebarPrimary.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 12.dp))

                if (currentUser == null) {
                    Button(
                        onClick = onAuthRequested,
                        colors = ButtonDefaults.buttonColors(containerColor = SidebarAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(MtanavIcons.Person, contentDescription = null, tint = SidebarDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign in / Register", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SidebarDark)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = SidebarAccent.copy(alpha = 0.2f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(MtanavIcons.Person, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentUser.fullName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SurfaceWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentUser.email,
                                        fontSize = 11.sp,
                                        color = SurfaceWhite.copy(alpha = 0.75f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SidebarAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (currentUser.roles.contains("ROLE_SUPER_ADMIN")) "ADMIN"
                                    else if (currentUser.roles.contains("ROLE_BUSINESS_OWNER")) "OWNER"
                                    else "CLIENT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SidebarAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (currentUser.roles.contains("ROLE_BUSINESS_OWNER")) {
                            Button(
                                onClick = onOwnerDashboardRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = SidebarPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Owner Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                            }
                        } else {
                            Button(
                                onClick = onCustomerPortalRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = SidebarPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Customer Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                            }
                        }

                        if (currentUser.roles.contains("ROLE_ADMIN") || currentUser.roles.contains("ROLE_SUPER_ADMIN")) {
                            Button(
                                onClick = onAdminDashboardRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = SidebarPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("Admin Panel", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                            }
                        }

                        TextButton(
                            onClick = onLogoutRequested,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(MtanavIcons.ExitToApp, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sign out", color = ErrorRed, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// 1. MAIN DISCOVERY CONTENT
// ============================================================

@Composable
private fun MainDiscoveryContent(
    searchQuery: String,
    repository: MtanavRepository,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    onSuggestionSelected: (SearchSuggestionDto) -> Unit,
    onOpenProducts: () -> Unit,
    onOpenServices: () -> Unit,
    onOpenPromotions: () -> Unit,
    onOpenJobs: () -> Unit,
    onOpenEarn: () -> Unit,
    onCloseDrawerRequested: (() -> Unit)? = null
) {
    var suggestions by remember { mutableStateOf<List<SearchSuggestionDto>>(emptyList()) }
    var isSuggestionsLoading by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        val trimmed = searchQuery.trim()
        if (trimmed.length >= 2) {
            isSuggestionsLoading = true
            showSuggestions = true
            delay(300)
            suggestions = repository.getSearchSuggestions(trimmed)
            isSuggestionsLoading = false
        } else {
            suggestions = emptyList()
            showSuggestions = false
            isSuggestionsLoading = false
        }
    }

    // BRANDING HEADER
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "MTANAV",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = SurfaceWhite,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Find it. Get there. Earn with it.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SidebarAccent
            )
        }

        onCloseDrawerRequested?.let { onClose ->
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = MtanavIcons.Close,
                    contentDescription = "Close Menu",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // ATTRACTIVE SEARCH INPUT
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = MtanavIcons.Search,
                contentDescription = null,
                tint = SidebarPrimary,
                modifier = Modifier.size(18.dp).padding(start = 2.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search products & services...",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = MtanavIcons.ArrowForward,
                        contentDescription = "Submit",
                        tint = SidebarPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // LIVE SEARCH SUGGESTION OVERLAY
    if (showSuggestions && searchQuery.trim().length >= 2) {
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "MATCHING RESULTS",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = { showSuggestions = false },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(MtanavIcons.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(12.dp))
                    }
                }

                if (isSuggestionsLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = SidebarPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Searching...", fontSize = 11.sp, color = TextSecondary)
                    }
                } else if (suggestions.isEmpty()) {
                    Text(
                        text = "No matching items found.",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        suggestions.take(6).forEach { item ->
                            SuggestionTile(
                                suggestion = item,
                                onClick = {
                                    showSuggestions = false
                                    onSuggestionSelected(item)
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SidebarLight)
                        .clickable {
                            showSuggestions = false
                            onSearchSubmitted()
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "View all results",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SidebarDark
                    )
                    Icon(MtanavIcons.ArrowForward, contentDescription = null, tint = SidebarDark, modifier = Modifier.size(13.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // MENU ITEM NAVIGATION LIST
    Text(
        text = "EXPLORE CATEGORIES",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = SurfaceWhite.copy(alpha = 0.65f),
        letterSpacing = 1.2.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        MenuNavItem(
            title = "Products",
            subtitle = "Spoons, rice, phones, chargers, shoes",
            icon = MtanavIcons.ShoppingBag,
            onClick = onOpenProducts
        )

        MenuNavItem(
            title = "Services",
            subtitle = "Garage, car wash, salon, phone repair",
            icon = MtanavIcons.Build,
            onClick = onOpenServices
        )

        MenuNavItem(
            title = "Promotions",
            subtitle = "Flash sales, discounts & genuine deals",
            icon = MtanavIcons.LocalOffer,
            onClick = onOpenPromotions
        )

        MenuNavItem(
            title = "Local Jobs",
            subtitle = "Delivery, sales, technical & trade work",
            icon = MtanavIcons.Work,
            onClick = onOpenJobs
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECONDARY PROMO BANNER: WANT TO EARN
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SidebarAccent.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, SidebarAccent.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenEarn)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Icon(MtanavIcons.Star, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Want to earn with MTANAV?", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                Text("Referrals & creator rewards", fontSize = 10.sp, color = SurfaceWhite.copy(alpha = 0.8f))
            }
            Icon(MtanavIcons.ArrowForward, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun MenuNavItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SidebarPrimary.copy(alpha = 0.35f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp)
        ) {
            Icon(icon, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = SurfaceWhite.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(MtanavIcons.ArrowForward, contentDescription = null, tint = SurfaceWhite.copy(alpha = 0.6f), modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun SuggestionTile(
    suggestion: SearchSuggestionDto,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AppBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.name,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            suggestion.businessName?.let {
                Text(it, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        suggestion.price?.let { p ->
            Text(
                text = FormattingUtils.formatTsh(p),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = SidebarPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ============================================================
// 2. ALL PRODUCTS CONTENT
// ============================================================

@Composable
private fun AllProductsContent(
    searchQuery: String,
    allProducts: List<ProductDto>,
    onSearchChange: (String) -> Unit,
    onProductSelected: (ProductDto) -> Unit,
    onBackClicked: () -> Unit,
    onCloseDrawerRequested: (() -> Unit)? = null
) {
    HeaderBarWithBack(
        backLabel = "Back to Explore",
        onBackClicked = onBackClicked,
        onCloseDrawerRequested = onCloseDrawerRequested
    )

    Text("PRODUCTS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(6.dp))

    Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search products...", fontSize = 11.5.sp, color = TextSecondary) },
            leadingIcon = { Icon(MtanavIcons.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    val filtered = if (searchQuery.isBlank()) allProducts else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        filtered.forEach { item ->
            SimpleNavTile(
                title = item.name,
                subtitle = item.description,
                icon = MtanavIcons.ShoppingBag,
                onClick = { onProductSelected(item) }
            )
        }
    }
}

// ============================================================
// 3. ALL SERVICES CONTENT
// ============================================================

@Composable
private fun AllServicesContent(
    searchQuery: String,
    allServices: List<CategoryDto>,
    onSearchChange: (String) -> Unit,
    onServiceSelected: (CategoryDto) -> Unit,
    onBackClicked: () -> Unit,
    onCloseDrawerRequested: (() -> Unit)? = null
) {
    HeaderBarWithBack(
        backLabel = "Back to Explore",
        onBackClicked = onBackClicked,
        onCloseDrawerRequested = onCloseDrawerRequested
    )

    Text("SERVICES", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(6.dp))

    Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search services...", fontSize = 11.5.sp, color = TextSecondary) },
            leadingIcon = { Icon(MtanavIcons.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    val filtered = if (searchQuery.isBlank()) allServices else allServices.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        filtered.forEach { item ->
            SimpleNavTile(
                title = item.name,
                subtitle = item.description,
                icon = MtanavIcons.Build,
                onClick = { onServiceSelected(item) }
            )
        }
    }
}

// ============================================================
// 4. RESULTS CONTENT & FILTERING
// ============================================================

@Composable
private fun ResultsContent(
    title: String,
    backLabel: String,
    resultCount: Int,
    filterState: FilterState,
    isLoading: Boolean,
    error: String?,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilters: () -> Unit,
    onBackClicked: () -> Unit,
    onCloseDrawerRequested: (() -> Unit)? = null
) {
    var draftFilter by remember(filterState) { mutableStateOf(filterState) }

    HeaderBarWithBack(
        backLabel = backLabel,
        onBackClicked = onBackClicked,
        onCloseDrawerRequested = onCloseDrawerRequested
    )

    Text(title.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Black, color = SurfaceWhite)
    Spacer(modifier = Modifier.height(4.dp))

    Surface(shape = CircleShape, color = SurfaceWhite.copy(alpha = 0.15f)) {
        Text(
            text = "$resultCount locations found",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SidebarAccent,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (isLoading) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = SurfaceWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Updating locations...", fontSize = 11.sp, color = SurfaceWhite)
        }
    }

    error?.let { err ->
        Surface(shape = RoundedCornerShape(6.dp), color = ErrorRed.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
            Text(err, color = SurfaceWhite, fontSize = 11.sp, modifier = Modifier.padding(6.dp))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // FILTER SECTION
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("FILTER RESULTS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite.copy(alpha = 0.65f), letterSpacing = 1.2.sp)

        // Max budget
        var budgetText by remember(draftFilter.maxPrice) { mutableStateOf(draftFilter.maxPrice?.toInt()?.toString() ?: "") }
        Column {
            Text("Maximum Price", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            Spacer(modifier = Modifier.height(3.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = SurfaceWhite) {
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = {
                        budgetText = it
                        draftFilter = draftFilter.copy(maxPrice = it.toDoubleOrNull())
                    },
                    placeholder = { Text("TSh e.g. 10000", fontSize = 11.sp, color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                )
            }
        }

        // Distance filter chips
        Column {
            Text("Distance Radius", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            Spacer(modifier = Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf(1.0 to "1 km", 3.0 to "3 km", 5.0 to "5 km", null to "Any").forEach { (dist, label) ->
                    val isSelected = draftFilter.distanceKm == dist
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) SidebarAccent else SurfaceWhite.copy(alpha = 0.2f))
                            .clickable { draftFilter = draftFilter.copy(distanceKm = dist) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(label, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isSelected) SidebarDark else SurfaceWhite)
                    }
                }
            }
        }

        // Sort filter chips
        Column {
            Text("Sort Order", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            Spacer(modifier = Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                SortOption.entries.forEach { option ->
                    val isSelected = draftFilter.sortOption == option
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) SidebarPrimary else SurfaceWhite.copy(alpha = 0.2f))
                            .clickable { draftFilter = draftFilter.copy(sortOption = option) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(option.label, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { onApplyFilter(draftFilter) },
            colors = ButtonDefaults.buttonColors(containerColor = SidebarAccent),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            Text("APPLY", color = SidebarDark, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }

        Button(
            onClick = {
                draftFilter = FilterState()
                onClearFilters()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            Text("CLEAR", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HeaderBarWithBack(
    backLabel: String,
    onBackClicked: () -> Unit,
    onCloseDrawerRequested: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(MtanavIcons.ArrowBack, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(backLabel, color = SidebarAccent, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        onCloseDrawerRequested?.let { onClose ->
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                Icon(MtanavIcons.Close, contentDescription = "Close", tint = SurfaceWhite, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SimpleNavTile(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceWhite.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Icon(icon, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = SurfaceWhite, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            subtitle?.let { Text(it, fontSize = 10.sp, color = SurfaceWhite.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Icon(MtanavIcons.ArrowForward, contentDescription = null, tint = SidebarAccent, modifier = Modifier.size(12.dp))
    }
}
