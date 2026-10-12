package tz.mtanav.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tz.mtanav.theme.*

@Composable
fun AuthDialog(
    promptMessage: String = "Create an account or sign in to continue.",
    onDismiss: () -> Unit,
    onLoginSubmitted: (String, String) -> Unit,
    onRegisterSubmitted: (String, String, String, String, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var isRegisterTab by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CLIENT") } // CLIENT or OWNER

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Prompt
                Text(
                    text = promptMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDeepBlue,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppBackground)
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isRegisterTab) PrimaryDeepBlue else Color.Transparent)
                            .clickable { isRegisterTab = false }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "SIGN IN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isRegisterTab) SurfaceWhite else TextSecondary
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isRegisterTab) PrimaryDeepBlue else Color.Transparent)
                            .clickable { isRegisterTab = true }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "REGISTER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterTab) SurfaceWhite else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let { err ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ErrorRed.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(err, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                    }
                }

                if (!isRegisterTab) {
                    // LOGIN FORM
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLoginSubmitted(email, password) },
                        enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                        } else {
                            Text("SIGN IN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { isRegisterTab = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Don't have an account? Register",
                            color = PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // REGISTER FORM WITH ROLE SELECTION (CLIENT vs OWNER)
                    Text(
                        text = "SELECT ACCOUNT TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // CLIENT CARD
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRole == "CLIENT") ActionTeal.copy(alpha = 0.08f) else AppBackground
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (selectedRole == "CLIENT") ActionTeal else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = "CLIENT" }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedRole == "CLIENT",
                                onClick = { selectedRole = "CLIENT" },
                                colors = RadioButtonDefaults.colors(selectedColor = ActionTeal)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("CLIENT (Customer)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    "Browse, order products, book services, save favorites, rate & review businesses",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // OWNER CARD
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRole == "OWNER") PrimaryDeepBlue.copy(alpha = 0.08f) else AppBackground
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (selectedRole == "OWNER") PrimaryDeepBlue else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = "OWNER" }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedRole == "OWNER",
                                onClick = { selectedRole = "OWNER" },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryDeepBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("OWNER (Business Owner)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    "Register shop or service location, add products, services, prices, photos, receive orders & bookings",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number (e.g. +2557...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onRegisterSubmitted(email, password, fullName, phoneNumber, selectedRole) },
                        enabled = email.isNotBlank() && password.length >= 6 && fullName.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                        } else {
                            Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { isRegisterTab = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already registered? Sign in",
                            color = PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
