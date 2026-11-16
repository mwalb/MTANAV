package tz.mtanav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import tz.mtanav.model.BusinessProductDto
import tz.mtanav.model.CartItem
import tz.mtanav.network.MtanavRepository
import tz.mtanav.theme.MtanavTheme
import tz.mtanav.ui.map.LocationNavigator
import tz.mtanav.ui.screens.*

sealed class Screen {
    data object HomeMap : Screen()
    data class Login(val targetScreenAfterAuth: Screen? = null) : Screen()
    data class Register(val targetScreenAfterAuth: Screen? = null, val initialRole: String = "CUSTOMER") : Screen()
    data class BusinessRegistration(val initialBusinessType: String = "Shop / Products") : Screen()
    data object RegisterProduct : Screen()
    data object RegisterService : Screen()
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

    fun routeAfterAuth(target: Screen? = null) {
        val user = repository.currentUser
        val roles = user?.roles ?: emptyList()
        val isOwner = roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER")
        val isAdmin = roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN")

        if (target != null) {
            when (target) {
                is Screen.OwnerPortal -> {
                    if (isOwner || isAdmin) {
                        currentScreen = target
                    } else {
                        currentScreen = Screen.BusinessRegistration(initialBusinessType = "Shop / Products")
                    }
                }
                is Screen.CustomerPortal -> {
                    if (!isOwner || isAdmin) {
                        currentScreen = target
                    } else {
                        currentScreen = Screen.OwnerPortal()
                    }
                }
                is Screen.RegisterProduct -> {
                    if (isOwner || isAdmin) {
                        currentScreen = Screen.OwnerPortal(initialSection = OwnerSection.PostProductService)
                    } else {
                        currentScreen = Screen.BusinessRegistration(initialBusinessType = "Shop / Products")
                    }
                }
                is Screen.RegisterService -> {
                    if (isOwner || isAdmin) {
                        currentScreen = Screen.OwnerPortal(initialSection = OwnerSection.PostProductService)
                    } else {
                        currentScreen = Screen.BusinessRegistration(initialBusinessType = "Service Provider")
                    }
                }
                is Screen.BusinessRegistration -> {
                    currentScreen = target
                }
                else -> {
                    currentScreen = target
                }
            }
        } else {
            when {
                isAdmin -> currentScreen = Screen.AdminDashboard
                isOwner -> currentScreen = Screen.OwnerPortal()
                else -> currentScreen = Screen.CustomerPortal()
            }
        }
    }

    MtanavTheme {
        Box(modifier = Modifier.fillMaxSize()) {
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
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.OwnerPortal())
                            } else {
                                routeAfterAuth(Screen.OwnerPortal())
                            }
                        },
                        onOpenCustomerPortal = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.CustomerPortal())
                            } else {
                                routeAfterAuth(Screen.CustomerPortal())
                            }
                        },
                        onOpenAdminDashboard = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.AdminDashboard)
                            } else {
                                currentScreen = Screen.AdminDashboard
                            }
                        },
                        onAuthRequested = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login()
                            } else {
                                routeAfterAuth()
                            }
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
                                routeAfterAuth(Screen.CustomerPortal(CustomerSection.Earn))
                            }
                        },
                        onRegisterBusinessRequested = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.BusinessRegistration())
                            } else {
                                val roles = repository.currentUser?.roles ?: emptyList()
                                if (roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER")) {
                                    currentScreen = Screen.OwnerPortal(initialSection = OwnerSection.PostProductService)
                                } else {
                                    currentScreen = Screen.BusinessRegistration()
                                }
                            }
                        },
                        onRegisterProductRequested = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.RegisterProduct)
                            } else {
                                routeAfterAuth(Screen.RegisterProduct)
                            }
                        },
                        onRegisterServiceRequested = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.RegisterService)
                            } else {
                                routeAfterAuth(Screen.RegisterService)
                            }
                        }
                    )
                }

                is Screen.RegisterProduct -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = Screen.RegisterProduct)
                    } else {
                        val roles = user.roles
                        val isOwner = roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER") || roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN")
                        if (isOwner) {
                            currentScreen = Screen.OwnerPortal(initialSection = OwnerSection.PostProductService)
                        } else {
                            currentScreen = Screen.BusinessRegistration(initialBusinessType = "Shop / Products")
                        }
                    }
                }

                is Screen.RegisterService -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = Screen.RegisterService)
                    } else {
                        val roles = user.roles
                        val isOwner = roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER") || roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN")
                        if (isOwner) {
                            currentScreen = Screen.OwnerPortal(initialSection = OwnerSection.PostProductService)
                        } else {
                            currentScreen = Screen.BusinessRegistration(initialBusinessType = "Service Provider")
                        }
                    }
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
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.CustomerPortal(CustomerSection.Jobs))
                            } else {
                                routeAfterAuth(Screen.CustomerPortal(CustomerSection.Jobs))
                            }
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
                                routeAfterAuth(screen.targetScreenAfterAuth)
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
                        initialRole = screen.initialRole,
                        onCustomerRegistered = {
                            if (screen.targetScreenAfterAuth != null) {
                                routeAfterAuth(screen.targetScreenAfterAuth)
                            } else {
                                routeAfterAuth(Screen.CustomerPortal())
                            }
                        },
                        onBusinessOwnerRegistered = {
                            val user = repository.currentUser
                            if (user != null) {
                                val roles = user.roles.toMutableList()
                                if (!roles.contains("ROLE_BUSINESS_OWNER")) roles.add("ROLE_BUSINESS_OWNER")
                                repository.currentUser = user.copy(roles = roles)
                            }
                            currentScreen = Screen.OwnerPortal()
                        },
                        onLoginClicked = {
                            currentScreen = Screen.Login(targetScreenAfterAuth = screen.targetScreenAfterAuth)
                        },
                        onBackClicked = {
                            currentScreen = Screen.Login(targetScreenAfterAuth = screen.targetScreenAfterAuth)
                        },
                        onRegisterSubmit = { email, password, fullName, phone, role, shopName, areaStreet ->
                            repository.register(
                                mapOf(
                                    "email" to email,
                                    "password" to password,
                                    "fullName" to fullName,
                                    "phoneNumber" to phone,
                                    "role" to role
                                )
                            )
                            if (role == "OWNER" || role == "BUSINESS_OWNER" || shopName.isNotBlank()) {
                                repository.createBusiness(
                                    tz.mtanav.model.CreateBusinessRequest(
                                        name = if (shopName.isNotBlank()) shopName else "My Shop / Service Yard",
                                        address = if (areaStreet.isNotBlank()) areaStreet else "Kariakoo, Dar es Salaam",
                                        city = "Dar es Salaam",
                                        phone = phone,
                                        latitude = -6.8182,
                                        longitude = 39.2778
                                    )
                                )
                            }
                        }
                    )
                }

                is Screen.BusinessRegistration -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen)
                    } else {
                        BusinessRegistrationScreen(
                            repository = repository,
                            initialBusinessType = screen.initialBusinessType,
                            onSubmittedSuccess = {
                                val roles = user.roles.toMutableList()
                                if (!roles.contains("ROLE_BUSINESS_OWNER")) roles.add("ROLE_BUSINESS_OWNER")
                                repository.currentUser = user.copy(roles = roles)
                                currentScreen = Screen.OwnerPortal()
                            },
                            onBackClicked = {
                                currentScreen = Screen.HomeMap
                            }
                        )
                    }
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
                                routeAfterAuth(
                                    Screen.CustomerPortal(
                                        initialSection = CustomerSection.Messages,
                                        targetBusinessIdForMessage = bId
                                    )
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
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.CustomerPortal(CustomerSection.Cart))
                            } else {
                                routeAfterAuth(Screen.CustomerPortal(CustomerSection.Cart))
                            }
                        }
                    )
                }

                is Screen.CartCheckout -> {
                    CartAndCheckoutScreen(
                        cartItems = screen.items,
                        onBackClicked = {
                            if (repository.currentUser == null) {
                                currentScreen = Screen.Login(targetScreenAfterAuth = Screen.CustomerPortal(CustomerSection.Cart))
                            } else {
                                routeAfterAuth(Screen.CustomerPortal(CustomerSection.Cart))
                            }
                        },
                        onOrderPlaced = {
                            cartItems = emptyList()
                            routeAfterAuth(Screen.CustomerPortal(CustomerSection.Orders))
                        }
                    )
                }

                is Screen.OwnerPortal -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen)
                    } else {
                        val roles = user.roles
                        val isOwner = roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER") || roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN")
                        if (!isOwner) {
                            currentScreen = Screen.BusinessRegistration(initialBusinessType = "Shop / Products")
                        } else {
                            OwnerPortalScreen(
                                repository = repository,
                                initialSection = screen.initialSection,
                                targetConversationId = screen.targetConversationId,
                                onNavigateToPublicMap = { currentScreen = Screen.HomeMap },
                                onOpenPublicShop = { bId -> currentScreen = Screen.ShopProducts(bId) },
                                onLogoutClicked = {
                                    repository.authToken = null
                                    repository.currentUser = null
                                    currentScreen = Screen.HomeMap
                                }
                            )
                        }
                    }
                }

                is Screen.CustomerPortal -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen)
                    } else {
                        val roles = user.roles
                        val isOwnerOnly = (roles.contains("ROLE_BUSINESS_OWNER") || roles.contains("ROLE_OWNER")) &&
                            !roles.contains("ROLE_CUSTOMER") &&
                            !roles.contains("ROLE_SUPER_ADMIN") &&
                            !roles.contains("ROLE_ADMIN")
                        if (isOwnerOnly) {
                            currentScreen = Screen.OwnerPortal()
                        } else {
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
                    }
                }

                is Screen.AdminDashboard -> {
                    val user = repository.currentUser
                    if (user == null) {
                        currentScreen = Screen.Login(targetScreenAfterAuth = screen)
                    } else {
                        val roles = user.roles
                        val isAdmin = roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN")
                        if (!isAdmin) {
                            routeAfterAuth()
                        } else {
                            AdminDashboardScreen(
                                repository = repository,
                                onBackClicked = { currentScreen = Screen.HomeMap }
                            )
                        }
                    }
                }
            }
        }
    }
}
