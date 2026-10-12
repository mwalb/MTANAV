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
    modifier: Modifier = Modifier
)
