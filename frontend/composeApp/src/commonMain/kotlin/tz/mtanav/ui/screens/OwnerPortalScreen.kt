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
    onNavigateToPublicMap: () -> Unit,
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
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load owner portal data."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOwnerData()
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // OWNER SIDEBAR
        OwnerSidebar(
            activeSection = activeSection,
            business = selectedBusiness,
            onSectionSelected = { activeSection = it },
            onNavigateToPublicMap = onNavigateToPublicMap,
            onLogoutClicked = onLogoutClicked,
            modifier = Modifier.width(300.dp).fillMaxHeight()
        )

        VerticalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.fillMaxHeight().width(1.dp))

        // OWNER CONTENT AREA (NO GOOGLE MAP)
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
                            text = "Business Owner Portal • ${selectedBusiness?.name ?: "MTANAV Business"}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { activeSection = OwnerSection.PostProductService },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("➕ POST PRODUCT OR SERVICE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

                // CONTENT ROUTING
                when (activeSection) {
                    OwnerSection.Dashboard -> OwnerDashboardContent(
                        business = selectedBusiness,
                        productsCount = products.size,
                        servicesCount = services.size,
                        ordersCount = orders.size,
                        bookingsCount = bookings.size,
                        onNavigateSection = { activeSection = it }
                    )

                    OwnerSection.MyProducts -> OwnerProductsContent(
                        products = products,
                        business = selectedBusiness
                    )

                    OwnerSection.MyServices -> OwnerServicesContent(
                        services = services,
                        business = selectedBusiness
                    )

                    OwnerSection.MyBusiness -> OwnerBusinessDetailsContent(
                        business = selectedBusiness
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
                        onPhotosUpdated = { loadOwnerData() }
                    )

                    OwnerSection.Orders -> OwnerOrdersContent(
                        orders = orders,
                        repository = repository,
                        onStatusChanged = { loadOwnerData() }
                    )

                    OwnerSection.Bookings -> OwnerBookingsContent(
                        bookings = bookings,
                        repository = repository,
                        onStatusChanged = { loadOwnerData() }
                    )

                    OwnerSection.Payments -> OwnerPaymentsContent(orders = orders, bookings = bookings)

                    OwnerSection.Verification -> OwnerVerificationContent(business = selectedBusiness)

                    OwnerSection.Settings -> OwnerSettingsContent()

                    OwnerSection.PostProductService -> OwnerPostProductServiceContent(
                        business = selectedBusiness,
                        repository = repository,
                        onSuccess = {
                            message = "Item posted successfully!"
                            loadOwnerData()
                            activeSection = OwnerSection.MyProducts
                        }
                    )
                }
            }
        }
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
                .padding(20.dp)
        ) {
            Text("MTANAV", fontSize = 22.sp, fontWeight = FontWeight.Black, color = SurfaceWhite, letterSpacing = 1.sp)
            Text("BUSINESS OWNER PORTAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HighlightGold, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            business?.let {
                Surface(shape = RoundedCornerShape(8.dp), color = SurfaceWhite.copy(alpha = 0.15f)) {
                    Text("🏪 ${it.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
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
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = section.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SurfaceWhite else TextPrimary
                        )
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

            TextButton(onClick = onLogoutClicked, modifier = Modifier.fillMaxWidth()) {
                Text("Sign out", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    onNavigateSection: (OwnerSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("BUSINESS OWNER OVERVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text(business?.name ?: "My Shop / Service Yard", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text("Status: ${business?.verificationStatus ?: "PENDING"}", fontSize = 13.sp, color = ActionTeal, fontWeight = FontWeight.Bold)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OwnerStatCard("PRODUCTS", "$productsCount items", "Listed for sale", ActionTeal, modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyProducts) })
            OwnerStatCard("SERVICES", "$servicesCount services", "Available for booking", PrimaryBlue, modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.MyServices) })
            OwnerStatCard("ORDERS", "$ordersCount pending", "Customer purchases", PrimaryDeepBlue, modifier = Modifier.weight(1f).clickable { onNavigateSection(OwnerSection.Orders) })
        }
    }
}

@Composable
private fun OwnerStatCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
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
                Text("➕ Post New Job", fontWeight = FontWeight.Bold)
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
                    Text("No jobs posted yet. Click 'Post New Job' to recruit workers.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            ownerJobs.forEach { job ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text("${job.category} • ${job.location}", fontSize = 12.sp, color = TextSecondary)
                                Text("Budget / Salary: TSh ${job.budgetAmount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Action)
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
                                Text("View Applications", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                Text(if (job.publicationStatus == "PUBLISHED") "Pause Job" else "Publish Job", fontSize = 11.sp)
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
                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Description") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = locationInput, onValueChange = { locationInput = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = salaryInput, onValueChange = { salaryInput = it }, label = { Text("Monthly Budget / Salary (TZS)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qualificationsInput, onValueChange = { qualificationsInput = it }, label = { Text("Required Qualifications") }, modifier = Modifier.fillMaxWidth())
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
                Text("➕ Create Promotion", fontWeight = FontWeight.Bold)
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
                    Text("No promotions created yet. Create discounts to attract customers.", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            ownerPromos.forEach { promo ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(promo.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text("Category: ${promo.category} • Offer Type: ${promo.offerType}", fontSize = 12.sp, color = TextSecondary)
                                promo.promotionalPrice?.let {
                                    Text("Promo Price: TSh ${it.toInt()} (${promo.discountPercentage ?: 0}% OFF)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Action)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (promo.status == "ACTIVE") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                            ) {
                                Text(promo.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (promo.status == "ACTIVE") SuccessGreen else ErrorRed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val next = if (promo.status == "ACTIVE") "PAUSED" else "ACTIVE"
                                        repository.updatePromotionStatus(promo.id, next)
                                        loadPromos()
                                    }
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (promo.status == "ACTIVE") "Pause Campaign" else "Activate Campaign", fontSize = 11.sp)
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
                        OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Promotion Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Description") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = origPriceInput, onValueChange = { origPriceInput = it }, label = { Text("Original Price (TZS)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = promoPriceInput, onValueChange = { promoPriceInput = it }, label = { Text("Promotional Price (TZS)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = termsInput, onValueChange = { termsInput = it }, label = { Text("Terms & Conditions") }, modifier = Modifier.fillMaxWidth())
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
// OTHER OWNER CONTENT SECTIONS
// ==========================================

@Composable private fun OwnerProductsContent(products: List<BusinessProductDto>, business: BusinessDto?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY PRODUCTS (${products.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        products.forEach { p ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(p.productName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("SKU: ${p.sku ?: "N/A"} • Available: ${p.availableQuantity}", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("TSh ${p.price.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable private fun OwnerServicesContent(services: List<BusinessServiceDto>, business: BusinessDto?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MY SERVICES (${services.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        services.forEach { s ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(s.serviceName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Duration: ${s.durationMinutes} min", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("TSh ${s.price.toInt()}", fontWeight = FontWeight.Black, color = ActionTeal, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable private fun OwnerBusinessDetailsContent(business: BusinessDto?) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("BUSINESS PROFILE DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Name: ${business?.name ?: "N/A"}", fontWeight = FontWeight.Bold)
            Text("Address: ${business?.address ?: "N/A"}")
            Text("City: ${business?.city ?: "Dar es Salaam"}")
            Text("Verification: ${business?.verificationStatus ?: "PENDING"}")
        }
    }
}

@Composable private fun OwnerPhotosContent(business: BusinessDto?, photoUsage: PhotoUsageDto?, repository: MtanavRepository, onPhotosUpdated: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("PHOTO ALLOWANCE & MANAGEMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Current Photos: ${photoUsage?.currentPhotoCount ?: 5} / ${photoUsage?.maxAllowedPhotos ?: 5}")
        }
    }
}

@Composable private fun OwnerOrdersContent(orders: List<OrderDto>, repository: MtanavRepository, onStatusChanged: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("CUSTOMER ORDERS (${orders.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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

@Composable private fun OwnerBookingsContent(bookings: List<BookingDto>, repository: MtanavRepository, onStatusChanged: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("SERVICE BOOKINGS (${bookings.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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

@Composable private fun OwnerPaymentsContent(orders: List<OrderDto>, bookings: List<BookingDto>) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("OWNER PAYMENTS & REVENUE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("Revenue: TSh ${(orders.sumOf { it.totalAmount } + bookings.sumOf { it.price }).toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ActionTeal)
        }
    }
}

@Composable private fun OwnerVerificationContent(business: BusinessDto?) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("VERIFICATION STATUS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("Status: ${business?.verificationStatus ?: "VERIFIED"}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ActionTeal)
        }
    }
}

@Composable private fun OwnerSettingsContent() {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("BUSINESS SETTINGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text("• Automatic Order Confirmation: Enabled")
        }
    }
}

@Composable private fun OwnerPostProductServiceContent(business: BusinessDto?, repository: MtanavRepository, onSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var itemType by remember { mutableStateOf("PRODUCT") }
    val coroutineScope = rememberCoroutineScope()

    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("POST NEW PRODUCT OR SERVICE", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { itemType = "PRODUCT" }, colors = ButtonDefaults.buttonColors(containerColor = if (itemType == "PRODUCT") PrimaryDeepBlue else AppBackground)) {
                    Text("Product", color = if (itemType == "PRODUCT") SurfaceWhite else TextPrimary)
                }
                Button(onClick = { itemType = "SERVICE" }, colors = ButtonDefaults.buttonColors(containerColor = if (itemType == "SERVICE") PrimaryDeepBlue else AppBackground)) {
                    Text("Service", color = if (itemType == "SERVICE") SurfaceWhite else TextPrimary)
                }
            }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Price (TZS)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    coroutineScope.launch {
                        business?.let { b ->
                            if (itemType == "PRODUCT") {
                                repository.createProduct(b.id, b.name, name, desc, priceText.toDoubleOrNull() ?: 10000.0, 50, emptyList())
                            } else {
                                repository.createService(b.id, b.name, name, desc, priceText.toDoubleOrNull() ?: 10000.0, 45)
                            }
                            onSuccess()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("POST TO MTANAV", fontWeight = FontWeight.Bold)
            }
        }
    }
}
