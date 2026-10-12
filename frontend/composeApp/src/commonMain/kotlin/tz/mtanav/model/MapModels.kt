package tz.mtanav.model

import kotlinx.serialization.Serializable

sealed interface SidebarMode {
    data object Explore : SidebarMode
    data object AllProducts : SidebarMode
    data object AllServices : SidebarMode
    data class ProductResults(
        val productId: Long,
        val productName: String,
        val icon: String = "📦"
    ) : SidebarMode
    data class ServiceResults(
        val serviceId: Long,
        val serviceName: String,
        val icon: String = "⚙️"
    ) : SidebarMode
    data class QueryResults(
        val query: String
    ) : SidebarMode
}

enum class SortOption(val label: String) {
    NEAREST("Nearest"),
    LOWEST_PRICE("Lowest price"),
    HIGHEST_RATING("Highest rating")
}

@Serializable
data class FilterState(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val distanceKm: Double? = null,
    val availableNow: Boolean = false,
    val sortOption: SortOption = SortOption.NEAREST
)

@Serializable
data class MapBusiness(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val address: String,
    val city: String,
    val buildingName: String? = null,
    val shopNumber: String? = null,
    val matchingProduct: String? = null,
    val matchingService: String? = null,
    val price: Double? = null,
    val availability: String? = "IN_STOCK",
    val category: String? = null,
    val imageUrl: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val verified: Boolean = true
)

fun MapBusiness.hasValidCoordinates(): Boolean {
    return latitude in -90.0..90.0 && longitude in -180.0..180.0 && !(latitude == 0.0 && longitude == 0.0)
}

fun BusinessMapDto.toMapBusiness(): MapBusiness = MapBusiness(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    rating = rating,
    reviewCount = reviewCount,
    address = address,
    city = city,
    buildingName = buildingName,
    shopNumber = shopNumber,
    matchingProduct = matchingItemName,
    price = startingPrice
)

sealed interface MapLoadState {
    data object Loading : MapLoadState
    data object Success : MapLoadState
    data class Error(val message: String, val details: String? = null) : MapLoadState
}

data class MtanavMapState(
    val sidebarMode: SidebarMode = SidebarMode.Explore,
    val searchQuery: String = "",
    val productSearchQuery: String = "",
    val serviceSearchQuery: String = "",
    val filterState: FilterState = FilterState(),
    val businesses: List<MapBusiness> = emptyList(),
    val selectedBusiness: MapBusiness? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val mapLoadState: MapLoadState = MapLoadState.Loading
)
