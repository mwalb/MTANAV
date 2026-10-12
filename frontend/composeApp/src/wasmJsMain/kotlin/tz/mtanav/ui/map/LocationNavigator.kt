package tz.mtanav.ui.map

import kotlinx.browser.window

actual class LocationNavigator {
    actual fun launchGoogleMapsNavigation(latitude: Double, longitude: Double, businessName: String) {
        val url = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
        window.open(url, "_blank")
    }

    actual fun openPhoneCall(phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        window.open("tel:$cleanNumber", "_self")
    }

    actual fun openWhatsApp(phoneNumber: String, message: String) {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val encodedMsg = encodeUri(message)
        val url = "https://wa.me/$cleanNumber?text=$encodedMsg"
        window.open(url, "_blank")
    }

    private fun encodeUri(s: String): String = s.map { c ->
        if (c.isLetterOrDigit() || c in ".-_~") c.toString()
        else "%" + c.code.toString(16).padStart(2, '0').uppercase()
    }.joinToString("")
}
