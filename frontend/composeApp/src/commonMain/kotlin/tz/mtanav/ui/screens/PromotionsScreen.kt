package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.model.PromotionDto
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromotionsScreen(
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    onPromotionSelected: (Long) -> Unit,
    onViewShopClicked: (businessId: Long, productId: Long?, serviceId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var promotions by remember { mutableStateOf<List<PromotionDto>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedOfferType by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun fetchPromotions() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                promotions = repository.getPromotions(
                    query = searchQuery.ifBlank { null },
                    category = selectedCategory,
                    offerType = selectedOfferType
                )
            } catch (e: Exception) {
                errorMessage = "Failed to load promotions. Please check connection and retry."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        categories = listOf(
            "All Offers",
            "Electronics",
            "Household",
            "Automotive",
            "Fashion & Footwear",
            "Beauty & Salon",
            "Printing & Office",
            "Services & Repairs",
            "Food & Groceries"
        )
        fetchPromotions()
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
                    Text("PROMOTIONS & DISCOUNTS", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Surface)
                    Text("Genuine deals and verified offers from local businesses", fontSize = 12.sp, color = Surface.copy(alpha = 0.8f))
                }
            }
        }

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // SEARCH & CATEGORY FILTER CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
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
                            placeholder = { Text("Search offers by product, service or business...", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )

                        Button(
                            onClick = { fetchPromotions() },
                            colors = ButtonDefaults.buttonColors(containerColor = Action),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SEARCH OFFERS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // CATEGORIES
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { cat ->
                            val isSel = (cat == "All Offers" && selectedCategory == null) || (cat == selectedCategory)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSel) Primary else PrimaryLight)
                                    .clickable {
                                        selectedCategory = if (cat == "All Offers") null else cat
                                        fetchPromotions()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Surface else PrimaryDark
                                )
                            }
                        }
                    }

                    // OFFER TYPES
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        listOf("All" to null, "Discount" to "DISCOUNT", "Flash Sale" to "FLASH_SALE", "Special Offer" to "SPECIAL_OFFER").forEach { (label, type) ->
                            val isSel = selectedOfferType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Action else Surface)
                                    .clickable {
                                        selectedOfferType = type
                                        fetchPromotions()
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
                        Button(onClick = { fetchPromotions() }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) {
                            Text("Retry", fontSize = 12.sp)
                        }
                    }
                }
            } else if (promotions.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏷️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active promotions matching your filter.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Check back soon for new offers from verified shops.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                Text(
                    text = "${promotions.size} active promotional offers found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(promotions) { promo ->
                        PromotionCardItem(
                            promo = promo,
                            onCardClicked = { onPromotionSelected(promo.id) },
                            onViewShopClicked = { onViewShopClicked(promo.businessId, promo.productId, promo.serviceId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionCardItem(
    promo: PromotionDto,
    onCardClicked: () -> Unit,
    onViewShopClicked: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onCardClicked)
    ) {
        Column {
            // SPONSORED BADGE HEADER & DISCOUNT BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (promo.isSponsored) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Accent) {
                            Text("SPONSORED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    } else {
                        Text(promo.category, fontSize = 11.sp, color = Surface.copy(alpha = 0.9f), fontWeight = FontWeight.Bold)
                    }

                    if (promo.discountPercentage != null && promo.discountPercentage > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Action) {
                            Text("${promo.discountPercentage}% OFF", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Surface, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = promo.businessName ?: "Verified Business",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Action
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = promo.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = promo.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // PRICES
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        promo.promotionalPrice?.let { promoPrice ->
                            Text(
                                text = "TSh ${promoPrice.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Action
                            )
                        }
                        promo.originalPrice?.let { origPrice ->
                            Text(
                                text = "TSh ${origPrice.toInt()}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    promo.expiryDate?.let { exp ->
                        Text("Expires: $exp", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                }

                HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewShopClicked,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }

                    Button(
                        onClick = onCardClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Offer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
