package tz.mtanav.ui.map

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.MapView
import tz.mtanav.map.AndroidNativeMapController
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var loadState by remember { mutableStateOf<MapLoadState>(MapLoadState.Loading) }
    var retryTrigger by remember { mutableStateOf(0) }

    val controller = remember(context) {
        AndroidNativeMapController(
            context = context,
            onBusinessSelected = onMarkerSelected,
            onViewShopClicked = onViewShopClicked,
            onNavigateClicked = onNavigateClicked
        )
    }

    val mapView = remember(context, retryTrigger) {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    mapView.onDestroy()
                    controller.destroy()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
                controller.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(mapView, retryTrigger) {
        loadState = MapLoadState.Loading
        try {
            mapView.getMapAsync { googleMap ->
                controller.attachGoogleMap(googleMap)
                loadState = MapLoadState.Success
                controller.showBusinesses(businesses)
            }
        } catch (e: Exception) {
            loadState = MapLoadState.Error("Failed to initialize Google Maps SDK: ${e.message}")
        }
    }

    LaunchedEffect(businesses, loadState) {
        if (loadState is MapLoadState.Success) {
            controller.showBusinesses(businesses)
        }
    }

    LaunchedEffect(selectedBusiness, loadState) {
        if (loadState is MapLoadState.Success && selectedBusiness != null) {
            controller.focusBusiness(selectedBusiness)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = loadState) {
            is MapLoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryDeepBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading Google Maps SDK...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }
            }

            is MapLoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(AppBackground).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.widthIn(max = 400.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🗺️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Map Could Not Be Loaded",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
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
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
