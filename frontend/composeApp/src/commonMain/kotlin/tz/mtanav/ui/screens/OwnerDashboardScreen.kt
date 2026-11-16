package tz.mtanav.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tz.mtanav.network.MtanavRepository

@Composable
fun OwnerDashboardScreen(
    repository: MtanavRepository,
    onBackClicked: () -> Unit,
    onOpenPublicShop: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OwnerPortalScreen(
        repository = repository,
        initialSection = OwnerSection.Dashboard,
        onNavigateToPublicMap = onBackClicked,
        onOpenPublicShop = onOpenPublicShop,
        onLogoutClicked = onBackClicked,
        modifier = modifier
    )
}
