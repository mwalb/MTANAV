package tz.mtanav.ui.map

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class LocationNavigator(private val context: Context) {
    actual fun launchGoogleMapsNavigation(latitude: Double, longitude: Double, businessName: String) {
        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(businessName)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        }
    }

    actual fun openPhoneCall(phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    actual fun openWhatsApp(phoneNumber: String, message: String) {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val encodedMsg = Uri.encode(message)
        val url = "https://wa.me/$cleanNumber?text=$encodedMsg"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
