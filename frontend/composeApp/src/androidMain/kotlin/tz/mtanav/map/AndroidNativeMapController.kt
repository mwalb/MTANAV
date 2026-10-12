package tz.mtanav.map

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import tz.mtanav.config.ApiConfig
import tz.mtanav.model.MapBusiness
import tz.mtanav.model.hasValidCoordinates

class AndroidNativeMapController(
    private val context: Context,
    private val onBusinessSelected: (MapBusiness) -> Unit,
    private val onViewShopClicked: (Long) -> Unit,
    private val onNavigateClicked: (MapBusiness) -> Unit
) : NativeMapController {

    private var googleMap: GoogleMap? = null
    private val markerMap = mutableMapOf<Marker, MapBusiness>()

    fun attachGoogleMap(map: GoogleMap) {
        this.googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.uiSettings?.isCompassEnabled = true

        googleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): android.view.View? = null

            override fun getInfoContents(marker: Marker): android.view.View {
                val business = markerMap[marker]

                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 20, 24, 20)
                    setBackgroundColor(Color.WHITE)
                }

                val titleView = TextView(context).apply {
                    text = business?.name ?: marker.title ?: ""
                    textSize = 15f
                    setTextColor(Color.parseColor("#172033"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                layout.addView(titleView)

                val detailsText = if (business != null) {
                    "⭐ ${business.rating} (${business.reviewCount}) • ${business.address}"
                } else {
                    marker.snippet ?: ""
                }
                val subtitleView = TextView(context).apply {
                    text = detailsText
                    textSize = 12f
                    setTextColor(Color.parseColor("#667085"))
                    setPadding(0, 4, 0, 8)
                }
                layout.addView(subtitleView)

                business?.matchingProduct?.let { match ->
                    val matchView = TextView(context).apply {
                        text = "Matching: $match"
                        textSize = 12f
                        setTextColor(Color.parseColor("#123B6D"))
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setPadding(0, 0, 0, 8)
                    }
                    layout.addView(matchView)
                }

                val actionHintView = TextView(context).apply {
                    text = "Tap window to View Shop"
                    textSize = 11f
                    setTextColor(Color.parseColor("#00A896"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                layout.addView(actionHintView)

                return layout
            }
        })

        googleMap?.setOnMarkerClickListener { marker ->
            val business = markerMap[marker]
            if (business != null) {
                onBusinessSelected(business)
            }
            marker.showInfoWindow()
            true
        }

        googleMap?.setOnInfoWindowClickListener { marker ->
            val business = markerMap[marker]
            if (business != null) {
                onViewShopClicked(business.id)
            }
        }

        ApiConfig.log("MAP_ANDROID", "MAP INITIALIZED")
    }

    override fun initialize() {
        ApiConfig.log("MAP_ANDROID", "MAP INIT START")
    }

    override fun clearMarkers() {
        googleMap?.clear()
        markerMap.clear()
        ApiConfig.log("MAP_ANDROID", "MAP MARKERS CLEARED")
    }

    override fun showBusinesses(businesses: List<MapBusiness>) {
        clearMarkers()

        val validBusinesses = businesses.filter { it.hasValidCoordinates() }
        val map = googleMap ?: return
        if (validBusinesses.isEmpty()) return

        val builder = LatLngBounds.Builder()

        validBusinesses.forEach { business ->
            val latLng = LatLng(business.latitude, business.longitude)
            builder.include(latLng)

            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(business.name)
                .snippet(business.address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

            val marker = map.addMarker(markerOptions)
            if (marker != null) {
                markerMap[marker] = business
            }
        }

        ApiConfig.log("MAP_ANDROID", "MAP MARKERS ADDED: ${validBusinesses.size}")

        if (validBusinesses.size == 1) {
            val single = validBusinesses.first()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(single.latitude, single.longitude), 15f))
        } else {
            val bounds = builder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            ApiConfig.log("MAP_ANDROID", "MAP FIT BOUNDS")
        }
    }

    override fun focusBusiness(business: MapBusiness) {
        if (!business.hasValidCoordinates()) return
        val map = googleMap ?: return
        val latLng = LatLng(business.latitude, business.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        showBusinessInfo(business)
    }

    override fun fitBusinesses(businesses: List<MapBusiness>) {
        val valid = businesses.filter { it.hasValidCoordinates() }
        val map = googleMap ?: return
        if (valid.isEmpty()) return
        if (valid.size == 1) {
            focusBusiness(valid.first())
            return
        }
        val builder = LatLngBounds.Builder()
        valid.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
    }

    override fun moveCamera(latitude: Double, longitude: Double, zoom: Float) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
    }

    override fun showBusinessInfo(business: MapBusiness) {
        markerMap.entries.firstOrNull { it.value.id == business.id }?.key?.showInfoWindow()
    }

    override fun clearSelection() {
        markerMap.keys.forEach { it.hideInfoWindow() }
    }

    override fun destroy() {
        clearMarkers()
        googleMap = null
    }
}
