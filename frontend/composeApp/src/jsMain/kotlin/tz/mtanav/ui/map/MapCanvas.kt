package tz.mtanav.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.document
import tz.mtanav.map.GoogleMapsWebLoader
import tz.mtanav.map.WebNativeMapController
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.MapLoadState
import tz.mtanav.theme.*

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
    var loadState by remember { mutableStateOf<MapLoadState>(MapLoadState.Loading) }
    var retryTrigger by remember { mutableStateOf(0) }

    val mapController = remember {
        WebNativeMapController(
            containerId = "map",
            onBusinessSelected = onMarkerSelected,
            onViewShopClicked = onViewShopClicked,
            onNavigateClicked = onNavigateClicked
        )
    }

    DisposableEffect(Unit) {
        val composeRoot = document.getElementById("compose-root")
        val mapRoot = document.getElementById("map-root")

        composeRoot?.classList?.add("with-map")
        mapRoot?.classList?.add("visible")

        onDispose {
            composeRoot?.classList?.remove("with-map")
            mapRoot?.classList?.remove("visible")
            mapController.destroy()
        }
    }

    LaunchedEffect(retryTrigger) {
        loadState = MapLoadState.Loading
        GoogleMapsWebLoader.load(
            onSuccess = {
                loadState = MapLoadState.Success
                mapController.initialize()
                mapController.showBusinesses(businesses)
            },
            onError = { message, details ->
                loadState = MapLoadState.Error(message, details)
            }
        )
    }

    LaunchedEffect(businesses, loadState) {
        if (loadState is MapLoadState.Success) {
            mapController.showBusinesses(businesses)
        }
    }

    LaunchedEffect(selectedBusiness, loadState) {
        if (loadState is MapLoadState.Success && selectedBusiness != null) {
            mapController.focusBusiness(selectedBusiness)
        }
    }

    when (val state = loadState) {
        is MapLoadState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryDeepBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading Google Maps...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        }

        is MapLoadState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(AppBackground)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.widthIn(max = 420.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🗺️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.details ?: "Check your Google Maps API key and Maps JavaScript API configuration.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { retryTrigger++ },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDeepBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Retry Loading Map", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        is MapLoadState.Success -> {
            // Native HTML map is visible in DOM #map-root container directly.
        }
    }
}
