package tz.mtanav.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import tz.mtanav.theme.PrimaryLight
import tz.mtanav.theme.TextPrimary

@Composable
fun AppThumbnail(
    imageUrl: String?,
    fallbackIcon: String = "📦",
    categoryType: String = "PRODUCT",
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val (bgColor, textColor) = when (categoryType.uppercase()) {
        "PRODUCT" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "SERVICE" -> Pair(Color(0xFFF3E8FF), Color(0xFF6B21A8))
        "BUSINESS" -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        else -> Pair(PrimaryLight, TextPrimary)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        val vectorIcon = MtanavIcons.getVectorForCategoryOrEmoji(fallbackIcon, categoryType)
        Icon(
            imageVector = vectorIcon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        )
    }
}

@Composable
fun AppImage(
    imageUrl: String?,
    fallbackIcon: String = "🏪",
    categoryType: String = "BUSINESS",
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    AppThumbnail(
        imageUrl = imageUrl,
        fallbackIcon = fallbackIcon,
        categoryType = categoryType,
        modifier = modifier,
        shape = shape
    )
}
