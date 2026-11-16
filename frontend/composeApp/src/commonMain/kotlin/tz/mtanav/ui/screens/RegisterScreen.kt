package tz.mtanav.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    onRegisterSubmit: suspend (email: String, password: String, fullName: String, phone: String, role: String, shopName: String, areaStreet: String) -> Unit,
    initialRole: String = "CUSTOMER",
    modifier: Modifier = Modifier
) {
    var selectedRole by remember {
        mutableStateOf(
            if (initialRole.equals("business", ignoreCase = true) || initialRole.equals("owner", ignoreCase = true) || initialRole.contains("BUSINESS", ignoreCase = true)) {
                "BUSINESS OWNER"
            } else {
                "CUSTOMER"
            }
        )
    }

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Dynamic Business Fields
    var shopName by remember { mutableStateOf("") }
    var areaStreet by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // FULL SCREEN BRAND GREEN GRADIENT (#114B3F to #1A6052)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryDeepBlue, PrimaryBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val isMobile = maxWidth < 600.dp
        val outerPadding = if (isMobile) 12.dp else 24.dp
        val cardInnerPadding = if (isMobile) 16.dp else 36.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
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
                        .padding(horizontal = cardInnerPadding, vertical = cardInnerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TextButton(onClick = onBackClicked, contentPadding = PaddingValues(0.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tz.mtanav.ui.components.MtanavIcons.ArrowBack,
                                    contentDescription = null,
                                    tint = PrimaryDeepBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back to Login", color = PrimaryDeepBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Create MTANAV Account",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryDeepBlue,
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

                    // 1. ACCOUNT TYPE FIRST (Above Full Name)
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
                            containerColor = if (selectedRole == "CUSTOMER") PrimaryLight else AppBackground
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (selectedRole == "CUSTOMER") PrimaryDeepBlue else Color(0xFFE2E8F0)
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
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryDeepBlue)
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
                            containerColor = if (selectedRole == "BUSINESS OWNER") PrimaryLight else AppBackground
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (selectedRole == "BUSINESS OWNER") PrimaryDeepBlue else Color(0xFFE2E8F0)
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
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryDeepBlue)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("BUSINESS OWNER ACCOUNT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = HighlightGold) {
                                        Text("STOREFRONT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = PrimaryDeepBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text("Register your shop/service, list products, manage photos & receive orders", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // DYNAMIC BUSINESS FIELDS (Revealed when BUSINESS OWNER ACCOUNT is selected)
                    if (selectedRole == "BUSINESS OWNER") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryLight.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, PrimaryDeepBlue.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🏪 STOREFRONT CREATION DETAILS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryDeepBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Create your shop listing immediately upon account setup.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                // Shop / Business Name
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "SHOP / BUSINESS NAME *",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = shopName,
                                        onValueChange = { shopName = it },
                                        placeholder = { Text("e.g. Kariakoo Household Supplies", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryDeepBlue,
                                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Area / Street
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "AREA / STREET *",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    OutlinedTextField(
                                        value = areaStreet,
                                        onValueChange = { areaStreet = it },
                                        placeholder = { Text("e.g. Swahili Street, Kariakoo", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryDeepBlue,
                                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            }
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
                                focusedBorderColor = PrimaryDeepBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phone-First Identifier
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PHONE NUMBER (PRIMARY IDENTIFIER) *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = { Text("+255 7XX XXX XXX", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDeepBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Address (Optional)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EMAIL ADDRESS (OPTIONAL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("e.g. owner@shop.co.tz", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDeepBlue,
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
                                focusedBorderColor = PrimaryDeepBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    val isBusinessValid = selectedRole != "BUSINESS OWNER" || shopName.isNotBlank()
                    val canRegister = fullName.isNotBlank() && phoneNumber.isNotBlank() && password.length >= 6 && isBusinessValid

                    Button(
                        onClick = {
                            if (!canRegister) return@Button
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val backendRole = if (selectedRole == "BUSINESS OWNER") "OWNER" else "CUSTOMER"
                                    val primaryEmail = email.ifBlank { "${phoneNumber.filter { it.isDigit() }}@mtanav.tz" }
                                    onRegisterSubmit(
                                        primaryEmail.trim(),
                                        password,
                                        fullName.trim(),
                                        phoneNumber.trim(),
                                        backendRole,
                                        shopName.trim(),
                                        areaStreet.trim()
                                    )
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
                        enabled = canRegister && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HighlightGold,
                            contentColor = PrimaryDeepBlue,
                            disabledContainerColor = AppBackground,
                            disabledContentColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PrimaryDeepBlue, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (selectedRole == "BUSINESS OWNER") "CREATE BUSINESS & STOREFRONT" else "CREATE ACCOUNT",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account?", fontSize = 13.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        TextButton(onClick = onLoginClicked, contentPadding = PaddingValues(0.dp)) {
                            Text(
                                text = "Login",
                                color = PrimaryDeepBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
