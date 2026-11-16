package tz.mtanav.ui.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import tz.mtanav.map.GoogleMapsWebLoader
import tz.mtanav.map.WebNativeMapController
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
    var loadState by remember { mutableStateOf<MapLoadState>(MapLoadState.Loading) }

    val mapController = remember {
        WebNativeMapController(
            containerId = "map",
            onBusinessSelected = onMarkerSelected,
            onViewShopClicked = onViewShopClicked,
            onNavigateClicked = onNavigateClicked,
            onHamburgerClicked = onHamburgerClicked,
            onSearchClicked = onSearchSubmitted,
            onProductsClicked = onOpenProductsRequested,
            onServicesClicked = onOpenServicesRequested,
            onPromotionsClicked = onOpenPromotionsRequested,
            onJobsClicked = onOpenJobsRequested,
            onEarnClicked = onOpenEarnRequested,
            onAuthClicked = onAuthRequested
        )
    }

    DisposableEffect(Unit) {
        js("""
            var composeRoot = document.getElementById("compose-root");
            var mapRoot = document.getElementById("map-root");
            if (composeRoot) composeRoot.style.display = "none";
            if (mapRoot) mapRoot.style.display = "flex";
        """)

        onDispose {
            js("""
                var composeRoot = document.getElementById("compose-root");
                var mapRoot = document.getElementById("map-root");
                if (composeRoot) composeRoot.style.display = "block";
                if (mapRoot) mapRoot.style.display = "none";
            """)
            mapController.destroy()
        }
    }

    LaunchedEffect(isDrawerOpen) {
        val composeRoot = document.getElementById("compose-root")
        if (isDrawerOpen) {
            composeRoot?.classList?.add("drawer-active")
        } else {
            composeRoot?.classList?.remove("drawer-active")
        }
        js("window.dispatchEvent(new Event('resize'))")
    }

    LaunchedEffect(Unit) {
        loadState = MapLoadState.Loading
        GoogleMapsWebLoader.load(
            onSuccess = {
                loadState = MapLoadState.Success
                mapController.initialize()
                mapController.showBusinesses(businesses)
                js("window.dispatchEvent(new Event('resize'))")
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
}
