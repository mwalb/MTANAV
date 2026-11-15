package tz.mtanav.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mtanav_frontend.composeapp.generated.resources.Res
import io.github.alexzhirkevich.compottie.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tz.mtanav.theme.HighlightGold
import tz.mtanav.theme.SurfaceWhite

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var jsonString by remember { mutableStateOf<String?>(null) }
    var loadError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val bytes = Res.readBytes("files/mtanav3.json")
            jsonString = bytes.decodeToString()
        } catch (e: Exception) {
            loadError = true
        }
    }

    // Safety fallback timer: ensure splash finishes within 3.5 seconds
    LaunchedEffect(Unit) {
        delay(3500)
        onSplashFinished()
    }

    val composition = jsonString?.let { json ->
        rememberLottieComposition(LottieCompositionSpec.JsonString(json)).value
    }

    val progressState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = composition != null,
        speed = 1.0f
    )
    val progress = progressState.value

    // Transition when animation finishes
    LaunchedEffect(progress) {
        if (progress >= 0.99f && composition != null) {
            onSplashFinished()
        }
    }

    // Fast fallback if loading error occurs
    LaunchedEffect(loadError) {
        if (loadError) {
            delay(1000)
            onSplashFinished()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF124E4A), // Dark #124E4A
                        Color(0xFF176B65)  // Primary #176B65
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            if (composition != null) {
                val painter = rememberLottiePainter(
                    composition = composition,
                    progress = progress
                )

                Image(
                    painter = painter,
                    contentDescription = "MTANAV Animated Splash Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(240.dp)
                        .aspectRatio(1f)
                )
            } else {
                Text(
                    text = "MTANAV",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = SurfaceWhite,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MTANAV",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = SurfaceWhite,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Find it. Get there. Earn with it.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = HighlightGold
            )
        }
    }
}
