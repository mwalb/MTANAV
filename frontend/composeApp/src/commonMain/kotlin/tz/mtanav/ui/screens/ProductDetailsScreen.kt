package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import tz.mtanav.model.BusinessProductDto
import tz.mtanav.model.CartItem
import tz.mtanav.model.ProductVariantDto
import tz.mtanav.theme.*
import tz.mtanav.ui.map.LocationNavigator

@Composable
fun ProductDetailsScreen(
    product: BusinessProductDto,
    locationNavigator: LocationNavigator,
    onBackClicked: () -> Unit,
    onAddToCartClicked: (CartItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf<ProductVariantDto?>(product.variants.firstOrNull()) }
    var quantity by remember { mutableStateOf(1) }

    val currentPrice = selectedVariant?.price ?: product.salePrice ?: product.price
    val totalAmount = currentPrice * quantity

    val icon = when {
        product.productName.contains("Charger", ignoreCase = true) -> "🔌"
        product.productName.contains("Phone", ignoreCase = true) -> "📱"
        product.productName.contains("Rice", ignoreCase = true) -> "🍚"
        product.productName.contains("Spoon", ignoreCase = true) -> "🥄"
        product.productName.contains("Shoe", ignoreCase = true) -> "👟"
        else -> "📦"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // TOP HEADER
        Surface(color = PrimaryDark, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClicked) {
                    Text("⬅️", fontSize = 20.sp, color = SurfaceWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Product Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // PRODUCT MAIN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryLight,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(icon, fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(product.productName, fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Sold by ${product.businessName}", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Action) {
                            Text("VERIFIED ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    product.productDescription?.let { desc ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(desc, fontSize = 13.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("TSh ${currentPrice.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Action)
                        product.salePrice?.let {
                            if (it < product.price && selectedVariant == null) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "TSh ${product.price.toInt()}",
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // VARIANTS SELECTOR
            if (product.variants.isNotEmpty()) {
                Text("SELECT VARIANT / TYPE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))

                product.variants.forEach { variant ->
                    val isSelected = selectedVariant?.id == variant.id
                    val borderColor = if (isSelected) Action else Color.Transparent
                    val bgColor = if (isSelected) PrimaryLight else Surface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { selectedVariant = variant },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(variant.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Text("In stock: ${variant.availableQuantity} ${variant.unit}", fontSize = 12.sp, color = TextSecondary)
                            }
                            Text("TSh ${variant.price.toInt()}", fontWeight = FontWeight.Black, color = Action, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QUANTITY SELECTOR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { if (quantity > 1) quantity-- },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) { Text("-", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 16.sp) }

                        Text("$quantity", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)

                        Button(
                            onClick = { quantity++ },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) { Text("+", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 16.sp) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CONTACT / WHATSAPP SHOP
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DIRECT MERCHANT ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val variantStr = selectedVariant?.let { " (${it.name})" } ?: ""
                                val msg = "Hello ${product.businessName}, I would like to order $quantity x ${product.productName}$variantStr (Total: TSh ${totalAmount.toInt()}) via MTANAV."
                                locationNavigator.openWhatsApp("+255700000000", msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Action),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("💬 WHATSAPP SHOP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                locationNavigator.launchGoogleMapsNavigation(-6.8182, 39.2778, product.businessName)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🗺️ NAVIGATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // BOTTOM ACTION BAR
        Surface(color = Surface, shadowElevation = 12.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Price", fontSize = 11.sp, color = TextSecondary)
                    Text("TSh ${totalAmount.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
                Button(
                    onClick = {
                        val cartItem = CartItem(
                            businessProductId = product.id,
                            businessId = product.businessId,
                            businessName = product.businessName,
                            productName = product.productName,
                            variantId = selectedVariant?.id,
                            variantName = selectedVariant?.name,
                            unitPrice = currentPrice,
                            quantity = quantity
                        )
                        onAddToCartClicked(cartItem)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ADD TO CART 🛒", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
