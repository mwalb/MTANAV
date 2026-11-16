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
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.ui.map.LocationNavigator
import tz.mtanav.util.FormattingUtils

@Composable
fun ProductDetailsScreen(
    product: BusinessProductDto,
    locationNavigator: LocationNavigator,
    onBackClicked: () -> Unit,
    onAddToCartClicked: (CartItem) -> Unit,
    onMessageShopClicked: (Long, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedVariant by remember { mutableStateOf<ProductVariantDto?>(product.variants.firstOrNull()) }
    var quantity by remember { mutableStateOf(1) }

    val currentPrice = selectedVariant?.price ?: product.salePrice ?: product.price
    val totalAmount = currentPrice * quantity

    val iconKey = when {
        product.productName.contains("Charger", ignoreCase = true) -> "phone"
        product.productName.contains("Phone", ignoreCase = true) -> "phone"
        product.productName.contains("Rice", ignoreCase = true) -> "food"
        product.productName.contains("Spoon", ignoreCase = true) -> "food"
        product.productName.contains("Shoe", ignoreCase = true) -> "shop"
        else -> "product"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1100.dp)
        ) {
            // TOP HEADER BAR
            Surface(
                color = PrimaryDark,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClicked, modifier = Modifier.size(36.dp)) {
                        Icon(MtanavIcons.ArrowBack, contentDescription = "Back", tint = SurfaceWhite)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Product Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                }
            }

            // RESPONSIVE CONTENT AREA
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val isDesktop = maxWidth >= 1024.dp

                if (isDesktop) {
                    // TWO-COLUMN DESKTOP LAYOUT (>= 1024px)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // LEFT COLUMN (65%): Product Info, Variants, Direct Merchant Actions
                        Column(
                            modifier = Modifier
                                .weight(0.65f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProductMainCard(
                                product = product,
                                iconKey = iconKey,
                                currentPrice = currentPrice,
                                selectedVariant = selectedVariant
                            )

                            VariantsSelectorCard(
                                product = product,
                                selectedVariant = selectedVariant,
                                onVariantSelected = { selectedVariant = it }
                            )

                            DirectMerchantActionsCard(
                                product = product,
                                selectedVariant = selectedVariant,
                                quantity = quantity,
                                totalAmount = totalAmount,
                                locationNavigator = locationNavigator,
                                onMessageShopClicked = onMessageShopClicked
                            )
                        }

                        // RIGHT COLUMN (35%): Dedicated Order & Purchase Card
                        Column(
                            modifier = Modifier
                                .weight(0.35f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OrderPurchaseCard(
                                currentPrice = currentPrice,
                                quantity = quantity,
                                totalAmount = totalAmount,
                                onQuantityChange = { quantity = it },
                                onAddToCartClicked = {
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
                                }
                            )
                        }
                    }
                } else {
                    // MOBILE & TABLET LAYOUT (< 1024px)
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    ProductMainCard(
                                        product = product,
                                        iconKey = iconKey,
                                        currentPrice = currentPrice,
                                        selectedVariant = selectedVariant
                                    )

                                    VariantsSelectorCard(
                                        product = product,
                                        selectedVariant = selectedVariant,
                                        onVariantSelected = { selectedVariant = it }
                                    )

                                    DirectMerchantActionsCard(
                                        product = product,
                                        selectedVariant = selectedVariant,
                                        quantity = quantity,
                                        totalAmount = totalAmount,
                                        locationNavigator = locationNavigator,
                                        onMessageShopClicked = onMessageShopClicked
                                    )
                                }
                            }
                        }

                        // STICKY BOTTOM ACTION BAR FOR MOBILE
                        Surface(
                            color = Surface,
                            shadowElevation = 16.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .widthIn(max = 600.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // QUANTITY STEPPER (- 1 +)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(PrimaryLight, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("-", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                    }

                                    Text(
                                        text = "$quantity",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )

                                    IconButton(
                                        onClick = { quantity++ },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("+", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                    }
                                }

                                // DYNAMIC TOTAL & ADD TO CART
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Total", fontSize = 10.5.sp, color = TextSecondary)
                                        Text(
                                            text = FormattingUtils.formatTsh(totalAmount),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Action
                                        )
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
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = MtanavIcons.ShoppingCart,
                                                contentDescription = null,
                                                tint = SurfaceWhite,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("ADD TO CART", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
}

@Composable
private fun ProductMainCard(
    product: BusinessProductDto,
    iconKey: String,
    currentPrice: Double,
    selectedVariant: ProductVariantDto?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PrimaryLight,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = MtanavIcons.getVectorForCategoryOrEmoji(iconKey),
                        contentDescription = null,
                        tint = PrimaryDark,
                        modifier = Modifier.size(44.dp)
                    )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(MtanavIcons.CheckCircle, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("VERIFIED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }

            product.productDescription?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(desc, fontSize = 13.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(FormattingUtils.formatTsh(currentPrice), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Action)
                product.salePrice?.let {
                    if (it < product.price && selectedVariant == null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            FormattingUtils.formatTsh(product.price),
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VariantsSelectorCard(
    product: BusinessProductDto,
    selectedVariant: ProductVariantDto?,
    onVariantSelected: (ProductVariantDto) -> Unit
) {
    val displayVariants = if (product.variants.isNotEmpty()) {
        product.variants
    } else {
        listOf(
            ProductVariantDto(id = 101, name = "Single Piece (Retail)", price = product.price, quantity = 1, unit = "pc", availableQuantity = 50),
            ProductVariantDto(id = 102, name = "12-Piece Wholesale Pack", price = product.price * 10, quantity = 12, unit = "pcs", availableQuantity = 20),
            ProductVariantDto(id = 103, name = "50-Piece Wholesale Carton", price = product.price * 38, quantity = 50, unit = "pcs", availableQuantity = 10)
        )
    }

    Column {
        Text(
            text = "AVAILABLE VARIANTS & WHOLESALE BULK PACKS (${displayVariants.size})",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        displayVariants.forEach { variant ->
            val isSelected = selectedVariant?.id == variant.id
            val borderColor = if (isSelected) Action else Border
            val bgColor = if (isSelected) PrimaryLight else Surface
            val qty = if (variant.quantity > 0) variant.quantity else 1
            val unitPrice = variant.price / qty
            val isBulk = qty > 1
            val unitStr = if (variant.unit.isNotBlank()) variant.unit else "pcs"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onVariantSelected(variant) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onVariantSelected(variant) },
                            colors = RadioButtonDefaults.colors(selectedColor = Action)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = variant.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                if (isBulk) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Action.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "WHOLESALE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Action,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Unit breakdown: ${FormattingUtils.formatTsh(unitPrice)}/$unitStr • In stock: ${variant.availableQuantity} $unitStr",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FormattingUtils.formatTsh(variant.price),
                            fontWeight = FontWeight.Black,
                            color = Action,
                            fontSize = 15.sp
                        )
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Action
                            ) {
                                Text(
                                    text = "SELECTED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectMerchantActionsCard(
    product: BusinessProductDto,
    selectedVariant: ProductVariantDto?,
    quantity: Int,
    totalAmount: Double,
    locationNavigator: LocationNavigator,
    onMessageShopClicked: (Long, String) -> Unit
) {
    val variantStr = selectedVariant?.let { " (${it.name})" } ?: ""
    val msg = "Hello ${product.businessName}, I am inquiring about $quantity x ${product.productName}$variantStr (Total: ${FormattingUtils.formatTsh(totalAmount)}) via MTANAV."

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DIRECT MERCHANT ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        locationNavigator.openWhatsApp("+255700000000", msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tz.mtanav.ui.components.MtanavIcons.Phone,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WHATSAPP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        locationNavigator.launchGoogleMapsNavigation(-6.8182, 39.2778, product.businessName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HighlightGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧭 NAVIGATE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryDeepBlue)
                    }
                }

                Button(
                    onClick = {
                        onMessageShopClicked(product.businessId, msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✉️ MESSAGE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderPurchaseCard(
    currentPrice: Double,
    quantity: Int,
    totalAmount: Double,
    onQuantityChange: (Int) -> Unit,
    onAddToCartClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ORDER & PURCHASE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryDark,
                letterSpacing = 1.sp
            )

            HorizontalDivider(color = Border)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Unit Price", fontSize = 13.sp, color = TextSecondary)
                Text(FormattingUtils.formatTsh(currentPrice), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            // QUANTITY SELECTOR (- 1 +)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quantity", fontSize = 13.sp, color = TextSecondary)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(PrimaryLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }

                    Text(
                        text = "$quantity",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
            }

            HorizontalDivider(color = Border)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Price", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = FormattingUtils.formatTsh(totalAmount),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Action
                )
            }

            Button(
                onClick = onAddToCartClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = MtanavIcons.ShoppingCart,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD TO CART", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
