package tz.mtanav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tz.mtanav.model.CartItem
import tz.mtanav.theme.*

@Composable
fun CartAndCheckoutScreen(
    cartItems: List<CartItem>,
    onBackClicked: () -> Unit,
    onOrderPlaced: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deliveryOption by remember { mutableStateOf("DELIVERY") }
    var deliveryAddress by remember { mutableStateOf("") }

    val totalAmount = cartItems.sumOf { it.unitPrice * it.quantity }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Header
        Surface(color = PrimaryDeepBlue, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClicked) {
                    Text("⬅️", fontSize = 20.sp, color = SurfaceWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Checkout Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            }
        }

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            Text("ORDER SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            cartItems.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            item.variantName?.let { Text(it, fontSize = 12.sp, color = TextSecondary) }
                            Text("Qty: ${item.quantity}", fontSize = 13.sp, color = TextPrimary)
                        }
                        Text("TSh ${(item.unitPrice * item.quantity).toInt()}", fontWeight = FontWeight.Bold, color = ActionTeal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("DELIVERY METHOD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { deliveryOption = "DELIVERY" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (deliveryOption == "DELIVERY") PrimaryBlue else AppBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🚚 Delivery", color = if (deliveryOption == "DELIVERY") SurfaceWhite else TextPrimary)
                }

                Button(
                    onClick = { deliveryOption = "PICKUP" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (deliveryOption == "PICKUP") PrimaryBlue else AppBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🏪 Pickup", color = if (deliveryOption == "PICKUP") SurfaceWhite else TextPrimary)
                }
            }

            if (deliveryOption == "DELIVERY") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Address (Street / Landmark)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Surface(color = SurfaceWhite, shadowElevation = 12.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grand Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("TSh ${totalAmount.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ActionTeal)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOrderPlaced,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ActionTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CONFIRM ORDER", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
