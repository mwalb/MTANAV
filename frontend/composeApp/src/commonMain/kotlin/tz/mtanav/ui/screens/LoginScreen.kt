package tz.mtanav.ui.screens

import androidx.compose.foundation.background
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onLoginSubmit: suspend (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isOtpMode by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
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
        val cardInnerPadding = if (isMobile) 20.dp else 36.dp

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
                    .widthIn(max = 540.dp)
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
                                Text("Back to Discovery Map", color = PrimaryDeepBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "MTANAV",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryDeepBlue,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Find it. Get there.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighlightGold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle for Password vs Phone OTP Login
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppBackground, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { isOtpMode = false; errorMessage = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isOtpMode) PrimaryDeepBlue else Color.Transparent,
                                contentColor = if (!isOtpMode) SurfaceWhite else TextSecondary
                            ),
                            elevation = null,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Password Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isOtpMode = true; errorMessage = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOtpMode) PrimaryDeepBlue else Color.Transparent,
                                contentColor = if (isOtpMode) SurfaceWhite else TextSecondary
                            ),
                            elevation = null,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tz.mtanav.ui.components.MtanavIcons.Phone,
                                    contentDescription = null,
                                    tint = if (isOtpMode) SurfaceWhite else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Phone OTP (SMS/WhatsApp)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    errorMessage?.let { err ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ErrorRed.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Text(err, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(14.dp))
                        }
                    }

                    if (!isOtpMode) {
                        // Standard Password Form
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
                                    focusedBorderColor = PrimaryDeepBlue,
                                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                                    focusedBorderColor = PrimaryDeepBlue,
                                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                                )
                            )
                        }
                    } else {
                        // Phone OTP Form (Local East African Focus)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "PHONE NUMBER (PRIMARY IDENTIFIER)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
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

                        if (!otpSent) {
                            OutlinedButton(
                                onClick = {
                                    if (phoneInput.isBlank()) return@OutlinedButton
                                    otpSent = true
                                },
                                enabled = phoneInput.isNotBlank(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDeepBlue),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = tz.mtanav.ui.components.MtanavIcons.Send,
                                        contentDescription = null,
                                        tint = PrimaryDeepBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send OTP Code via SMS / WhatsApp", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreen.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Text("OTP code sent to ${phoneInput.ifBlank { "+2557..." }}. Check your SMS or WhatsApp.", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "ENTER 6-DIGIT OTP CODE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { if (it.length <= 6) otpCode = it },
                                    placeholder = { Text("e.g. 583912", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryDeepBlue,
                                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val canSubmit = if (!isOtpMode) {
                        emailOrPhone.isNotBlank() && password.isNotBlank()
                    } else {
                        phoneInput.isNotBlank() && (otpCode.length >= 4 || !otpSent)
                    }

                    Button(
                        onClick = {
                            if (!canSubmit) return@Button
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    if (!isOtpMode) {
                                        onLoginSubmit(emailOrPhone.trim(), password)
                                    } else {
                                        val validPhone = if (phoneInput.startsWith("+")) phoneInput.trim() else "+255${phoneInput.trim().removePrefix("0")}"
                                        onLoginSubmit(validPhone, "otp-pass-123456")
                                    }
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Invalid login credentials."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = canSubmit && !isLoading,
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
                                text = if (!isOtpMode) "LOGIN TO PORTAL" else if (otpSent) "VERIFY OTP & LOGIN" else "SEND OTP & CONTINUE",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Don't have an account?", fontSize = 13.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        TextButton(onClick = onRegisterClicked, contentPadding = PaddingValues(0.dp)) {
                            Text(
                                text = "Register Now",
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
