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
import tz.mtanav.model.CreateBusinessRequest
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@Composable
fun BusinessRegistrationScreen(
    repository: MtanavRepository,
    onSubmittedSuccess: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) } // 1: Info, 2: Location Form, 3: Photos/Review

    var businessName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("Shop") } // Shop, Service, Both
    var description by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var supportsDelivery by remember { mutableStateOf(true) }
    var supportsPickup by remember { mutableStateOf(true) }

    // Pure Form Location Fields (NO GOOGLE MAP)
    var region by remember { mutableStateOf("Dar es Salaam") }
    var district by remember { mutableStateOf("Ilala") }
    var ward by remember { mutableStateOf("Kariakoo") }
    var street by remember { mutableStateOf("") }
    var buildingName by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var shopNumber by remember { mutableStateOf("") }

    var photoUrl by remember { mutableStateOf("") }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(40.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
                        Text("← Back", color = PrimaryDeepBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (isSubmitted) "STATUS: PENDING" else "Step $step of 3",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActionTeal
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSubmitted) "Business Registration Submitted" else "Register Business Location",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryDeepBlue
                )
                Text(
                    text = "MTANAV Business Onboarding Portal",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                errorMessage?.let { err ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ErrorRed.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                    ) {
                        Text(err, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(14.dp))
                    }
                }

                if (isSubmitted) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    ) {
                        Text("⏳", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Awaiting Verification",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryDeepBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Your business details have been submitted successfully. An administrator will verify your shop or service profile before it appears live on public MTANAV discovery.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onSubmittedSuccess,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("OPEN OWNER PORTAL DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } else {
                    when (step) {
                        1 -> {
                            Text("1. BUSINESS & CONTACT INFORMATION", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { businessName = it },
                                label = { Text("Business / Shop Name *") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("BUSINESS TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf("Shop / Products", "Service Provider", "Both Shop & Service").forEach { type ->
                                    val isSelected = businessType == type
                                    Button(
                                        onClick = { businessType = type },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDeepBlue else AppBackground),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(type, color = if (isSelected) SurfaceWhite else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Business Description & Offerings") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone Number *") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = whatsapp,
                                    onValueChange = { whatsapp = it },
                                    label = { Text("WhatsApp (optional)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Business Email (optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {
                                    if (businessName.isNotBlank() && phone.isNotBlank()) {
                                        step = 2
                                    } else {
                                        errorMessage = "Please enter Business Name and Phone Number."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("NEXT: LOCATION ADDRESS FORM ➔", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        2 -> {
                            Text("2. LOCATION ADDRESS (FORM FIELDS ONLY — NO MAP)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Enter exact street address and landmark details for customers", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = region,
                                    onValueChange = { region = it },
                                    label = { Text("Region *") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = district,
                                    onValueChange = { district = it },
                                    label = { Text("District *") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = ward,
                                    onValueChange = { ward = it },
                                    label = { Text("Ward / Area") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = street,
                                    onValueChange = { street = it },
                                    label = { Text("Street Name *") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = buildingName,
                                onValueChange = { buildingName = it },
                                label = { Text("Building / Complex Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = floor,
                                    onValueChange = { floor = it },
                                    label = { Text("Floor") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = shopNumber,
                                    onValueChange = { shopNumber = it },
                                    label = { Text("Shop / Office Number") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { step = 1 },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("← Back", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        if (street.isNotBlank() || buildingName.isNotBlank()) {
                                            step = 3
                                        } else {
                                            errorMessage = "Please enter street name or building name."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(50.dp)
                                ) {
                                    Text("NEXT: PHOTOS ➔", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }

                        3 -> {
                            Text("3. BUSINESS PHOTOS & REVIEW", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("First 5 photos are completely FREE for your business", fontSize = 12.sp, color = ActionTeal)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = photoUrl,
                                    onValueChange = { photoUrl = it },
                                    label = { Text("Photo URL") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Button(
                                    onClick = {
                                        if (photoUrl.isNotBlank()) {
                                            imageUrls = imageUrls + photoUrl.trim()
                                            photoUrl = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Text("ADD PHOTO")
                                }
                            }

                            if (imageUrls.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Uploaded Photos (${imageUrls.size} / 5 Free):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                imageUrls.forEachIndexed { idx, url ->
                                    Text("• Photo #${idx + 1}: $url", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { step = 2 },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("← Back", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isLoading = true
                                            errorMessage = null
                                            try {
                                                val req = CreateBusinessRequest(
                                                    name = businessName,
                                                    description = description,
                                                    phone = phone,
                                                    email = email,
                                                    supportsDelivery = supportsDelivery,
                                                    supportsPickup = supportsPickup,
                                                    latitude = -6.8182,
                                                    longitude = 39.2874,
                                                    address = "$buildingName, $street, $ward, $district",
                                                    city = region,
                                                    region = district,
                                                    buildingName = buildingName,
                                                    floor = floor,
                                                    shopNumber = shopNumber,
                                                    street = street,
                                                    landmark = ward,
                                                    logoUrl = imageUrls.firstOrNull()
                                                )
                                                val created = repository.createBusiness(req)
                                                if (imageUrls.size > 1) {
                                                    imageUrls.drop(1).forEach { url ->
                                                        try {
                                                            repository.addBusinessPhoto(created.id, url)
                                                        } catch (_: Exception) {}
                                                    }
                                                }
                                                isSubmitted = true
                                            } catch (e: Exception) {
                                                errorMessage = "Failed to submit business registration."
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(50.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = SurfaceWhite, strokeWidth = 2.5.dp)
                                    } else {
                                        Text("SUBMIT BUSINESS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
