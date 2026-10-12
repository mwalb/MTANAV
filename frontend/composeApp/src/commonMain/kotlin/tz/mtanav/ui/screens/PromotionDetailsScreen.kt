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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.PromotionDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*
import tz.mtanav.ui.map.LocationNavigator

@Composable
fun PromotionDetailsScreen(
    promotionId: Long,
    repository: MtanavRepository,
    locationNavigator: LocationNavigator,
    onBackClicked: () -> Unit,
    onViewShopClicked: (businessId: Long, productId: Long?, serviceId: Long?) -> Unit,
    onAuthRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promo by remember { mutableStateOf<PromotionDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSavedState by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun loadPromotion() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val fetched = repository.getPromotionDetails(promotionId)
                promo = fetched
                isSavedState = fetched.isSaved
            } catch (e: Exception) {
                errorMessage = "Failed to load promotion details."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(promotionId) {
        loadPromotion()
    }

    fun toggleSavePromotion() {
        if (repository.currentUser == null) {
            onAuthRequested()
            return
        }
        coroutineScope.launch {
            try {
                if (isSavedState) {
                    repository.unsavePromotion(promotionId)
                    isSavedState = false
                } else {
                    repository.savePromotion(promotionId)
                    isSavedState = true
                }
            } catch (e: Exception) { /* ignored */ }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // TOP APP BAR
        Surface(color = Primary, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                TextButton(onClick = onBackClicked) {
                    Text("← Back to Promotions", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("OFFER DETAILS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Surface)
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
                    Button(onClick = { loadPromotion() }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                        Text("Retry")
                    }
                }
            }
        } else promo?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(p.businessName ?: "Verified Business", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Action)

                            if (p.isSponsored) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Accent) {
                                    Text("SPONSORED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(p.title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)

                        Spacer(modifier = Modifier.height(12.dp))

                        // PRICING SUMMARY
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            p.promotionalPrice?.let { promoPrice ->
                                Text("TSh ${promoPrice.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Action)
                            }
                            p.originalPrice?.let { origPrice ->
                                Text("TSh ${origPrice.toInt()}", fontSize = 15.sp, color = TextSecondary, textDecoration = TextDecoration.LineThrough)
                            }
                            if (p.discountPercentage != null && p.discountPercentage > 0) {
                                Surface(shape = RoundedCornerShape(6.dp), color = PrimaryLight) {
                                    Text("Save ${p.discountPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDark, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 16.dp))

                        Text("Offer Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(p.description, fontSize = 13.sp, color = TextPrimary)

                        p.termsAndConditions?.let { terms ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Terms & Conditions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(terms, fontSize = 13.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Offer Status: ${p.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            p.expiryDate?.let { exp ->
                                Text("Valid Until: $exp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                        }
                    }
                }

                // ACTIONS CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Promotional Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                        Button(
                            onClick = { onViewShopClicked(p.businessId, p.productId, p.serviceId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Shop & Products / Services", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (p.businessLatitude != null && p.businessLongitude != null) {
                            OutlinedButton(
                                onClick = {
                                    locationNavigator.launchGoogleMapsNavigation(
                                        latitude = p.businessLatitude,
                                        longitude = p.businessLongitude,
                                        businessName = p.businessName ?: "Business"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📍 Navigate to Shop (External Google Maps)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Primary)
                            }
                        }

                        Button(
                            onClick = { toggleSavePromotion() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSavedState) Accent else Action),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (isSavedState) "★ Saved to My Offers" else "☆ Save Offer to My Offers",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSavedState) TextPrimary else Surface
                            )
                        }
                    }
                }
            }
        }
    }
}
