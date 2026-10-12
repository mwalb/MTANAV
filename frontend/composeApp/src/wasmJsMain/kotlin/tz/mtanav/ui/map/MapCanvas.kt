package tz.mtanav.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tz.mtanav.model.MapBusiness
import tz.mtanav.theme.HighlightGold
import tz.mtanav.theme.PrimaryDeepBlue
import tz.mtanav.theme.SurfaceWhite
import tz.mtanav.theme.TextPrimary
import tz.mtanav.theme.TextSecondary

@Composable
actual fun MapCanvas(
    businesses: List<MapBusiness>,
    selectedBusiness: MapBusiness?,
    activeFilterTitle: String?,
    onMarkerSelected: (MapBusiness) -> Unit,
    onViewShopClicked: (Long) -> Unit,
    onNavigateClicked: (MapBusiness) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (activeFilterTitle != null) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PrimaryDeepBlue,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔍", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Showing: $activeFilterTitle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = CircleShape,
                        color = HighlightGold
                    ) {
                        Text(
                            text = "${businesses.size} found",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        if (businesses.isEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .widthIn(max = 360.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No businesses found in this area",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try expanding your search area, removing filters, or searching another product or service.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
