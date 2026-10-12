package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import tz.mtanav.model.JobDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocalJobsScreen(
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    onJobSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var jobs by remember { mutableStateOf<List<JobDto>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedEmploymentType by remember { mutableStateOf<String?>(null) }
    var locationQuery by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun fetchJobs() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                jobs = repository.getJobs(
                    query = searchQuery.ifBlank { null },
                    category = selectedCategory,
                    employmentType = selectedEmploymentType,
                    location = locationQuery.ifBlank { null }
                )
            } catch (e: Exception) {
                errorMessage = "Failed to load local jobs. Please check connection and retry."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            categories = listOf(
                "All Categories",
                "Delivery and logistics",
                "Sales and marketing",
                "Technology and IT",
                "Repairs and technical work",
                "Construction and trades",
                "Hospitality and food services",
                "Cleaning and household services",
                "Retail and shop assistance",
                "Office and administrative work",
                "Temporary and casual work"
            )
        } catch (e: Exception) { /* ignored */ }
        fetchJobs()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // TOP APP BAR
        Surface(
            color = Primary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                TextButton(onClick = onBackClicked) {
                    Text("← Back", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("LOCAL JOBS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Surface)
                    Text("Connect with employers & earn with MTANAV", fontSize = 12.sp, color = Surface.copy(alpha = 0.8f))
                }
            }
        }

        // CONTENT CONTAINER
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // SEARCH & FILTER BAR
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by title, skill or keyword...", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )

                        OutlinedTextField(
                            value = locationQuery,
                            onValueChange = { locationQuery = it },
                            placeholder = { Text("Filter location...", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )

                        Button(
                            onClick = { fetchJobs() },
                            colors = ButtonDefaults.buttonColors(containerColor = Action),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SEARCH JOBS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // CATEGORIES CHIPS
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { cat ->
                            val isSelected = (cat == "All Categories" && selectedCategory == null) || (cat == selectedCategory)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Primary else PrimaryLight)
                                    .clickable {
                                        selectedCategory = if (cat == "All Categories") null else cat
                                        fetchJobs()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Surface else PrimaryDark
                                )
                            }
                        }
                    }

                    // EMPLOYMENT TYPE CHIPS
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Job Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        listOf("All" to null, "Full Time" to "FULL_TIME", "Part Time" to "PART_TIME", "Contract" to "CONTRACT", "Casual" to "CASUAL").forEach { (label, type) ->
                            val isSel = selectedEmploymentType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Action else Surface)
                                    .border(1.dp, Border, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedEmploymentType = type
                                        fetchJobs()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Surface else TextPrimary)
                            }
                        }
                    }
                }
            }

            // STATES
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
                        Button(onClick = { fetchJobs() }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                            Text("Retry", fontSize = 12.sp)
                        }
                    }
                }
            } else if (jobs.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💼", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No jobs found matching your criteria.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Try adjusting your search terms or filters.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                Text(
                    text = "${jobs.size} local job opportunities available",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(jobs) { job ->
                        JobCardItem(job = job, onClick = { onJobSelected(job.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun JobCardItem(job: JobDto, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = job.businessName ?: "Employer #${job.posterId}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryLight
                ) {
                    Text(
                        text = job.employmentType.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = job.description,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍 ${job.location}", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)

                    val payDisplay = when {
                        job.payAmountMin != null && job.payAmountMax != null ->
                            "TSh ${job.payAmountMin.toInt()} - ${job.payAmountMax.toInt()} / ${job.payFrequency ?: "month"}"
                        job.budgetAmount > 0 -> "TSh ${job.budgetAmount.toInt()} (${job.payFrequency ?: "fixed"})"
                        else -> "Compensation: Not specified"
                    }
                    Text(text = "💰 $payDisplay", fontSize = 12.sp, color = Action, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View & Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
