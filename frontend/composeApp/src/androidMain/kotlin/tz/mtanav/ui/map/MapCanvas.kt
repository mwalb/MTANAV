package tz.mtanav.ui.map

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.MapView
import tz.mtanav.map.AndroidNativeMapController
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.MapLoadState

@Composable
actual fun MapCanvas(
    businesses: List<MapBusiness>,
    selectedBusiness: MapBusiness?,
    activeFilterTitle: String?,
    onMarkerSelected: (MapBusiness) -> Unit,
    onViewShopClicked: (Long) -> Unit,
    onNavigateClicked: (MapBusiness) -> Unit,
    onHamburgerClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onOpenProductsRequested: () -> Unit,
    onOpenServicesRequested: () -> Unit,
    onOpenPromotionsRequested: () -> Unit,
    onOpenJobsRequested: () -> Unit,
    onOpenEarnRequested: () -> Unit,
    onSearchSubmitted: (String) -> Unit,
    onAuthRequested: () -> Unit,
    isDrawerOpen: Boolean,
    modifier: Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var loadState by remember { mutableStateOf<MapLoadState>(MapLoadState.Loading) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapController by remember { mutableStateOf<AndroidNativeMapController?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView?.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapController?.destroy()
        }
    }

    LaunchedEffect(mapView) {
        val view = mapView ?: return@LaunchedEffect
        val controller = AndroidNativeMapController(
            context = context,
            onBusinessSelected = onMarkerSelected,
            onViewShopClicked = onViewShopClicked,
            onNavigateClicked = onNavigateClicked
        )
        mapController = controller
        view.getMapAsync { googleMap ->
            controller.attachGoogleMap(googleMap)
            loadState = MapLoadState.Success
        }
        controller.initialize()
    }

    LaunchedEffect(businesses, loadState) {
        if (loadState is MapLoadState.Success) {
            mapController?.showBusinesses(businesses)
        }
    }

    LaunchedEffect(selectedBusiness, loadState) {
        if (loadState is MapLoadState.Success && selectedBusiness != null) {
            mapController?.focusBusiness(selectedBusiness)
        }
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).also { mv ->
                mapView = mv
                mv.onCreate(Bundle())
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
