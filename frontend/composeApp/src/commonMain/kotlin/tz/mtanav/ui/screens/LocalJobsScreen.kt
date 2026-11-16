package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.JobDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.util.FormattingUtils
import tz.mtanav.util.UrgencyLevel

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
        categories = listOf(
            "All Categories",
            "Delivery & Logistics",
            "Technology",
            "Marketing",
            "Sales",
            "Hospitality",
            "Repairs & Technical",
            "Retail & Shop Assistance",
            "Office & Admin",
            "Casual & Temporary"
        )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(MtanavIcons.ArrowBack, contentDescription = "Back", tint = Accent, modifier = Modifier.size(16.dp))
                        Text("Back", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("LOCAL JOBS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Surface)
                    Text("Connect with verified employers & earn with MTANAV", fontSize = 12.sp, color = Surface.copy(alpha = 0.85f))
                }
            }
        }

        // CONTENT CONTAINER WITH RESPONSIVE CONSTRAINTS
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val isDesktop = maxWidth >= 1024.dp
            val isMobile = maxWidth < 768.dp

            Column(modifier = Modifier.fillMaxSize()) {
                // SEARCH & FILTER BAR
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // STACKED ON MOBILE (<768px), SIDE-BY-SIDE ON TABLET/DESKTOP
                        if (isMobile) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search by title, skill or keyword...", fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                                )

                                OutlinedTextField(
                                    value = locationQuery,
                                    onValueChange = { locationQuery = it },
                                    placeholder = { Text("Filter location (e.g. Dar es Salaam)...", fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                                )

                                Button(
                                    onClick = { fetchJobs() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Action),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Icon(MtanavIcons.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Surface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SEARCH JOBS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        } else {
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
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Icon(MtanavIcons.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Surface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SEARCH JOBS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        // HORIZONTAL SCROLLABLE CATEGORIES PILLS (<768px and up)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 2.dp)
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
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
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
                        }

                        // HORIZONTAL SCROLLABLE EMPLOYMENT TYPE CHIPS
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Job Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            val jobTypes = listOf(
                                "All" to null,
                                "Full-time" to "FULL_TIME",
                                "Part-time" to "PART_TIME",
                                "Contract" to "CONTRACT",
                                "Temporary" to "TEMPORARY",
                                "Internship" to "INTERNSHIP",
                                "Freelance" to "FREELANCE",
                                "Casual" to "CASUAL"
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 2.dp)
                            ) {
                                jobTypes.forEach { (label, type) ->
                                    val isSel = (type == null && selectedEmploymentType == null) || (selectedEmploymentType == type)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Action else Surface)
                                            .border(1.dp, if (isSel) Action else Border, RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedEmploymentType = type
                                                fetchJobs()
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Surface else TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                // STATES & RESULTS GRID
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
                            Icon(MtanavIcons.Work, contentDescription = null, tint = Primary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No jobs found matching your criteria.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Try adjusting your search terms or filter selection.", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                } else {
                    // DYNAMIC RESULT COUNT HEADING
                    Text(
                        text = "${jobs.size} local job opportunities available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 2-COLUMN GRID ON DESKTOP (>=1024px), 1-COLUMN ON MOBILE/TABLET
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isDesktop) 2 else 1),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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
}

@Composable
private fun JobCardItem(job: JobDto, onClick: () -> Unit) {
    val compensationStr = FormattingUtils.formatJobCompensation(job)
    val urgencyTag = FormattingUtils.getUrgencyTag(job.applicationDeadline, isDeadline = true)

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // HEADER: TITLE + VERIFIED BADGE + TYPE BADGE
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = job.businessName ?: "Employer #${job.posterId}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary
                        )
                        Icon(
                            MtanavIcons.CheckCircle,
                            contentDescription = "Verified",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "VERIFIED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

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

            // DESCRIPTION (2-line clamped)
            Text(
                text = job.description,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Border, modifier = Modifier.padding(bottom = 10.dp))

            // DETAILS: LOCATION, COMPENSATION & DEADLINE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(MtanavIcons.LocationOn, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(13.dp))
                            Text(text = job.location, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }

                        urgencyTag?.let { tag ->
                            val (badgeBg, badgeFg) = when (tag.level) {
                                UrgencyLevel.EXPIRED -> Pair(ErrorRed.copy(alpha = 0.15f), ErrorRed)
                                UrgencyLevel.URGENT -> Pair(Accent.copy(alpha = 0.2f), Warning)
                                UrgencyLevel.NORMAL -> Pair(PrimaryLight, PrimaryDark)
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = badgeBg) {
                                Text(
                                    text = tag.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeFg,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(MtanavIcons.LocalOffer, contentDescription = null, tint = Action, modifier = Modifier.size(13.dp))
                        Text(
                            text = compensationStr,
                            fontSize = 12.sp,
                            color = Action,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("View & Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
