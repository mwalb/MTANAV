package tz.mtanav.ui.map

expect class LocationNavigator {
    fun launchGoogleMapsNavigation(latitude: Double, longitude: Double, businessName: String)
    fun openPhoneCall(phoneNumber: String)
    fun openWhatsApp(phoneNumber: String, message: String)
}

