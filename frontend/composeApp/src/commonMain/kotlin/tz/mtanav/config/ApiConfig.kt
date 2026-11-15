package tz.mtanav.config

object ApiConfig {
    /**
     * Default Google Maps API Key for development.
     * Can be replaced or configured per build/environment.
     */
    const val GOOGLE_MAPS_API_KEY = "AIzaSyDnKLNd3113Sj2042jlLdZVbCxblsjAgf8"

    /**
     * Default initial camera position (Dar es Salaam, Tanzania).
     */
    const val DEFAULT_LATITUDE = -6.7924
    const val DEFAULT_LONGITUDE = 39.2083
    const val DEFAULT_ZOOM = 12f

    /**
     * Backend API Base URL.
     */
    fun getBaseUrl(): String {
        return "https://mtanav.fly.dev/api"
    }

    /**
     * Development logger helper for Map and API lifecycle events.
     */
    fun log(tag: String, message: String) {
        println("[$tag] $message")
    }
}
