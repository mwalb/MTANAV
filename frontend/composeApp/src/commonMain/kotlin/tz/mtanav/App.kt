package tz.mtanav

import androidx.compose.runtime.*
import tz.mtanav.model.BusinessProductDto
import tz.mtanav.model.CartItem
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.MtanavTheme
import tz.mtanav.ui.map.LocationNavigator
import tz.mtanav.ui.screens.*

sealed class Screen {
    data object HomeMap : Screen()
    data class Login(val targetScreenAfterAuth: Screen? = null) : Screen()
    data class Register(val targetScreenAfterAuth: Screen? = null) : Screen()
    data object BusinessRegistration : Screen()
    data class ShopProducts(
        val businessId: Long,
        val selectedProductId: Long? = null,
        val selectedServiceId: Long? = null
    ) : Screen()
    data class ProductDetails(val product: BusinessProductDto) : Screen()
    data class CartCheckout(val items: List<CartItem>) : Screen()
    data class OwnerPortal(
        val initialSection: OwnerSection = OwnerSection.Dashboard,
        val targetConversationId: Long? = null
    ) : Screen()
    data class CustomerPortal(
        val initialSection: CustomerSection = CustomerSection.Dashboard,
        val targetConversationId: Long? = null,
        val targetBusinessIdForMessage: Long? = null
    ) : Screen()
    data object AdminDashboard : Screen()
    data object LocalJobs : Screen()
    data class JobDetails(val jobId: Long) : Screen()
    data object Promotions : Screen()
    data class PromotionDetails(val promotionId: Long) : Screen()
}

@Composable
fun App(locationNavigator: LocationNavigator) {
    val repository = remember { MtanavRepository() }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.HomeMap) }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var showSplash by remember { mutableStateOf(true) }

    fun routeAfterAuth() {
        val user = repository.currentUser
        val roles = user?.roles ?: emptyList()
        when {
            roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN") -> {
                currentScreen = Screen.AdminDashboard
            }
            roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER") -> {
                currentScreen = Screen.OwnerPortal()
            }
            else -> {
                currentScreen = Screen.CustomerPortal()
            }
        }
    }

    MtanavTheme {
        if (showSplash) {
            tz.mtanav.ui.components.SplashScreen(
                onSplashFinished = {
                    showSplash = false
                }
            )
        } else {
            when (val screen = currentScreen) {
            is Screen.HomeMap -> {
                HomeMapScreen(
                    repository = repository,
                    locationNavigator = locationNavigator,
                    onViewShopClicked = { businessId, selectedProductId, selectedServiceId ->
                        currentScreen = Screen.ShopProducts(
                            businessId = businessId,
                            selectedProductId = selectedProductId,
                            selectedServiceId = selectedServiceId
                        )
                    },
                    onOpenOwnerDashboard = {
                        currentScreen = Screen.OwnerPortal()
                    },
                    onOpenCustomerPortal = {
                        currentScreen = Screen.CustomerPortal()
                    },
                    onOpenAdminDashboard = {
                        currentScreen = Screen.AdminDashboard
                    },
                    onAuthRequested = {
                        currentScreen = Screen.Login()
                    },
                    onOpenJobsRequested = {
                        currentScreen = Screen.LocalJobs
                    },
                    onOpenPromotionsRequested = {
                        currentScreen = Screen.Promotions
                    },
                    onOpenEarnRequested = {
                        if (repository.currentUser == null) {
                            currentScreen = Screen.Login(targetScreenAfterAuth = Screen.CustomerPortal(CustomerSection.Earn))
                        } else {
                            currentScreen = Screen.CustomerPortal(CustomerSection.Earn)
                        }
                    },
                    onRegisterBusinessRequested = {
                        if (repository.currentUser == null) {
                            currentScreen = Screen.Login(targetScreenAfterAuth = Screen.BusinessRegistration)
                        } else {
                            currentScreen = Screen.BusinessRegistration
                        }
                    }
                )
            }

            is Screen.LocalJobs -> {
                LocalJobsScreen(
                    repository = repository,
                    onBackClicked = { currentScreen = Screen.HomeMap },
                    onJobSelected = { jobId ->
                        currentScreen = Screen.JobDetails(jobId)
                    }
                )
            }

            is Screen.JobDetails -> {
                JobDetailsScreen(
                    jobId = screen.jobId,
                    repository = repository,
                    onBackClicked = { currentScreen = Screen.LocalJobs },
                    onAuthRequested = { currentScreen = Screen.Login() },
                    onApplicationSubmitted = {
                        currentScreen = Screen.CustomerPortal(CustomerSection.Jobs)
                    }
                )
            }

            is Screen.Promotions -> {
                PromotionsScreen(
                    repository = repository,
                    onBackClicked = { currentScreen = Screen.HomeMap },
                    onPromotionSelected = { promoId ->
                        currentScreen = Screen.PromotionDetails(promoId)
                    },
                    onViewShopClicked = { businessId, productId, serviceId ->
                        currentScreen = Screen.ShopProducts(
                            businessId = businessId,
                            selectedProductId = productId,
                            selectedServiceId = serviceId
                        )
                    }
                )
            }

            is Screen.PromotionDetails -> {
                PromotionDetailsScreen(
                    promotionId = screen.promotionId,
                    repository = repository,
                    locationNavigator = locationNavigator,
                    onBackClicked = { currentScreen = Screen.Promotions },
                    onViewShopClicked = { businessId, productId, serviceId ->
                        currentScreen = Screen.ShopProducts(
                            businessId = businessId,
                            selectedProductId = productId,
                            selectedServiceId = serviceId
                        )
                    },
                    onAuthRequested = { currentScreen = Screen.Login() }
                )
            }

            is Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        if (screen.targetScreenAfterAuth != null) {
                            currentScreen = screen.targetScreenAfterAuth
                        } else {
                            routeAfterAuth()
                        }
                    },
                    onRegisterClicked = {
                        currentScreen = Screen.Register(targetScreenAfterAuth = screen.targetScreenAfterAuth)
                    },
                    onBackClicked = {
                        currentScreen = Screen.HomeMap
                    },
                    onLoginSubmit = { email, password ->
                        repository.login(mapOf("email" to email, "password" to password))
                        Unit
                    }
                )
            }

            is Screen.Register -> {
                RegisterScreen(
                    onCustomerRegistered = {
                        if (screen.targetScreenAfterAuth != null) {
                            currentScreen = screen.targetScreenAfterAuth
                        } else {
                            currentScreen = Screen.CustomerPortal()
                        }
                    },
                    onBusinessOwnerRegistered = {
                        currentScreen = Screen.BusinessRegistration
                    },
                    onLoginClicked = {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen.targetScreenAfterAuth)
                    },
                    onBackClicked = {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen.targetScreenAfterAuth)
                    },
                    onRegisterSubmit = { email, password, fullName, phone, role ->
                        repository.register(
                            mapOf(
                                "email" to email,
                                "password" to password,
                                "fullName" to fullName,
                                "phoneNumber" to phone,
                                "role" to role
                            )
                        )
                        Unit
                    }
                )
            }

            is Screen.BusinessRegistration -> {
                BusinessRegistrationScreen(
                    repository = repository,
                    onSubmittedSuccess = {
                        currentScreen = Screen.OwnerPortal()
                    },
                    onBackClicked = {
                        currentScreen = Screen.Register()
                    }
                )
            }

            is Screen.ShopProducts -> {
                ShopProductsScreen(
                    businessId = screen.businessId,
                    selectedProductId = screen.selectedProductId,
                    selectedServiceId = screen.selectedServiceId,
                    repository = repository,
                    locationNavigator = locationNavigator,
                    onBackClicked = { currentScreen = Screen.HomeMap },
                    onProductSelected = { product ->
                        currentScreen = Screen.ProductDetails(product)
                    },
                    onMessageShopOwnerClicked = { bId ->
                        if (repository.currentUser == null) {
                            currentScreen = Screen.Login(
                                targetScreenAfterAuth = Screen.CustomerPortal(
                                    initialSection = CustomerSection.Messages,
                                    targetBusinessIdForMessage = bId
                                )
                            )
                        } else {
                            currentScreen = Screen.CustomerPortal(
                                initialSection = CustomerSection.Messages,
                                targetBusinessIdForMessage = bId
                            )
                        }
                    }
                )
            }

            is Screen.ProductDetails -> {
                ProductDetailsScreen(
                    product = screen.product,
                    locationNavigator = locationNavigator,
                    onBackClicked = { currentScreen = Screen.HomeMap },
                    onAddToCartClicked = { cartItem ->
                        cartItems = cartItems + cartItem
                        currentScreen = Screen.CustomerPortal(CustomerSection.Cart)
                    }
                )
            }

            is Screen.CartCheckout -> {
                CartAndCheckoutScreen(
                    cartItems = screen.items,
                    onBackClicked = { currentScreen = Screen.CustomerPortal(CustomerSection.Cart) },
                    onOrderPlaced = {
                        cartItems = emptyList()
                        currentScreen = Screen.CustomerPortal(CustomerSection.Orders)
                    }
                )
            }

            is Screen.OwnerPortal -> {
                OwnerPortalScreen(
                    repository = repository,
                    initialSection = screen.initialSection,
                    targetConversationId = screen.targetConversationId,
                    onNavigateToPublicMap = { currentScreen = Screen.HomeMap },
                    onLogoutClicked = {
                        repository.authToken = null
                        repository.currentUser = null
                        currentScreen = Screen.HomeMap
                    }
                )
            }

            is Screen.CustomerPortal -> {
                CustomerPortalScreen(
                    repository = repository,
                    locationNavigator = locationNavigator,
                    cartItems = cartItems,
                    initialSection = screen.initialSection,
                    targetConversationId = screen.targetConversationId,
                    targetBusinessIdForMessage = screen.targetBusinessIdForMessage,
                    onNavigateToPublicMap = { currentScreen = Screen.HomeMap },
                    onLogoutClicked = {
                        repository.authToken = null
                        repository.currentUser = null
                        currentScreen = Screen.HomeMap
                    },
                    onCartUpdated = { updatedCart ->
                        cartItems = updatedCart
                    }
                )
            }

            is Screen.AdminDashboard -> {
                AdminDashboardScreen(
                    repository = repository,
                    onBackClicked = { currentScreen = Screen.HomeMap }
                )
            }
        }
    }
}
}
