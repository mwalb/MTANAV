package tz.mtanav.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object MtanavIcons {
    val Search: ImageVector = Icons.Default.Search
    val Close: ImageVector = Icons.Default.Close
    val Menu: ImageVector = Icons.Default.Menu
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowForward: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val ShoppingCart: ImageVector = Icons.Default.ShoppingCart
    val ShoppingBag: ImageVector = Icons.Default.ShoppingBag
    val Storefront: ImageVector = Icons.Default.Storefront
    val LocationOn: ImageVector = Icons.Default.LocationOn
    val Phone: ImageVector = Icons.Default.Phone
    val Email: ImageVector = Icons.Default.Email
    val LocalShipping: ImageVector = Icons.Default.LocalShipping
    val Work: ImageVector = Icons.Default.Work
    val LocalOffer: ImageVector = Icons.Default.LocalOffer
    val Star: ImageVector = Icons.Default.Star
    val Person: ImageVector = Icons.Default.Person
    val Lock: ImageVector = Icons.Default.Lock
    val ExitToApp: ImageVector = Icons.Default.ExitToApp
    val Check: ImageVector = Icons.Default.Check
    val CheckCircle: ImageVector = Icons.Default.CheckCircle
    val Build: ImageVector = Icons.Default.Build
    val FilterList: ImageVector = Icons.Default.FilterList
    val Map: ImageVector = Icons.Default.Map
    val Navigation: ImageVector = Icons.Default.Navigation

    fun getVectorForCategoryOrEmoji(iconStr: String?, categoryType: String = "PRODUCT"): ImageVector {
        val str = iconStr?.lowercase() ?: ""
        return when {
            str.contains("car") || str.contains("garage") || str.contains("wash") || str.contains("🚗") -> Icons.Default.DirectionsCar
            str.contains("phone") || str.contains("charger") || str.contains("mobile") || str.contains("📱") || str.contains("🔌") -> Icons.Default.PhoneIphone
            str.contains("service") || str.contains("repair") || str.contains("gear") || str.contains("⚙") || str.contains("🔧") -> Icons.Default.Build
            str.contains("food") || str.contains("rice") || str.contains("spoon") || str.contains("restaurant") || str.contains("🍚") || str.contains("🥄") || str.contains("🍔") -> Icons.Default.Restaurant
            str.contains("job") || str.contains("work") || str.contains("briefcase") || str.contains("💼") -> Icons.Default.Work
            str.contains("offer") || str.contains("promo") || str.contains("discount") || str.contains("tag") || str.contains("🏷") || str.contains("🎁") -> Icons.Default.LocalOffer
            str.contains("shop") || str.contains("store") || str.contains("business") || str.contains("🏢") || str.contains("🏪") -> Icons.Default.Storefront
            str.contains("cart") || str.contains("checkout") || str.contains("🛒") -> Icons.Default.ShoppingCart
            str.contains("deliver") || str.contains("truck") || str.contains("🚚") -> Icons.Default.LocalShipping
            str.contains("location") || str.contains("pin") || str.contains("map") || str.contains("📍") || str.contains("🗺") -> Icons.Default.LocationOn
            str.contains("user") || str.contains("profile") || str.contains("person") || str.contains("👤") -> Icons.Default.Person
            categoryType.equals("SERVICE", ignoreCase = true) -> Icons.Default.Build
            categoryType.equals("BUSINESS", ignoreCase = true) -> Icons.Default.Storefront
            else -> Icons.Default.ShoppingBag
        }
    }
}
