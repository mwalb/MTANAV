package tz.mtanav.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

object MtanavIcons {
    val Search: ImageVector = Icons.Default.Search
    val Close: ImageVector = Icons.Default.Close
    val Menu: ImageVector = Icons.Default.Menu
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowForward: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val ShoppingCart: ImageVector = Icons.Default.ShoppingCart
    val ShoppingBag: ImageVector = Icons.Default.ShoppingCart
    val Storefront: ImageVector = Icons.Default.Home
    val LocationOn: ImageVector = Icons.Default.LocationOn
    val Phone: ImageVector = Icons.Default.Phone
    val Email: ImageVector = Icons.Default.Email
    val LocalShipping: ImageVector = Icons.Default.ShoppingCart
    val Work: ImageVector = Icons.Default.Place
    val LocalOffer: ImageVector = Icons.Default.Star
    val Star: ImageVector = Icons.Default.Star
    val Person: ImageVector = Icons.Default.Person
    val Lock: ImageVector = Icons.Default.Lock
    val ExitToApp: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val Check: ImageVector = Icons.Default.Check
    val CheckCircle: ImageVector = Icons.Default.CheckCircle
    val Build: ImageVector = Icons.Default.Settings
    val FilterList: ImageVector = Icons.Default.Search
    val Map: ImageVector = Icons.Default.Place
    val Navigation: ImageVector = Icons.Default.LocationOn

    fun getVectorForCategoryOrEmoji(iconStr: String?, categoryType: String = "PRODUCT"): ImageVector {
        val str = iconStr?.lowercase() ?: ""
        return when {
            str.contains("car") || str.contains("garage") || str.contains("wash") || str.contains("🚗") -> Icons.Default.Place
            str.contains("phone") || str.contains("charger") || str.contains("mobile") || str.contains("📱") || str.contains("🔌") -> Icons.Default.Phone
            str.contains("service") || str.contains("repair") || str.contains("gear") || str.contains("⚙") || str.contains("🔧") -> Icons.Default.Settings
            str.contains("food") || str.contains("rice") || str.contains("spoon") || str.contains("restaurant") || str.contains("🍚") || str.contains("🥄") || str.contains("🍔") -> Icons.Default.Place
            str.contains("job") || str.contains("work") || str.contains("briefcase") || str.contains("💼") -> Icons.Default.Place
            str.contains("offer") || str.contains("promo") || str.contains("discount") || str.contains("tag") || str.contains("🏷") || str.contains("🎁") -> Icons.Default.Star
            str.contains("shop") || str.contains("store") || str.contains("business") || str.contains("🏢") || str.contains("🏪") -> Icons.Default.Home
            str.contains("cart") || str.contains("checkout") || str.contains("🛒") -> Icons.Default.ShoppingCart
            str.contains("deliver") || str.contains("truck") || str.contains("🚚") -> Icons.Default.ShoppingCart
            str.contains("location") || str.contains("pin") || str.contains("map") || str.contains("📍") || str.contains("🗺") -> Icons.Default.LocationOn
            str.contains("user") || str.contains("profile") || str.contains("person") || str.contains("👤") -> Icons.Default.Person
            categoryType.equals("SERVICE", ignoreCase = true) -> Icons.Default.Settings
            categoryType.equals("BUSINESS", ignoreCase = true) -> Icons.Default.Home
            else -> Icons.Default.ShoppingCart
        }
    }
}
