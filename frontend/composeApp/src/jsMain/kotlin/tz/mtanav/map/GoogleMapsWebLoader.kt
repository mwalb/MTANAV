package tz.mtanav.map

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLScriptElement
import tz.mtanav.config.ApiConfig

object GoogleMapsWebLoader {

    private var isLoading = false
    private var isLoaded = false

    fun load(
        apiKey: String = ApiConfig.GOOGLE_MAPS_API_KEY,
        onSuccess: () -> Unit,
        onError: (message: String, details: String?) -> Unit
    ) {
        val googleExists = js("typeof google !== 'undefined' && typeof google.maps !== 'undefined'") as Boolean
        if (isLoaded || googleExists) {
            isLoaded = true
            isLoading = false
            onSuccess()
            return
        }

        isLoading = true

        val callbackName = "initMtanavGoogleMaps"
        window.asDynamic()[callbackName] = {
            isLoading = false
            isLoaded = true
            ApiConfig.log("MAP_WEB", "MAP API LOADED")
            onSuccess()
        }

        window.asDynamic()["gm_authFailure"] = {
            isLoading = false
            isLoaded = false
            val errorMsg = "Google Maps API key authorization failed. Please check your Maps JavaScript API configuration."
            ApiConfig.log("MAP_WEB", "MAP ERROR: $errorMsg")
            onError("Map could not be loaded", errorMsg)
        }

        val existingScript = document.getElementById("google-maps-js-sdk")
        if (existingScript != null) {
            existingScript.remove()
        }

        val script = (document.createElement("script") as HTMLScriptElement).apply {
            id = "google-maps-js-sdk"
            src = "https://maps.googleapis.com/maps/api/js?key=$apiKey&callback=$callbackName"
            async = true
            defer = true
            onerror = { _, _, _, _, _ ->
                isLoading = false
                isLoaded = false
                val errorMsg = "Failed to load Google Maps JavaScript API. Check network connection and API key configuration."
                ApiConfig.log("MAP_WEB", "MAP ERROR: $errorMsg")
                onError("Map could not be loaded", errorMsg)
            }
        }

        document.head?.appendChild(script)
    }
}
