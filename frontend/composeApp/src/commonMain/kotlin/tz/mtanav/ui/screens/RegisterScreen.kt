package tz.mtanav.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.theme.*

@Composable
fun RegisterScreen(
    onCustomerRegistered: () -> Unit,
    onBusinessOwnerRegistered: () -> Unit,
    onLoginClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onRegisterSubmit: suspend (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER or BUSINESS OWNER

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // FULL SCREEN CAMPNAV BLUE GRADIENT (#1A237E to #3949AB)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CampnavDeepBlue, CampnavBlue)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .widthIn(max = 580.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 36.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
                        Text("← Back to Login", color = CampnavDeepBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Create MTANAV Account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = CampnavDeepBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Find it. Get there.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlightGold,
                    modifier = Modifier.padding(bottom = 20.dp)
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

                // Full Name
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FULL NAME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("e.g. Juma Salum or Sarah Kimaro", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampnavDeepBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email or Phone
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "EMAIL OR PHONE NUMBER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        placeholder = { Text("e.g. user@mtanav.tz or +255700000000", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampnavDeepBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASSWORD (MINIMUM 6 CHARACTERS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Create a strong password", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampnavDeepBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "SELECT ACCOUNT TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                )

                // CUSTOMER Option Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRole == "CUSTOMER") ActionTeal.copy(alpha = 0.08f) else AppBackground
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (selectedRole == "CUSTOMER") ActionTeal else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRole = "CUSTOMER" }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        RadioButton(
                            selected = selectedRole == "CUSTOMER",
                            onClick = { selectedRole = "CUSTOMER" },
                            colors = RadioButtonDefaults.colors(selectedColor = ActionTeal)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CUSTOMER ACCOUNT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Browse products & services, order items, book services and manage transactions", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BUSINESS OWNER Option Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRole == "BUSINESS OWNER") CampnavDeepBlue.copy(alpha = 0.08f) else AppBackground
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (selectedRole == "BUSINESS OWNER") CampnavDeepBlue else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRole = "BUSINESS OWNER" }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        RadioButton(
                            selected = selectedRole == "BUSINESS OWNER",
                            onClick = { selectedRole = "BUSINESS OWNER" },
                            colors = RadioButtonDefaults.colors(selectedColor = CampnavDeepBlue)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("BUSINESS OWNER ACCOUNT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Register your shop/service, list products, manage photos & receive orders", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || emailOrPhone.isBlank() || password.length < 6) return@Button
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val backendRole = if (selectedRole == "BUSINESS OWNER") "OWNER" else "CUSTOMER"
                                onRegisterSubmit(emailOrPhone.trim(), password, fullName.trim(), "", backendRole)
                                if (selectedRole == "BUSINESS OWNER") {
                                    onBusinessOwnerRegistered()
                                } else {
                                    onCustomerRegistered()
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Registration failed. Please try again."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = fullName.isNotBlank() && emailOrPhone.isNotBlank() && password.length >= 6 && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = CampnavDeepBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                    } else {
                        Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 0.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account?", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = onLoginClicked, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = "Login",
                            color = CampnavBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
