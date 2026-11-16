package tz.mtanav.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tz.mtanav.model.MapBusiness

@Composable
expect fun MapCanvas(
    businesses: List<MapBusiness>,
    selectedBusiness: MapBusiness?,
    activeFilterTitle: String?,
    onMarkerSelected: (MapBusiness) -> Unit,
    onViewShopClicked: (Long) -> Unit,
    onNavigateClicked: (MapBusiness) -> Unit,
    onHamburgerClicked: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
    onOpenProductsRequested: () -> Unit = {},
    onOpenServicesRequested: () -> Unit = {},
    onOpenPromotionsRequested: () -> Unit = {},
    onOpenJobsRequested: () -> Unit = {},
    onOpenEarnRequested: () -> Unit = {},
    onSearchSubmitted: (String) -> Unit = {},
    onAuthRequested: () -> Unit = {},
    isDrawerOpen: Boolean = false,
    modifier: Modifier = Modifier
)
