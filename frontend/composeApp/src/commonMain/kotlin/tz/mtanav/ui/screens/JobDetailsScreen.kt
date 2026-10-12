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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.JobDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@Composable
fun JobDetailsScreen(
    jobId: Long,
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    onAuthRequested: () -> Unit,
    onApplicationSubmitted: () -> Unit,
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
            } catch (e: Exception) {
                submitError = e.message ?: "Failed to submit application."
            } finally {
                isSubmitting = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // HEADER
        Surface(color = Primary, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                TextButton(onClick = onBackClicked) {
                    Text("← Back to Jobs", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                // MAIN CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(j.title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Text(j.businessName ?: "Employer #${j.posterId}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Primary)
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
                                val payDisplay = when {
                                    j.payAmountMin != null && j.payAmountMax != null ->
                                        "TSh ${j.payAmountMin.toInt()} - ${j.payAmountMax.toInt()} / ${j.payFrequency ?: "month"}"
                                    j.budgetAmount > 0 -> "TSh ${j.budgetAmount.toInt()} (${j.payFrequency ?: "fixed"})"
                                    else -> "Not specified"
                                }
                                Text(payDisplay, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Action)
                            }

                            Column {
                                Text("APPLICATION DEADLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Text(j.applicationDeadline ?: "Open until filled", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                // DESCRIPTION & RESPONSIBILITIES & QUALIFICATIONS
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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

                // APPLY ACTION CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Interested in this job position?", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Submit your application directly through MTANAV.", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (repository.currentUser == null) {
                            Button(
                                onClick = onAuthRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign In or Register to Apply", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Button(
                                onClick = { showApplyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Action),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("APPLY FOR THIS JOB", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
