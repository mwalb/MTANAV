@file:NoLiveLiterals

package tz.mtanav.map

import androidx.compose.runtime.NoLiveLiterals
import kotlinx.browser.document
import kotlinx.browser.window
import tz.mtanav.config.ApiConfig
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.hasValidCoordinates

class WebNativeMapController(
    private val containerId: String = "map",
    private val onBusinessSelected: (MapBusiness) -> Unit,
    private val onViewShopClicked: (Long) -> Unit,
    private val onNavigateClicked: (MapBusiness) -> Unit,
    private val onHamburgerClicked: () -> Unit = {},
    private val onSearchClicked: () -> Unit = {}
) : NativeMapController {

    private var jsMap: dynamic = null
    private val markersMap = mutableMapOf<Long, dynamic>()
    private var activeInfoWindow: dynamic = null
    private var userLocationMarker: dynamic = null
    private var selectedBusinessId: Long? = null

    override fun initialize() {
        if (jsMap != null) {
            triggerResize()
            return
        }

        ApiConfig.log("MAP_WEB", "INITIALIZING GOOGLE MAPS INSTANCE")
        val container = document.getElementById(containerId) ?: return

        val lat = ApiConfig.DEFAULT_LATITUDE
        val lng = ApiConfig.DEFAULT_LONGITUDE
        val zoomVal = ApiConfig.DEFAULT_ZOOM

        val mapObj = js("""
            new google.maps.Map(container, {
                zoom: zoomVal,
                center: { lat: lat, lng: lng },
                mapTypeControl: true,
                streetViewControl: true,
                zoomControl: true,
                fullscreenControl: true,
                gestureHandling: "greedy"
            })
        """)
        jsMap = mapObj

        // Render user current location marker
        renderUserLocationMarker(lat, lng)

        // Window resize listener
        window.addEventListener("resize", {
            triggerResize()
        })

        ApiConfig.log("MAP_WEB", "GOOGLE MAPS INSTANCE INITIALIZED SUCCESSFULLY")
    }

    fun triggerResize() {
        val mapInstance = jsMap ?: return
        js("google.maps.event.trigger(mapInstance, 'resize')")
    }

    private fun renderUserLocationMarker(lat: Double, lng: Double) {
        val mapInstance = jsMap ?: return
        if (userLocationMarker != null) {
            js("userLocationMarker.setMap(null)")
        }

        userLocationMarker = js("""
            new google.maps.Marker({
                position: { lat: lat, lng: lng },
                map: mapInstance,
                title: "Your Location",
                icon: {
                    path: google.maps.SymbolPath.CIRCLE,
                    scale: 7,
                    fillColor: "#0284C7",
                    fillOpacity: 1,
                    strokeWeight: 3,
                    strokeColor: "#FFFFFF"
                },
                zIndex: 999
            })
        """)
    }

    override fun clearMarkers() {
        markersMap.values.forEach { marker ->
            js("marker.setMap(null)")
        }
        markersMap.clear()
        activeInfoWindow?.close()
        activeInfoWindow = null
        selectedBusinessId = null
    }

    override fun showBusinesses(businesses: List<MapBusiness>) {
        if (jsMap == null) return

        val validBusinesses = businesses.filter { it.hasValidCoordinates() }
        val currentIds = validBusinesses.map { it.id }.toSet()

        // Remove markers no longer in list
        val toRemove = markersMap.keys.filter { it !in currentIds }
        toRemove.forEach { bId ->
            val m = markersMap.remove(bId)
            js("m.setMap(null)")
        }

        if (validBusinesses.isEmpty()) return

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
            val isSelected = (bId == selectedBusinessId)

            js("bounds.extend({ lat: bLat, lng: bLng })")

            val matchHtml = if (business.matchingProduct != null) {
                "<div style='color:#176B65; font-weight:600; font-size:12px; margin-bottom:4px;'>Matching: ${business.matchingProduct}${if (business.price != null) " (TZS ${business.price.toInt()})" else ""}</div>"
            } else if (business.matchingService != null) {
                "<div style='color:#176B65; font-weight:600; font-size:12px; margin-bottom:4px;'>Service: ${business.matchingService}</div>"
            } else ""

            val infoHtml = """
                <div style="padding:8px; font-family:-apple-system, sans-serif; color:#24332F; max-width:240px;">
                    <div style="font-weight:bold; font-size:14px; margin-bottom:2px;">$bName</div>
                    <div style="color:#687773; font-size:12px; margin-bottom:4px;">⭐ $bRating ($bReviews) • $bAddress</div>
                    $matchHtml
                    <div style="display:flex; gap:6px; margin-top:8px;">
                        <button id="mtanav-btn-view-$bId" style="background-color:#176B65; color:white; border:none; padding:6px 12px; border-radius:6px; font-size:11px; font-weight:bold; cursor:pointer;">VIEW SHOP</button>
                        <button id="mtanav-btn-nav-$bId" style="background-color:#E8AD4A; color:#124E4A; border:none; padding:6px 12px; border-radius:6px; font-size:11px; font-weight:bold; cursor:pointer;">NAVIGATE</button>
                    </div>
                </div>
            """.trimIndent()

            val fillColor = if (isSelected) "#E8AD4A" else "#176B65"
            val strokeColor = if (isSelected) "#124E4A" else "#FFFFFF"
            val scaleVal = if (isSelected) 12 else 9

            val existingMarker = markersMap[bId]
            if (existingMarker != null) {
                js("""
                    existingMarker.setIcon({
                        path: google.maps.SymbolPath.CIRCLE,
                        scale: scaleVal,
                        fillColor: fillColor,
                        fillOpacity: 1,
                        strokeWeight: 2,
                        strokeColor: strokeColor
                    })
                """)
            } else {
                val marker = js("""
                    new google.maps.Marker({
                        position: { lat: bLat, lng: bLng },
                        map: mapInstance,
                        title: bName,
                        icon: {
                            path: google.maps.SymbolPath.CIRCLE,
                            scale: scaleVal,
                            fillColor: fillColor,
                            fillOpacity: 1,
                            strokeWeight: 2,
                            strokeColor: strokeColor
                        }
                    })
                """)

                val infoWindow = js("new google.maps.InfoWindow({ content: infoHtml })")

                marker.addListener("click", {
                    activeInfoWindow?.close()
                    infoWindow.open(mapInstance, marker)
                    activeInfoWindow = infoWindow
                    selectedBusinessId = bId
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

                markersMap[bId] = marker
            }
        }

        if (validBusinesses.size == 1) {
            val first = validBusinesses.first()
            val fLat = first.latitude
            val fLng = first.longitude
            js("mapInstance.setCenter({ lat: fLat, lng: fLng })")
            js("mapInstance.setZoom(15)")
        } else if (validBusinesses.size > 1) {
            js("mapInstance.fitBounds(bounds)")
        }
    }

    override fun focusBusiness(business: MapBusiness) {
        if (!business.hasValidCoordinates() || jsMap == null) return
        val mapInstance = jsMap
        val bLat = business.latitude
        val bLng = business.longitude
        selectedBusinessId = business.id
        js("mapInstance.panTo({ lat: bLat, lng: bLng })")
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
        selectedBusinessId = null
    }

    override fun destroy() {
        clearMarkers()
        if (userLocationMarker != null) {
            js("userLocationMarker.setMap(null)")
            userLocationMarker = null
        }
        jsMap = null
    }
}
