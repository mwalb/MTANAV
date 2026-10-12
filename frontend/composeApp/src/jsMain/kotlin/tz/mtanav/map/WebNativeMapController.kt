package tz.mtanav.map

import kotlinx.browser.document
import kotlinx.browser.window
import tz.mtanav.config.ApiConfig
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.hasValidCoordinates

class WebNativeMapController(
    private val containerId: String = "map",
    private val onBusinessSelected: (MapBusiness) -> Unit,
    private val onViewShopClicked: (Long) -> Unit,
    private val onNavigateClicked: (MapBusiness) -> Unit
) : NativeMapController {

    private var jsMap: dynamic = null
    private val markersMap = mutableMapOf<Long, dynamic>()
    private var activeInfoWindow: dynamic = null

    override fun initialize() {
        ApiConfig.log("MAP_WEB", "MAP INIT START")
        val container = document.getElementById(containerId) ?: return

        val lat = ApiConfig.DEFAULT_LATITUDE
        val lng = ApiConfig.DEFAULT_LONGITUDE
        val zoomVal = ApiConfig.DEFAULT_ZOOM

        jsMap = js("""
            new google.maps.Map(container, {
                zoom: zoomVal,
                center: { lat: lat, lng: lng },
                mapTypeControl: true,
                streetViewControl: false,
                zoomControl: true,
                fullscreenControl: true
            })
        """)
        ApiConfig.log("MAP_WEB", "MAP INITIALIZED")
    }

    override fun clearMarkers() {
        markersMap.values.forEach { marker ->
            marker.setMap(null)
        }
        markersMap.clear()
        activeInfoWindow?.close()
        activeInfoWindow = null
        ApiConfig.log("MAP_WEB", "MAP MARKERS CLEARED")
    }

    override fun showBusinesses(businesses: List<MapBusiness>) {
        clearMarkers()

        val validBusinesses = businesses.filter { it.hasValidCoordinates() }
        if (validBusinesses.isEmpty() || jsMap == null) return

        val mapInstance = jsMap
        val bounds = js("new google.maps.LatLngBounds()")

        validBusinesses.forEach { business ->
            val bLat = business.latitude
            val bLng = business.longitude
            val bName = business.name
            val bId = business.id
            val bRating = business.rating
            val bReviews = business.reviewCount
            val bAddress = business.address

            js("bounds.extend({ lat: bLat, lng: bLng })")

            val matchHtml = if (business.matchingProduct != null) {
                "<div style='color:#123B6D; font-weight:600; font-size:12px; margin-bottom:4px;'>Matching: ${business.matchingProduct}${if (business.price != null) " (TSh ${business.price.toInt()})" else ""}</div>"
            } else if (business.matchingService != null) {
                "<div style='color:#123B6D; font-weight:600; font-size:12px; margin-bottom:4px;'>Service: ${business.matchingService}</div>"
            } else ""

            val infoHtml = """
                <div style="padding:8px; font-family:sans-serif; color:#172033; max-width:240px;">
                    <div style="font-weight:bold; font-size:14px; margin-bottom:2px;">$bName</div>
                    <div style="color:#667085; font-size:12px; margin-bottom:4px;">⭐ $bRating ($bReviews) • $bAddress</div>
                    $matchHtml
                    <div style="display:flex; gap:6px; margin-top:8px;">
                        <button id="mtanav-btn-view-$bId" style="background-color:#123B6D; color:white; border:none; padding:6px 12px; border-radius:6px; font-size:11px; font-weight:bold; cursor:pointer;">VIEW SHOP</button>
                        <button id="mtanav-btn-nav-$bId" style="background-color:#00A896; color:white; border:none; padding:6px 12px; border-radius:6px; font-size:11px; font-weight:bold; cursor:pointer;">NAVIGATE</button>
                    </div>
                </div>
            """.trimIndent()

            val marker = js("""
                new google.maps.Marker({
                    position: { lat: bLat, lng: bLng },
                    map: mapInstance,
                    title: bName,
                    icon: {
                        path: google.maps.SymbolPath.CIRCLE,
                        scale: 8,
                        fillColor: "#123B6D",
                        fillOpacity: 1,
                        strokeWeight: 2,
                        strokeColor: "#FFFFFF"
                    }
                })
            """)

            val infoWindow = js("new google.maps.InfoWindow({ content: infoHtml })")

            marker.addListener("click", {
                activeInfoWindow?.close()
                infoWindow.open(mapInstance, marker)
                activeInfoWindow = infoWindow
                onBusinessSelected(business)

                window.setTimeout({
                    val viewBtn = document.getElementById("mtanav-btn-view-$bId")
                    viewBtn?.addEventListener("click", {
                        onViewShopClicked(bId)
                    })

                    val navBtn = document.getElementById("mtanav-btn-nav-$bId")
                    navBtn?.addEventListener("click", {
                        onNavigateClicked(business)
                    })
                }, 100)
            })

            markersMap[business.id] = marker
        }

        ApiConfig.log("MAP_WEB", "MAP MARKERS ADDED: ${validBusinesses.size}")

        if (validBusinesses.size == 1) {
            val first = validBusinesses.first()
            val fLat = first.latitude
            val fLng = first.longitude
            js("mapInstance.setCenter({ lat: fLat, lng: fLng })")
            js("mapInstance.setZoom(15)")
        } else if (validBusinesses.size > 1) {
            js("mapInstance.fitBounds(bounds)")
            ApiConfig.log("MAP_WEB", "MAP FIT BOUNDS")
        }
    }

    override fun focusBusiness(business: MapBusiness) {
        if (!business.hasValidCoordinates() || jsMap == null) return
        val mapInstance = jsMap
        val bLat = business.latitude
        val bLng = business.longitude
        js("mapInstance.setCenter({ lat: bLat, lng: bLng })")
        js("mapInstance.setZoom(16)")
        showBusinessInfo(business)
    }

    override fun fitBusinesses(businesses: List<MapBusiness>) {
        val valid = businesses.filter { it.hasValidCoordinates() }
        if (valid.isEmpty() || jsMap == null) return
        if (valid.size == 1) {
            focusBusiness(valid.first())
            return
        }
        val mapInstance = jsMap
        val bounds = js("new google.maps.LatLngBounds()")
        valid.forEach { b ->
            val lat = b.latitude
            val lng = b.longitude
            js("bounds.extend({ lat: lat, lng: lng })")
        }
        js("mapInstance.fitBounds(bounds)")
    }

    override fun moveCamera(latitude: Double, longitude: Double, zoom: Float) {
        if (jsMap == null) return
        val mapInstance = jsMap
        js("mapInstance.setCenter({ lat: latitude, lng: longitude })")
        js("mapInstance.setZoom(zoom)")
    }

    override fun showBusinessInfo(business: MapBusiness) {
        val targetMarker = markersMap[business.id] ?: return
        js("google.maps.event.trigger(targetMarker, 'click')")
    }

    override fun clearSelection() {
        activeInfoWindow?.close()
        activeInfoWindow = null
    }

    override fun destroy() {
        clearMarkers()
        jsMap = null
    }
}
