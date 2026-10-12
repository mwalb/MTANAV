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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tz.mtanav.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onLoginSubmit: suspend (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .widthIn(max = 540.dp)
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
                        Text("← Back to Discovery Map", color = CampnavDeepBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "MTANAV",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = CampnavDeepBlue,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Find it. Get there.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighlightGold
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Sign in to access your business owner or customer portal",
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
                        placeholder = { Text("e.g. owner@shop.co.tz or +255700000000", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampnavDeepBlue,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your account password", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
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

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (emailOrPhone.isBlank() || password.isBlank()) return@Button
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                onLoginSubmit(emailOrPhone.trim(), password)
                                onLoginSuccess()
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Invalid login credentials."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = emailOrPhone.isNotBlank() && password.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = CampnavDeepBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SurfaceWhite, strokeWidth = 2.dp)
                    } else {
                        Text("LOGIN TO PORTAL", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 0.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account?", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = onRegisterClicked, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = "Register Now",
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
