package tz.mtanav.map

import tz.mtanav.model.MapBusiness

interface NativeMapController {
    fun initialize()
    fun clearMarkers()
    fun showBusinesses(businesses: List<MapBusiness>)
    fun focusBusiness(business: MapBusiness)
    fun fitBusinesses(businesses: List<MapBusiness>)
    fun moveCamera(latitude: Double, longitude: Double, zoom: Float)
    fun showBusinessInfo(business: MapBusiness)
    fun clearSelection()
    fun destroy()
}
