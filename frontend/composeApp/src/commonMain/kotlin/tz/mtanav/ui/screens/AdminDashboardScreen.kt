package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.*
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@Composable
fun AdminDashboardScreen(
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser = repository.currentUser
    val isSuperAdmin = currentUser?.roles?.contains("ROLE_SUPER_ADMIN") == true

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Verified, 2: Rejected, 3: Suspended, 4: Audit Logs, 5: SuperAdmin
    var businesses by remember { mutableStateOf<List<BusinessDto>>(emptyList()) }
    var auditLogs by remember { mutableStateOf<List<AuditLogDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val statusFilter = when (selectedTab) {
                    0 -> "PENDING"
                    1 -> "VERIFIED"
                    2 -> "REJECTED"
                    3 -> "SUSPENDED"
                    else -> null
                }

                if (selectedTab in 0..3) {
                    businesses = repository.getAdminBusinesses(statusFilter)
                } else if (selectedTab == 4) {
                    auditLogs = repository.getAuditLogs()
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load admin data."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column {
                TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
                    Text("← Back to Map", color = PrimaryDeepBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Text("ADMINISTRATION PANEL", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }

            Surface(shape = RoundedCornerShape(8.dp), color = PrimaryDeepBlue) {
                Text(
                    text = if (isSuperAdmin) "SUPER ADMIN" else "ADMIN",
                    color = SurfaceWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = SurfaceWhite,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Pending Verification", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Verified Businesses", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("Rejected", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                Text("Suspended", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }) {
                Text("Audit Logs", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            if (isSuperAdmin) {
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }) {
                    Text("Super Admin", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ActionTeal)
                }
            }
        }

        errorMessage?.let { err ->
            Surface(shape = RoundedCornerShape(8.dp), color = ErrorRed.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Text(err, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }

        message?.let { msg ->
            Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Text(msg, color = SuccessGreen, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }

        if (selectedTab in 0..3) {
            if (businesses.isEmpty() && !isLoading) {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                        Text("No businesses in this queue", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            } else {
                businesses.forEach { business ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(business.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("📍 ${business.address}, ${business.city}", fontSize = 13.sp, color = TextSecondary)
                                    business.buildingName?.let { Text("🏢 Building: $it", fontSize = 12.sp, color = PrimaryBlue) }
                                    business.phone?.let { Text("📞 $it • ✉️ ${business.email ?: "N/A"}", fontSize = 12.sp, color = TextSecondary) }
                                }

                                Surface(shape = RoundedCornerShape(6.dp), color = PrimaryDeepBlue.copy(alpha = 0.08f)) {
                                    Text(
                                        text = business.verificationStatus,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDeepBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 12.dp))

                            // Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (business.verificationStatus != "VERIFIED") {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                try {
                                                    repository.updateVerificationStatus(business.id, "VERIFIED")
                                                    message = "Business #${business.id} has been VERIFIED!"
                                                    loadData()
                                                } catch (e: Exception) {
                                                    errorMessage = "Update failed."
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("APPROVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (business.verificationStatus != "REJECTED") {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                try {
                                                    repository.updateVerificationStatus(business.id, "REJECTED")
                                                    message = "Business #${business.id} has been REJECTED."
                                                    loadData()
                                                } catch (e: Exception) {
                                                    errorMessage = "Update failed."
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("REJECT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (business.verificationStatus != "SUSPENDED") {
                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                try {
                                                    repository.updateVerificationStatus(business.id, "SUSPENDED")
                                                    message = "Business #${business.id} has been SUSPENDED."
                                                    loadData()
                                                } catch (e: Exception) {
                                                    errorMessage = "Update failed."
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("SUSPEND", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ErrorRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 4) {
            // AUDIT LOGS
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PLATFORM AUDIT LOGS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (auditLogs.isEmpty()) {
                        Text("No audit logs recorded yet.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        auditLogs.forEach { log ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${log.action} • ${log.entity} #${log.entityId ?: ""}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    log.details?.let { Text(it, fontSize = 11.sp, color = TextSecondary) }
                                }
                                log.createdAt?.let { Text(it, fontSize = 10.sp, color = TextSecondary) }
                            }
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                        }
                    }
                }
            }
        } else if (selectedTab == 5 && isSuperAdmin) {
            // SUPER ADMIN CONTROL PANEL
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SUPER ADMIN PLATFORM CONTROLS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                    Text("Manage administrator accounts, platform pricing rules, and global moderation.", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(shape = RoundedCornerShape(8.dp), color = AppBackground, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Configurable Photo Policy Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("• Free Photo Upload Allowance: 5 photos per business", fontSize = 12.sp, color = TextSecondary)
                            Text("• Additional Photo Cost: TSh 10 per extra photo", fontSize = 12.sp, color = TextSecondary)
                            Text("• Payment Abstraction: Active (Mock Tanzanian M-Pesa / TigoPesa / Airtel Money)", fontSize = 12.sp, color = ActionTeal)
                        }
                    }
                }
            }
        }
    }
}
