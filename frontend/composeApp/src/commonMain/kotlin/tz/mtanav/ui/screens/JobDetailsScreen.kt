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
import tz.mtanav.model.JobDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.util.FormattingUtils
import tz.mtanav.util.UrgencyLevel

@Composable
fun JobDetailsScreen(
    jobId: Long,
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    onAuthRequested: () -> Unit,
    onApplicationSubmitted: () -> Unit,
    onViewShopClicked: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var job by remember { mutableStateOf<JobDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showApplyDialog by remember { mutableStateOf(false) }
    var proposedRateText by remember { mutableStateOf("") }
    var coverNoteText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    var submitSuccessMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun loadJob() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val fetched = repository.getJobDetails(jobId)
                job = fetched
                proposedRateText = fetched.budgetAmount.toInt().toString()
            } catch (e: Exception) {
                errorMessage = "Failed to load job details."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(jobId) {
        loadJob()
    }

    fun submitApplication() {
        coroutineScope.launch {
            isSubmitting = true
            submitError = null
            try {
                val rate = proposedRateText.toDoubleOrNull() ?: (job?.budgetAmount ?: 0.0)
                repository.applyForJob(
                    jobId = jobId,
                    proposedRate = rate,
                    coverNote = coverNoteText
                )
                submitSuccessMessage = "Application submitted successfully! Track status in your Customer Dashboard."
                showApplyDialog = false
                onApplicationSubmitted()
            } catch (e: Exception) {
                submitError = e.message ?: "Failed to submit application."
            } finally {
                isSubmitting = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        val isDesktop = maxWidth >= 1024.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // HEADER
            Surface(color = Primary, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    TextButton(onClick = onBackClicked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(MtanavIcons.ArrowBack, contentDescription = "Back", tint = Accent, modifier = Modifier.size(16.dp))
                            Text("Back to Jobs", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("JOB DETAILS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Surface)
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage ?: "", color = ErrorRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { loadJob() }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                            Text("Retry")
                        }
                    }
                }
            } else job?.let { j ->
                val compensationStr = FormattingUtils.formatJobCompensation(j)
                val deadlineDateStr = FormattingUtils.formatDateReadable(j.applicationDeadline, fallback = "Open until filled")
                val deadlineUrgency = FormattingUtils.getUrgencyTag(j.applicationDeadline, isDeadline = true)
                val postedDateStr = FormattingUtils.formatDateReadable(j.createdAt, fallback = "Recently")

                Column(modifier = Modifier.weight(1f)) {
                    // SCROLLABLE BODY
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        submitSuccessMessage?.let { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✅ $msg", color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (isDesktop) {
                            // DESKTOP TWO-COLUMN SPLIT (Left 65%, Right 35%)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // LEFT COLUMN (65% width): Overview, Description, Responsibilities, Qualifications
                                Column(
                                    modifier = Modifier.weight(0.65f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // TITLE & EMPLOYER CARD
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp)) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(j.title, fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    ) {
                                                        Text(j.businessName ?: "Employer #${j.posterId}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Primary)
                                                        Icon(
                                                            MtanavIcons.CheckCircle,
                                                            contentDescription = "Verified Employer",
                                                            tint = SuccessGreen,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text("VERIFIED EMPLOYER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text("📍 Location: ${j.location}", fontSize = 13.sp, color = TextSecondary)
                                                    Text("📅 Posted: $postedDateStr", fontSize = 12.sp, color = TextSecondary)
                                                }

                                                Surface(shape = RoundedCornerShape(8.dp), color = PrimaryLight) {
                                                    Text(
                                                        text = j.employmentType.replace("_", " "),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PrimaryDark,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // JOB DETAILS CONTENT CARD
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Column {
                                                Text("Job Overview & Description", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(j.description, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                                            }

                                            j.responsibilities?.let { resp ->
                                                HorizontalDivider(color = Border)
                                                Column {
                                                    Text("Key Responsibilities", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(resp, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                                                }
                                            }

                                            j.qualifications?.let { qual ->
                                                HorizontalDivider(color = Border)
                                                Column {
                                                    Text("Qualifications & Requirements", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(qual, fontSize = 13.sp, color = TextPrimary, lineHeight = 20.sp)
                                                }
                                            }

                                            j.contactInstructions?.let { contact ->
                                                HorizontalDivider(color = Border)
                                                Column {
                                                    Text("Contact & Application Instructions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(contact, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                // RIGHT COLUMN (35% width): Sticky Summary & Apply Card
                                Column(
                                    modifier = Modifier.weight(0.35f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text("Job Summary & Compensation", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)

                                            HorizontalDivider(color = Border)

                                            // COMPENSATION
                                            Column {
                                                Text("COMPENSATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Text(compensationStr, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Action)
                                            }

                                            // DEADLINE WITH URGENCY TAG
                                            Column {
                                                Text("APPLICATION DEADLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(deadlineDateStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    deadlineUrgency?.let { tag ->
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
                                            }

                                            // JOB TYPE & LOCATION
                                            Column {
                                                Text("JOB TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Text(j.employmentType.replace("_", " "), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            }

                                            Column {
                                                Text("LOCATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Text("📍 ${j.location}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                            }

                                            j.businessId?.let { busId ->
                                                onViewShopClicked?.let { navShop ->
                                                    OutlinedButton(
                                                        onClick = { navShop(busId) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text("View Employer Shop", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                                                    }
                                                }
                                            }

                                            HorizontalDivider(color = Border)

                                            // APPLY CTA BUTTON
                                            if (repository.currentUser == null) {
                                                Button(
                                                    onClick = onAuthRequested,
                                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                                ) {
                                                    Text("Sign In or Register to Apply", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                }
                                            } else {
                                                Button(
                                                    onClick = { showApplyDialog = true },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Action),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                                ) {
                                                    Text("APPLY FOR THIS JOB", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // MOBILE SINGLE COLUMN LAYOUT (<1024px)
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // MAIN HEADER CARD
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(j.title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    Text(j.businessName ?: "Employer #${j.posterId}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary)
                                                    Icon(
                                                        MtanavIcons.CheckCircle,
                                                        contentDescription = "Verified Employer",
                                                        tint = SuccessGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Text("VERIFIED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("📍 Location: ${j.location}", fontSize = 13.sp, color = TextSecondary)
                                            }

                                            Surface(shape = RoundedCornerShape(8.dp), color = PrimaryLight) {
                                                Text(
                                                    text = j.employmentType.replace("_", " "),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryDark,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }

                                        HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 16.dp))

                                        // PAY & DEADLINE METRICS
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("COMPENSATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Text(compensationStr, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Action)
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("APPLICATION DEADLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                                Text(deadlineDateStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                deadlineUrgency?.let { tag ->
                                                    val (badgeBg, badgeFg) = when (tag.level) {
                                                        UrgencyLevel.EXPIRED -> Pair(ErrorRed.copy(alpha = 0.15f), ErrorRed)
                                                        UrgencyLevel.URGENT -> Pair(Accent.copy(alpha = 0.2f), Warning)
                                                        UrgencyLevel.NORMAL -> Pair(PrimaryLight, PrimaryDark)
                                                    }
                                                    Surface(shape = RoundedCornerShape(4.dp), color = badgeBg, modifier = Modifier.padding(top = 2.dp)) {
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
                                        }
                                    }
                                }

                                // OVERVIEW & RESPONSIBILITIES CARD
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Column {
                                            Text("Job Overview & Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(j.description, fontSize = 13.sp, color = TextPrimary)
                                        }

                                        j.responsibilities?.let { resp ->
                                            Column {
                                                Text("Key Responsibilities", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(resp, fontSize = 13.sp, color = TextPrimary)
                                            }
                                        }

                                        j.qualifications?.let { qual ->
                                            Column {
                                                Text("Qualifications & Requirements", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(qual, fontSize = 13.sp, color = TextPrimary)
                                            }
                                        }

                                        j.contactInstructions?.let { contact ->
                                            Column {
                                                Text("Contact & Application Instructions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(contact, fontSize = 13.sp, color = TextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // STICKY MOBILE APPLY CTA (<1024px)
                    if (!isDesktop) {
                        Surface(
                            shadowElevation = 8.dp,
                            color = Surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (repository.currentUser == null) {
                                    Button(
                                        onClick = onAuthRequested,
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Text("Sign In or Register to Apply", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = { showApplyDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Action),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Text("APPLY FOR THIS JOB", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // APPLICATION MODAL DIALOG
        if (showApplyDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) showApplyDialog = false },
                title = { Text("Apply for Job: ${job?.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        submitError?.let { err ->
                            Text(err, color = ErrorRed, fontSize = 12.sp)
                        }

                        Text("Applicant: ${repository.currentUser?.fullName} (${repository.currentUser?.email})", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                        OutlinedTextField(
                            value = proposedRateText,
                            onValueChange = { proposedRateText = it },
                            label = { Text("Proposed Rate / Salary (TZS)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = coverNoteText,
                            onValueChange = { coverNoteText = it },
                            label = { Text("Cover Note / Why you are a good fit") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { submitApplication() },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = Action)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Surface, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("SUBMIT APPLICATION", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showApplyDialog = false },
                        enabled = !isSubmitting
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
