package tz.mtanav.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tz.mtanav.model.CartItem
import tz.mtanav.theme.*
import tz.mtanav.ui.components.MtanavIcons
import tz.mtanav.util.FormattingUtils

@Composable
fun CartAndCheckoutScreen(
    cartItems: List<CartItem>,
    onBackClicked: () -> Unit,
    onOrderPlaced: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deliveryOption by remember { mutableStateOf("DELIVERY") }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("M-PESA") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val subtotalAmount = cartItems.sumOf { it.unitPrice * it.quantity }
    val deliveryFee = if (deliveryOption == "DELIVERY") 3500.0 else 0.0
    val totalAmount = subtotalAmount + deliveryFee

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // TOP HEADER
        Surface(color = PrimaryDeepBlue, modifier = Modifier.fillMaxWidth(), shadowElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClicked, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = MtanavIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = SurfaceWhite
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Cart & Checkout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = SurfaceWhite
                    )
                    Text(
                        text = "Complete your order with local merchant delivery",
                        fontSize = 11.sp,
                        color = HighlightGold
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val isDesktop = maxWidth >= 1024.dp
            val isMobileView = maxWidth < 768.dp

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.widthIn(max = 440.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = MtanavIcons.ShoppingCart,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your Cart is Empty",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Explore products from verified local shops on the discovery map and add items to your cart.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onBackClicked,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Browse Products & Map", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else if (isDesktop) {
                // ============================================================
                // DESKTOP TWO-COLUMN SPLIT (>= 1024px)
                // Left: 60% (Delivery & Payment Methods)
                // Right: 40% Sticky (Order Summary & Confirm Button)
                // ============================================================
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // LEFT COLUMN (60%)
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DeliveryOptionsCard(
                            deliveryOption = deliveryOption,
                            deliveryAddress = deliveryAddress,
                            onOptionSelected = { deliveryOption = it },
                            onAddressChanged = { deliveryAddress = it }
                        )

                        PaymentMethodsCard(
                            paymentMethod = paymentMethod,
                            isMobileView = false,
                            onMethodSelected = { paymentMethod = it }
                        )
                    }

                    // RIGHT COLUMN (40%, Sticky Order Summary)
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OrderSummaryCard(
                            cartItems = cartItems,
                            subtotalAmount = subtotalAmount,
                            deliveryFee = deliveryFee,
                            totalAmount = totalAmount,
                            isPlacingOrder = isPlacingOrder,
                            onConfirmOrder = {
                                isPlacingOrder = true
                                onOrderPlaced()
                            }
                        )
                    }
                }
            } else {
                // ============================================================
                // MOBILE / TABLET STACKED LAYOUT (< 1024px)
                // ============================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OrderItemsListCard(cartItems = cartItems)

                    DeliveryOptionsCard(
                        deliveryOption = deliveryOption,
                        deliveryAddress = deliveryAddress,
                        onOptionSelected = { deliveryOption = it },
                        onAddressChanged = { deliveryAddress = it }
                    )

                    PaymentMethodsCard(
                        paymentMethod = paymentMethod,
                        isMobileView = isMobileView,
                        onMethodSelected = { paymentMethod = it }
                    )

                    FeeBreakdownCard(
                        subtotalAmount = subtotalAmount,
                        deliveryFee = deliveryFee,
                        totalAmount = totalAmount,
                        isPlacingOrder = isPlacingOrder,
                        onConfirmOrder = {
                            isPlacingOrder = true
                            onOrderPlaced()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryOptionsCard(
    deliveryOption: String,
    deliveryAddress: String,
    onOptionSelected: (String) -> Unit,
    onAddressChanged: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MtanavIcons.LocalShipping,
                    contentDescription = null,
                    tint = PrimaryDeepBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DELIVERY & FULFILLMENT METHOD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryDeepBlue,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isDelivery = deliveryOption == "DELIVERY"
                val isPickup = deliveryOption == "PICKUP"

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDelivery) PrimaryDeepBlue else AppBackground,
                    border = BorderStroke(1.dp, if (isDelivery) PrimaryDeepBlue else Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOptionSelected("DELIVERY") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = MtanavIcons.LocalShipping,
                            contentDescription = null,
                            tint = if (isDelivery) SurfaceWhite else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Local Delivery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isDelivery) SurfaceWhite else TextPrimary
                            )
                            Text(
                                text = "TSh 3,500 fee",
                                fontSize = 10.5.sp,
                                color = if (isDelivery) SurfaceWhite.copy(alpha = 0.8f) else TextSecondary
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPickup) PrimaryDeepBlue else AppBackground,
                    border = BorderStroke(1.dp, if (isPickup) PrimaryDeepBlue else Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOptionSelected("PICKUP") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = MtanavIcons.Storefront,
                            contentDescription = null,
                            tint = if (isPickup) SurfaceWhite else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Store Pickup",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isPickup) SurfaceWhite else TextPrimary
                            )
                            Text(
                                text = "Free",
                                fontSize = 10.5.sp,
                                color = if (isPickup) HighlightGold else ActionTeal
                            )
                        }
                    }
                }
            }

            if (deliveryOption == "DELIVERY") {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = onAddressChanged,
                    label = { Text("Delivery Address & Local Landmarks") },
                    placeholder = {
                        Text(
                            text = "e.g., Kariakoo, Swahili Street, near Gerezani bus stop",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = MtanavIcons.LocationOn,
                            contentDescription = null,
                            tint = ActionTeal
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDeepBlue,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Include specific local landmarks (bus stops, shops, street names) for fast delivery",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodsCard(
    paymentMethod: String,
    isMobileView: Boolean,
    onMethodSelected: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MtanavIcons.CheckCircle,
                    contentDescription = null,
                    tint = ActionTeal,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PAYMENT METHOD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryDeepBlue,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val methods = listOf(
                "M-Pesa" to "#E11D48",
                "Tigo Pesa" to "#2563EB",
                "Airtel" to "#DC2626",
                "Cash" to "#059669"
            )

            if (isMobileView) {
                // CLEAN 2X2 GRID FOR MOBILE VIEWPORTS (< 768px)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0 until 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (col in 0 until 2) {
                                val idx = row * 2 + col
                                val (mName, _) = methods[idx]
                                val isSelected = paymentMethod.equals(mName, ignoreCase = true)

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) ActionTeal else AppBackground,
                                    border = BorderStroke(1.dp, if (isSelected) ActionTeal else Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onMethodSelected(mName) }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = mName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) SurfaceWhite else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // HORIZONTAL ROW FOR LARGER SCREENS (>= 768px)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    methods.forEach { (mName, _) ->
                        val isSelected = paymentMethod.equals(mName, ignoreCase = true)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ActionTeal else AppBackground,
                            border = BorderStroke(1.dp, if (isSelected) ActionTeal else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onMethodSelected(mName) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
                            ) {
                                Text(
                                    text = mName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (isSelected) SurfaceWhite else TextPrimary
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
private fun OrderItemsListCard(cartItems: List<CartItem>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "SELECTED ITEMS (${cartItems.sumOf { it.quantity }})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryDeepBlue,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            cartItems.forEachIndexed { index, item ->
                val itemTotal = item.unitPrice * item.quantity

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.businessName}${item.variantName?.let { " • $it" } ?: ""}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Qty: ${item.quantity} × ${FormattingUtils.formatTsh(item.unitPrice)}",
                            fontSize = 11.5.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = FormattingUtils.formatTsh(itemTotal),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.5.sp,
                        color = ActionTeal,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (index < cartItems.size - 1) {
                    HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    cartItems: List<CartItem>,
    subtotalAmount: Double,
    deliveryFee: Double,
    totalAmount: Double,
    isPlacingOrder: Boolean,
    onConfirmOrder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ORDER SUMMARY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryDeepBlue,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Itemized list preview
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Qty: ${item.quantity}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = FormattingUtils.formatTsh(item.unitPrice * item.quantity),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 14.dp))

            // Breakdown of fees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", fontSize = 13.sp, color = TextSecondary)
                Text(FormattingUtils.formatTsh(subtotalAmount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Fee", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text = if (deliveryFee > 0) FormattingUtils.formatTsh(deliveryFee) else "Free (Pickup)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (deliveryFee > 0) TextPrimary else ActionTeal
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Amount Due", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = FormattingUtils.formatTsh(totalAmount),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ActionTeal
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConfirmOrder,
                enabled = !isPlacingOrder,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = MtanavIcons.CheckCircle,
                            contentDescription = null,
                            tint = HighlightGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONFIRM & PAY TRANSACTION",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            color = SurfaceWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeeBreakdownCard(
    subtotalAmount: Double,
    deliveryFee: Double,
    totalAmount: Double,
    isPlacingOrder: Boolean,
    onConfirmOrder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", fontSize = 13.sp, color = TextSecondary)
                Text(FormattingUtils.formatTsh(subtotalAmount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Fee", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text = if (deliveryFee > 0) FormattingUtils.formatTsh(deliveryFee) else "Free (Pickup)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (deliveryFee > 0) TextPrimary else ActionTeal
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Amount Due", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = FormattingUtils.formatTsh(totalAmount),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ActionTeal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirmOrder,
                enabled = !isPlacingOrder,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(color = SurfaceWhite, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = MtanavIcons.CheckCircle,
                            contentDescription = null,
                            tint = HighlightGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONFIRM & PAY TRANSACTION",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            color = SurfaceWhite
                        )
                    }
                }
            }
        }
    }
}
