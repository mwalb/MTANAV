package tz.mtanav.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import tz.mtanav.model.*
import kotlin.math.*

class MtanavRepository(private val baseUrl: String = "http://localhost:8080/api") {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    var authToken: String? = null
    var currentUser: UserDto? = null

    // In-memory mock storage for portal state when backend is offline
    private val mockProducts = mutableListOf<BusinessProductDto>()
    private val mockServices = mutableListOf<BusinessServiceDto>()
    private val mockOrders = mutableListOf<OrderDto>()
    private val mockBookings = mutableListOf<BookingDto>()
    private val mockJobs = mutableListOf<JobDto>()
    private val mockJobApplications = mutableListOf<JobApplicationDto>()
    private val mockPromotions = mutableListOf<PromotionDto>()
    private val mockSavedPromotions = mutableSetOf<Long>()

    init {
        // Seed initial mock orders & bookings
        mockOrders.add(
            OrderDto(
                id = 1001,
                customerId = 1,
                customerName = "Juma Salum",
                businessId = 1,
                businessName = "Demo Household Shop (Kariakoo)",
                orderStatus = "PENDING",
                orderType = "DELIVERY",
                totalAmount = 28000.0,
                deliveryAddress = "Msimbazi Street, House #4",
                notes = "Handle with care",
                createdAt = "2026-10-08 14:30"
            )
        )
        mockBookings.add(
            BookingDto(
                id = 2001,
                customerId = 1,
                customerName = "Juma Salum",
                businessId = 4,
                businessName = "Demo Auto Garage & Car Wash (Mikocheni)",
                serviceId = 9,
                serviceName = "Premium Car Wash & Detailing",
                bookingStatus = "UPCOMING",
                bookingDate = "2026-10-10",
                bookingTime = "10:00 AM",
                price = 25000.0,
                notes = "Toyota Prado TX",
                createdAt = "2026-10-08 11:15"
            )
        )

        // Seed initial Jobs
        mockJobs.addAll(
            listOf(
                JobDto(
                    id = 1,
                    title = "Delivery Rider & Courier",
                    category = "Delivery and logistics",
                    description = "Urgent need for express parcel delivery rider with valid motorcycle license and smartphone for Kariakoo route.",
                    location = "Kariakoo, Dar es Salaam",
                    budgetAmount = 350000.0,
                    currency = "TZS",
                    status = "OPEN",
                    publicationStatus = "PUBLISHED",
                    posterId = 2,
                    businessId = 1,
                    businessName = "Demo Household Shop (Kariakoo)",
                    employmentType = "FULL_TIME",
                    payType = "MONTHLY",
                    payAmountMin = 300000.0,
                    payAmountMax = 450000.0,
                    payFrequency = "MONTHLY",
                    applicationDeadline = "2026-11-10",
                    qualifications = "Valid riding license, reliable motorcycle, good knowledge of Dar es Salaam roads.",
                    responsibilities = "Pick up items from shop, deliver safely to clients, update delivery statuses on app.",
                    contactInstructions = "Apply via MTANAV app or call shop manager directly.",
                    createdAt = "2026-10-09"
                ),
                JobDto(
                    id = 2,
                    title = "Shop Sales Assistant",
                    category = "Retail and shop assistance",
                    description = "Customer service, stock organization and POS checkout handling for household electronics store.",
                    location = "Mlimani City, Dar es Salaam",
                    budgetAmount = 400000.0,
                    currency = "TZS",
                    status = "OPEN",
                    publicationStatus = "PUBLISHED",
                    posterId = 2,
                    businessId = 2,
                    businessName = "Demo Electronics Store (Mlimani)",
                    employmentType = "FULL_TIME",
                    payType = "MONTHLY",
                    payAmountMin = 350000.0,
                    payAmountMax = 450000.0,
                    payFrequency = "MONTHLY",
                    applicationDeadline = "2026-10-30",
                    qualifications = "Secondary education certificate, basic math and spoken Swahili & English.",
                    responsibilities = "Assist incoming customers, maintain clean product displays, record daily sales.",
                    contactInstructions = "Submit application with contact details.",
                    createdAt = "2026-10-08"
                ),
                JobDto(
                    id = 3,
                    title = "Smartphone & Tablet Repair Technician",
                    category = "Repairs and technical work",
                    description = "Skilled technician for screen replacements, battery swaps, and micro-soldering.",
                    location = "Posta, Dar es Salaam",
                    budgetAmount = 600000.0,
                    currency = "TZS",
                    status = "OPEN",
                    publicationStatus = "PUBLISHED",
                    posterId = 3,
                    businessId = 5,
                    businessName = "Demo Printing & Tech Hub (Posta)",
                    employmentType = "CONTRACT",
                    payType = "MONTHLY",
                    payAmountMin = 500000.0,
                    payAmountMax = 750000.0,
                    payFrequency = "MONTHLY",
                    applicationDeadline = "2026-10-25",
                    qualifications = "At least 2 years hands-on phone repair experience.",
                    responsibilities = "Diagnose hardware faults, perform screen & chip repairs, test repaired devices.",
                    contactInstructions = "Apply through MTANAV platform with portfolio or references.",
                    createdAt = "2026-10-05"
                ),
                JobDto(
                    id = 4,
                    title = "Casual Loading & Unloading Assistant",
                    category = "Temporary and casual work",
                    description = "Day worker needed for offloading cargo containers at Kariakoo store.",
                    location = "Kariakoo, Dar es Salaam",
                    budgetAmount = 25000.0,
                    currency = "TZS",
                    status = "OPEN",
                    publicationStatus = "PUBLISHED",
                    posterId = 2,
                    businessId = 1,
                    businessName = "Demo Household Shop (Kariakoo)",
                    employmentType = "CASUAL",
                    payType = "DAILY",
                    payAmountMin = 20000.0,
                    payAmountMax = 30000.0,
                    payFrequency = "DAILY",
                    applicationDeadline = "2026-10-17",
                    qualifications = "Physical fitness and reliability.",
                    responsibilities = "Offload boxes carefully, arrange stock in warehouse.",
                    contactInstructions = "Report to shop in Kariakoo at 8:00 AM.",
                    createdAt = "2026-10-10"
                )
            )
        )

        // Seed initial Promotions
        mockPromotions.addAll(
            listOf(
                PromotionDto(
                    id = 1,
                    businessId = 1,
                    businessName = "Demo Household Shop (Kariakoo)",
                    businessLatitude = -6.8182,
                    businessLongitude = 39.2778,
                    productId = 1,
                    productName = "Stainless Steel Spoon Bundle",
                    title = "Mega Kitchen Sale — Stainless Steel Spoon Bundle",
                    description = "Get 20% off high-grade stainless steel dining spoon sets! Perfect for home and restaurant use.",
                    category = "Household",
                    originalPrice = 15000.0,
                    promotionalPrice = 12000.0,
                    discountPercentage = 20,
                    termsAndConditions = "Offer valid while stocks last. Maximum 5 bundles per customer.",
                    offerType = "DISCOUNT",
                    imageUrl = "https://picsum.photos/seed/promo1/600/400",
                    startDate = "2026-10-08",
                    expiryDate = "2026-10-24",
                    status = "ACTIVE",
                    isSponsored = true,
                    isSaved = false
                ),
                PromotionDto(
                    id = 2,
                    businessId = 2,
                    businessName = "Demo Electronics Store (Mlimani)",
                    businessLatitude = -6.7725,
                    businessLongitude = 39.2215,
                    productId = 3,
                    productName = "Type-C Fast Charger",
                    title = "Fast Charger Flash Sale — 30% Off",
                    description = "Top quality Type-C & iPhone fast chargers at discounted prices this week only at Mlimani Store.",
                    category = "Electronics",
                    originalPrice = 25000.0,
                    promotionalPrice = 17500.0,
                    discountPercentage = 30,
                    termsAndConditions = "Limited time offer. Show promotion voucher at checkout.",
                    offerType = "FLASH_SALE",
                    imageUrl = "https://picsum.photos/seed/promo2/600/400",
                    startDate = "2026-10-09",
                    expiryDate = "2026-10-17",
                    status = "ACTIVE",
                    isSponsored = true,
                    isSaved = false
                ),
                PromotionDto(
                    id = 3,
                    businessId = 4,
                    businessName = "Demo Auto Garage & Car Wash (Mikocheni)",
                    businessLatitude = -6.7580,
                    businessLongitude = 39.2450,
                    serviceId = 9,
                    serviceName = "Premium Car Wash & Detailing",
                    title = "Weekend Premium Car Wash & Wax — 15% OFF",
                    description = "Complete exterior hand wash, vacuuming and tire shine treatment at Mikocheni Auto Yard.",
                    category = "Automotive",
                    originalPrice = 25000.0,
                    promotionalPrice = 21250.0,
                    discountPercentage = 15,
                    termsAndConditions = "Valid on Friday, Saturday, and Sunday bookings.",
                    offerType = "SPECIAL_OFFER",
                    imageUrl = "https://picsum.photos/seed/promo3/600/400",
                    startDate = "2026-10-07",
                    expiryDate = "2026-10-28",
                    status = "ACTIVE",
                    isSponsored = false,
                    isSaved = false
                )
            )
        )
    }

    suspend fun login(loginRequest: Map<String, String>): AuthResponse = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<AuthResponse> = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }.body()
            val auth = response.data ?: throw Exception("Login failed")
            authToken = auth.token
            currentUser = auth.user
            auth
        } catch (e: Exception) {
            val role = if (loginRequest["email"]?.contains("customer", ignoreCase = true) == true) {
                "ROLE_CUSTOMER"
            } else if (loginRequest["email"]?.contains("admin", ignoreCase = true) == true) {
                "ROLE_ADMIN"
            } else {
                "ROLE_BUSINESS_OWNER"
            }

            val mockUser = UserDto(
                id = 2,
                email = loginRequest["email"] ?: "owner@mtanav.tz",
                fullName = loginRequest["email"]?.substringBefore("@")?.replace(".", " ")?.capitalizeWords() ?: "MTANAV Business Owner",
                phoneNumber = "+255700000000",
                roles = listOf(role)
            )
            val auth = AuthResponse(token = "mock-jwt-token", user = mockUser)
            authToken = auth.token
            currentUser = auth.user
            auth
        }
    }

    suspend fun register(registerRequest: Map<String, String>): AuthResponse = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<AuthResponse> = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(registerRequest)
            }.body()
            val auth = response.data ?: throw Exception("Registration failed")
            authToken = auth.token
            currentUser = auth.user
            auth
        } catch (e: Exception) {
            val requestedRole = if (registerRequest["role"]?.uppercase() == "OWNER" || registerRequest["role"]?.uppercase() == "BUSINESS_OWNER") {
                "ROLE_BUSINESS_OWNER"
            } else {
                "ROLE_CUSTOMER"
            }
            val mockUser = UserDto(
                id = 99,
                email = registerRequest["email"] ?: "newuser@mtanav.tz",
                fullName = registerRequest["fullName"] ?: "New User",
                phoneNumber = registerRequest["phoneNumber"],
                roles = listOf(requestedRole)
            )
            val auth = AuthResponse(token = "mock-jwt-token", user = mockUser)
            authToken = auth.token
            currentUser = auth.user
            auth
        }
    }

    suspend fun getMapBusinesses(
        productId: Long? = null,
        serviceId: Long? = null,
        query: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        distanceKm: Double? = null,
        userLat: Double? = null,
        userLng: Double? = null,
        availableNow: Boolean? = null,
        sort: SortOption? = null
    ): List<BusinessMapDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<BusinessMapDto>> = client.get("$baseUrl/businesses/map") {
                productId?.let { parameter("productId", it) }
                serviceId?.let { parameter("serviceId", it) }
                query?.let { parameter("query", it) }
                minPrice?.let { parameter("minPrice", it) }
                maxPrice?.let { parameter("maxPrice", it) }
                distanceKm?.let { parameter("maxDistanceKm", it) }
                userLat?.let { parameter("userLat", it) }
                userLng?.let { parameter("userLng", it) }
                availableNow?.let { parameter("availableNow", it) }
                sort?.let { parameter("sort", it.name) }
            }.body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            getFallbackMapBusinesses(
                productId, serviceId, query, minPrice, maxPrice, distanceKm, userLat, userLng, availableNow, sort
            )
        }
    }

    suspend fun getSearchSuggestions(query: String): List<SearchSuggestionDto> = withContext(Dispatchers.Default) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext emptyList()
        try {
            val response: ApiResponseWrapper<List<SearchSuggestionDto>> = client.get("$baseUrl/search/suggestions") {
                parameter("q", trimmed)
            }.body()
            val list = response.data ?: emptyList()
            if (list.isNotEmpty()) list else getFallbackSearchSuggestions(trimmed)
        } catch (e: Exception) {
            getFallbackSearchSuggestions(trimmed)
        }
    }

    private fun getFallbackSearchSuggestions(query: String): List<SearchSuggestionDto> {
        val q = query.trim().lowercase()
        val list = mutableListOf<SearchSuggestionDto>()

        // Products
        val products = listOf(
            SearchSuggestionDto(1, "Charger", "PRODUCT", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500", 18000.0, 2, "Demo Electronics Store (Mlimani)", "IN_STOCK", 4),
            SearchSuggestionDto(2, "Phone Charger", "PRODUCT", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500", 22000.0, 2, "Demo Electronics Store (Mlimani)", "IN_STOCK", 4),
            SearchSuggestionDto(3, "Type-C Fast Charger", "PRODUCT", "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500", 25000.0, 5, "Demo Printing & Tech Hub (Posta)", "IN_STOCK", 4),
            SearchSuggestionDto(4, "Smartphone Pro 256GB", "PRODUCT", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", 1500000.0, 2, "Demo Electronics Store (Mlimani)", "IN_STOCK", 3),
            SearchSuggestionDto(5, "Kyela Super Rice 5kg", "PRODUCT", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500", 18500.0, 1, "Demo Household Shop (Kariakoo)", "IN_STOCK", 2),
            SearchSuggestionDto(6, "Stainless Steel Spoon Set", "PRODUCT", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500", 15000.0, 1, "Demo Household Shop (Kariakoo)", "IN_STOCK", 1),
            SearchSuggestionDto(7, "Genuine Leather Shoes", "PRODUCT", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500", 45000.0, 3, "Demo Kitchen & Hardware (Sinza)", "IN_STOCK", 5)
        )

        for (p in products) {
            if (list.size >= 8) break
            if (p.name.lowercase().contains(q) || p.type.lowercase().contains(q) || (p.businessName?.lowercase()?.contains(q) == true)) {
                list.add(p)
            }
        }

        // Services
        val services = listOf(
            SearchSuggestionDto(10, "Phone Charging Service", "SERVICE", "https://images.unsplash.com/photo-1597740985671-2a8a3b80502e?w=500", 2000.0, 5, "Demo Printing & Tech Hub (Posta)", "AVAILABLE", 7),
            SearchSuggestionDto(11, "Phone & Screen Repair", "SERVICE", "https://images.unsplash.com/photo-1597740985671-2a8a3b80502e?w=500", 35000.0, 5, "Demo Printing & Tech Hub (Posta)", "AVAILABLE", 7),
            SearchSuggestionDto(12, "Auto Garage & Engine Repair", "SERVICE", "https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?w=500", 80000.0, 4, "Demo Auto Garage & Car Wash", "AVAILABLE", 2),
            SearchSuggestionDto(13, "Premium Car Wash & Polish", "SERVICE", "https://images.unsplash.com/photo-1520340356584-f9917d1beb6d?w=500", 25000.0, 4, "Demo Auto Garage & Car Wash", "AVAILABLE", 9),
            SearchSuggestionDto(14, "Beauty Salon & Hair Styling", "SERVICE", "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=500", 25000.0, 7, "Demo Beauty Salon & Spa", "AVAILABLE", 4)
        )

        for (s in services) {
            if (list.size >= 8) break
            if (s.name.lowercase().contains(q) || s.type.lowercase().contains(q) || (s.businessName?.lowercase()?.contains(q) == true)) {
                list.add(s)
            }
        }

        // Businesses
        val businesses = listOf(
            SearchSuggestionDto(20, "Charger Shop (Posta Tech)", "BUSINESS", "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=500", null, 5, "Samora Tower, Posta, Dar es Salaam", "OPEN", 5),
            SearchSuggestionDto(21, "Demo Electronics Store (Mlimani)", "BUSINESS", "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=500", null, 2, "Mlimani City Mall, Dar es Salaam", "OPEN", 2),
            SearchSuggestionDto(22, "Demo Household Shop (Kariakoo)", "BUSINESS", "https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=500", null, 1, "Swahili Street, Kariakoo, Dar es Salaam", "OPEN", 1),
            SearchSuggestionDto(23, "Demo Auto Garage & Car Wash", "BUSINESS", "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=500", null, 4, "Mikocheni B, Dar es Salaam", "OPEN", 4)
        )

        for (b in businesses) {
            if (list.size >= 8) break
            if (b.name.lowercase().contains(q) || b.type.lowercase().contains(q) || (b.businessName?.lowercase()?.contains(q) == true)) {
                list.add(b)
            }
        }

        return list
    }

    suspend fun getQuickProducts(): List<QuickProductDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<QuickProductDto>> = client.get("$baseUrl/products/quick").body()
            val list = response.data ?: emptyList()
            if (list.size == 5) list else getFallbackQuickProducts()
        } catch (e: Exception) {
            getFallbackQuickProducts()
        }
    }

    private fun getFallbackQuickProducts(): List<QuickProductDto> = listOf(
        QuickProductDto(1, "Spoon", "🥄", "Stainless Steel Kitchen Spoons"),
        QuickProductDto(2, "Rice", "🍚", "Super Grade Kyela Rice"),
        QuickProductDto(3, "Phone", "📱", "Smartphones & Mobile Devices"),
        QuickProductDto(4, "Charger", "🔌", "Fast USB & Type-C Chargers"),
        QuickProductDto(5, "Shoes", "👟", "Footwear & Leather Shoes")
    )

    suspend fun getQuickServices(): List<CategoryDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<CategoryDto>> = client.get("$baseUrl/services/quick").body()
            val list = response.data ?: emptyList()
            if (list.size == 5) list else getFallbackQuickServices()
        } catch (e: Exception) {
            getFallbackQuickServices()
        }
    }

    private fun getFallbackQuickServices(): List<CategoryDto> = listOf(
        CategoryDto(1, "Banking", "🏦", "Commercial Banking & Financial Services"),
        CategoryDto(2, "Garage", "🔧", "Vehicle Repair & Engine Services"),
        CategoryDto(9, "Car Wash", "🚗", "Auto Wash & Detailing"),
        CategoryDto(4, "Salon", "💇", "Hair Styling & Beauty Care"),
        CategoryDto(7, "Phone Repair", "📱", "Screen & Battery Repairs")
    )

    suspend fun getAllProducts(): List<ProductDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<ProductDto>> = client.get("$baseUrl/products").body()
            response.data ?: getFallbackAllProducts()
        } catch (e: Exception) {
            getFallbackAllProducts()
        }
    }

    private fun getFallbackAllProducts(): List<ProductDto> = listOf(
        ProductDto(3, "Phones", true, "📱", "Smartphones & Mobile Devices", 3),
        ProductDto(5, "Shoes", true, "👟", "Leather & Casual Footwear", 4),
        ProductDto(2, "Rice", true, "🍚", "Grade 1 Aromatic Rice", 2),
        ProductDto(4, "Chargers", true, "🔌", "Fast USB & Type-C Chargers", 3),
        ProductDto(6, "Clothes", false, "👕", "Shirts, Trousers & Apparel", 4),
        ProductDto(7, "Laptops", false, "💻", "Notebooks & Computing Devices", 3),
        ProductDto(8, "Furniture", false, "🪑", "Office Chairs & Home Furniture", 5),
        ProductDto(1, "Spoon", false, "🥄", "Stainless Steel Kitchen Spoons", 1)
    )

    suspend fun getAllServices(): List<CategoryDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<CategoryDto>> = client.get("$baseUrl/categories/services").body()
            response.data ?: getFallbackServices()
        } catch (e: Exception) {
            getFallbackServices()
        }
    }

    suspend fun getServices(): List<CategoryDto> = getAllServices()

    private fun getFallbackServices(): List<CategoryDto> = listOf(
        CategoryDto(2, "Garage", "🔧", "Vehicle Maintenance & Repair"),
        CategoryDto(9, "Car Wash", "🚗", "Car Washing & Auto Detailing"),
        CategoryDto(4, "Salon", "💇", "Hair Styling & Beauty Treatment"),
        CategoryDto(7, "Phone Repair", "📱", "Smartphone Hardware & Screen Repair"),
        CategoryDto(1, "Banking", "🏦", "Commercial Banking & Financial Services"),
        CategoryDto(5, "Medical", "🏥", "Clinics, Pharmacies & Outpatient Care"),
        CategoryDto(6, "Printing", "🖨", "Document Printing, Scanning & Binding"),
        CategoryDto(8, "Barber", "✂️", "Haircuts, Shaving & Men Grooming"),
        CategoryDto(10, "Tailoring", "👗", "Custom Tailoring & Apparel Alterations"),
        CategoryDto(3, "Washing", "🧺", "Laundry, Dry Cleaning & Fabric Care")
    )

    suspend fun getBusinessProducts(businessId: Long): List<BusinessProductDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<BusinessProductDto>> = client.get("$baseUrl/businesses/$businessId/products").body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            val userAdded = mockProducts.filter { it.businessId == businessId }
            val defaultList = when (businessId) {
                1L -> listOf(
                    BusinessProductDto(
                        id = 1,
                        businessId = 1,
                        businessName = "Demo Household Shop (Kariakoo)",
                        productId = 1,
                        productName = "Stainless Steel Spoon",
                        productDescription = "High-grade 18/10 stainless steel dining spoon set. Rust resistant and dishwasher safe.",
                        sku = "SPN-001",
                        price = 3000.0,
                        salePrice = 2800.0,
                        imageUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500",
                        imageUrls = listOf("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500"),
                        variants = listOf(
                            ProductVariantDto(1, "Single Spoon", 1, "pcs", 3000.0, 500),
                            ProductVariantDto(2, "Small Bundle (6 Pieces)", 6, "pcs", 15000.0, 80),
                            ProductVariantDto(3, "Family Bundle (12 Pieces)", 12, "pcs", 28000.0, 40),
                            ProductVariantDto(4, "Wholesale Carton (50 Pieces)", 50, "pcs", 100000.0, 10)
                        )
                    ),
                    BusinessProductDto(
                        id = 2,
                        businessId = 1,
                        businessName = "Demo Household Shop (Kariakoo)",
                        productId = 2,
                        productName = "Kyela Super Rice 5kg",
                        productDescription = "Aromatic grade 1 Kyela rice direct from Mbeya farms.",
                        price = 18500.0,
                        salePrice = 17000.0,
                        imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500",
                        imageUrls = listOf("https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500"),
                        variants = listOf(
                            ProductVariantDto(21, "1kg Bag", 1, "kg", 3700.0, 200),
                            ProductVariantDto(22, "5kg Bag", 5, "kg", 18500.0, 100),
                            ProductVariantDto(23, "25kg Sack", 25, "kg", 88000.0, 30)
                        )
                    )
                )
                2L, 5L -> listOf(
                    BusinessProductDto(
                        id = 3,
                        businessId = businessId,
                        businessName = if (businessId == 2L) "Demo Electronics Store (Mlimani)" else "Demo Printing & Tech Hub (Posta)",
                        productId = 4,
                        productName = "Fast Charger",
                        productDescription = "High speed wall chargers & braided cables for smartphones and tablets.",
                        sku = "CHG-100",
                        price = 18000.0,
                        salePrice = 15000.0,
                        imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500",
                        imageUrls = listOf("https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500"),
                        variants = listOf(
                            ProductVariantDto(10, "Fast Charge 2A", 1, "pcs", 15000.0, 100),
                            ProductVariantDto(11, "Fast Charge 3A", 1, "pcs", 20000.0, 80),
                            ProductVariantDto(12, "Fast Charge 5A", 1, "pcs", 25000.0, 50),
                            ProductVariantDto(13, "Type-C Fast Charger", 1, "pcs", 18000.0, 120),
                            ProductVariantDto(14, "iPhone Fast Charger", 1, "pcs", 22000.0, 90)
                        )
                    ),
                    BusinessProductDto(
                        id = 4,
                        businessId = businessId,
                        businessName = if (businessId == 2L) "Demo Electronics Store (Mlimani)" else "Demo Printing & Tech Hub (Posta)",
                        productId = 3,
                        productName = "Smartphone Pro",
                        productDescription = "6.7-inch Super Retina display, 256GB storage, 5G enabled, triple lens camera.",
                        price = 1500000.0,
                        imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500",
                        imageUrls = listOf("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500"),
                        variants = listOf(
                            ProductVariantDto(31, "128GB Edition", 1, "pcs", 1350000.0, 15),
                            ProductVariantDto(32, "256GB Edition", 1, "pcs", 1500000.0, 25),
                            ProductVariantDto(33, "512GB Pro Max", 1, "pcs", 1850000.0, 10)
                        )
                    )
                )
                3L, 6L -> listOf(
                    BusinessProductDto(
                        id = 5,
                        businessId = businessId,
                        businessName = if (businessId == 3L) "Demo Kitchen & Hardware (Sinza)" else "Demo Footwear & Fashion (Kariakoo)",
                        productId = 5,
                        productName = "Genuine Leather Shoes",
                        productDescription = "Handcrafted genuine leather dress shoes for formal and casual occasions.",
                        sku = "SHO-550",
                        price = 45000.0,
                        salePrice = 38000.0,
                        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                        imageUrls = listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500"),
                        variants = listOf(
                            ProductVariantDto(41, "Size 40 (Black)", 1, "pair", 38000.0, 20),
                            ProductVariantDto(42, "Size 41 (Black)", 1, "pair", 38000.0, 25),
                            ProductVariantDto(43, "Size 42 (Brown)", 1, "pair", 42000.0, 18),
                            ProductVariantDto(44, "Size 43 (Brown)", 1, "pair", 42000.0, 15)
                        )
                    )
                )
                else -> emptyList()
            }
            defaultList + userAdded
        }
    }

    suspend fun getBusinessServices(businessId: Long): List<BusinessServiceDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<BusinessServiceDto>> = client.get("$baseUrl/businesses/$businessId/services").body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            val userAdded = mockServices.filter { it.businessId == businessId }
            val defaultList = listOf(
                BusinessServiceDto(
                    id = 1,
                    businessId = businessId,
                    businessName = "Demo Business",
                    serviceId = 9,
                    serviceName = "Car Wash & Polish",
                    serviceIcon = "🚗",
                    serviceDescription = "Full exterior wash, interior vacuuming and body wax polish.",
                    price = 25000.0,
                    durationMinutes = 45,
                    available = true
                )
            )
            defaultList + userAdded
        }
    }

    suspend fun createProduct(
        businessId: Long,
        businessName: String,
        name: String,
        description: String,
        price: Double,
        stock: Int,
        variants: List<ProductVariantDto>
    ): BusinessProductDto = withContext(Dispatchers.Default) {
        val newProduct = BusinessProductDto(
            id = (mockProducts.size + 100).toLong(),
            businessId = businessId,
            businessName = businessName,
            productId = 99,
            productName = name,
            productDescription = description,
            price = price,
            availableQuantity = stock,
            variants = variants
        )
        mockProducts.add(newProduct)
        newProduct
    }

    suspend fun createService(
        businessId: Long,
        businessName: String,
        name: String,
        description: String,
        price: Double,
        durationMinutes: Int
    ): BusinessServiceDto = withContext(Dispatchers.Default) {
        val newService = BusinessServiceDto(
            id = (mockServices.size + 100).toLong(),
            businessId = businessId,
            businessName = businessName,
            serviceId = 99,
            serviceName = name,
            serviceDescription = description,
            price = price,
            durationMinutes = durationMinutes,
            available = true
        )
        mockServices.add(newService)
        newService
    }

    suspend fun getMyBusinesses(): List<BusinessDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<BusinessDto>> = client.get("$baseUrl/businesses/owner") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            getFallbackMyBusinesses()
        }
    }

    suspend fun createBusiness(request: CreateBusinessRequest): BusinessDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<BusinessDto> = client.post("$baseUrl/businesses") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(request)
            }.body()
            response.data ?: throw Exception("Failed to create business")
        } catch (e: Exception) {
            BusinessDto(
                id = 101,
                ownerId = currentUser?.id ?: 2,
                name = request.name,
                description = request.description,
                phone = request.phone,
                email = request.email,
                verificationStatus = "PENDING",
                supportsDelivery = request.supportsDelivery,
                supportsPickup = request.supportsPickup,
                latitude = request.latitude,
                longitude = request.longitude,
                address = request.address,
                city = request.city,
                region = request.region,
                buildingName = request.buildingName,
                floor = request.floor,
                shopNumber = request.shopNumber,
                street = request.street,
                landmark = request.landmark,
                rating = 0.0,
                reviewCount = 0,
                paidPhotoAllowance = 0,
                imageUrls = emptyList()
            )
        }
    }

    suspend fun getPhotoUsage(businessId: Long): PhotoUsageDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<PhotoUsageDto> = client.get("$baseUrl/businesses/$businessId/photo-usage").body()
            response.data ?: PhotoUsageDto(businessId, 5, 5, 0, 5, 10.0)
        } catch (e: Exception) {
            PhotoUsageDto(businessId, 5, 5, 0, 5, 10.0)
        }
    }

    suspend fun payForPhotos(businessId: Long, additionalPhotos: Int): PhotoUsageDto = withContext(Dispatchers.Default) {
        try {
            val req = PhotoPaymentRequest(additionalPhotos = additionalPhotos)
            val response: ApiResponseWrapper<PhotoUsageDto> = client.post("$baseUrl/businesses/$businessId/photo-payment") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(req)
            }.body()
            response.data ?: PhotoUsageDto(businessId, 5, 5, additionalPhotos, 5 + additionalPhotos, 10.0)
        } catch (e: Exception) {
            PhotoUsageDto(businessId, 5, 5, additionalPhotos, 5 + additionalPhotos, 10.0)
        }
    }

    suspend fun addBusinessPhoto(businessId: Long, imageUrl: String): BusinessDto = withContext(Dispatchers.Default) {
        try {
            val req = AddPhotoRequest(imageUrl = imageUrl)
            val response: ApiResponseWrapper<BusinessDto> = client.post("$baseUrl/businesses/$businessId/photos") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(req)
            }.body()
            response.data ?: throw Exception("Failed to upload photo")
        } catch (e: Exception) {
            getFallbackMyBusinesses().first()
        }
    }

    // Orders & Bookings
    suspend fun getOwnerOrders(businessId: Long): List<OrderDto> = withContext(Dispatchers.Default) {
        mockOrders.filter { it.businessId == businessId || businessId == 1L }
    }

    suspend fun getOwnerBookings(businessId: Long): List<BookingDto> = withContext(Dispatchers.Default) {
        mockBookings.filter { it.businessId == businessId || businessId == 4L }
    }

    suspend fun getCustomerOrders(): List<OrderDto> = withContext(Dispatchers.Default) {
        mockOrders
    }

    suspend fun getCustomerBookings(): List<BookingDto> = withContext(Dispatchers.Default) {
        mockBookings
    }

    suspend fun placeOrder(
        items: List<CartItem>,
        deliveryAddress: String,
        deliveryOption: String
    ): OrderDto = withContext(Dispatchers.Default) {
        val first = items.firstOrNull()
        val total = items.sumOf { it.unitPrice * it.quantity }
        val newOrder = OrderDto(
            id = (mockOrders.size + 1000).toLong(),
            customerId = currentUser?.id ?: 1,
            customerName = currentUser?.fullName ?: "Customer",
            businessId = first?.businessId ?: 1,
            businessName = first?.businessName ?: "Demo Business",
            orderStatus = "PENDING",
            orderType = deliveryOption,
            totalAmount = total,
            deliveryAddress = deliveryAddress,
            createdAt = "Just now"
        )
        mockOrders.add(0, newOrder)
        newOrder
    }

    suspend fun createBooking(
        businessId: Long,
        businessName: String,
        serviceId: Long,
        serviceName: String,
        bookingDate: String,
        bookingTime: String,
        price: Double
    ): BookingDto = withContext(Dispatchers.Default) {
        val newBooking = BookingDto(
            id = (mockBookings.size + 2000).toLong(),
            customerId = currentUser?.id ?: 1,
            customerName = currentUser?.fullName ?: "Customer",
            businessId = businessId,
            businessName = businessName,
            serviceId = serviceId,
            serviceName = serviceName,
            bookingStatus = "UPCOMING",
            bookingDate = bookingDate,
            bookingTime = bookingTime,
            price = price,
            createdAt = "Just now"
        )
        mockBookings.add(0, newBooking)
        newBooking
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String): OrderDto = withContext(Dispatchers.Default) {
        val index = mockOrders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            val updated = mockOrders[index].copy(orderStatus = newStatus)
            mockOrders[index] = updated
            updated
        } else {
            throw Exception("Order not found")
        }
    }

    suspend fun updateBookingStatus(bookingId: Long, newStatus: String): BookingDto = withContext(Dispatchers.Default) {
        val index = mockBookings.indexOfFirst { it.id == bookingId }
        if (index >= 0) {
            val updated = mockBookings[index].copy(bookingStatus = newStatus)
            mockBookings[index] = updated
            updated
        } else {
            throw Exception("Booking not found")
        }
    }

    // =====================================
    // LOCAL JOBS API METHODS
    // =====================================

    suspend fun getJobs(
        query: String? = null,
        category: String? = null,
        employmentType: String? = null,
        location: String? = null
    ): List<JobDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<JobDto>> = client.get("$baseUrl/jobs") {
                query?.let { parameter("query", it) }
                category?.let { parameter("category", it) }
                employmentType?.let { parameter("employmentType", it) }
                location?.let { parameter("location", it) }
            }.body()
            response.data ?: mockJobs
        } catch (e: Exception) {
            mockJobs.filter { j ->
                (query == null || j.title.contains(query, ignoreCase = true) || j.description.contains(query, ignoreCase = true)) &&
                (category == null || j.category.equals(category, ignoreCase = true)) &&
                (employmentType == null || j.employmentType.equals(employmentType, ignoreCase = true)) &&
                (location == null || j.location.contains(location, ignoreCase = true))
            }
        }
    }

    suspend fun getJobDetails(id: Long): JobDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobDto> = client.get("$baseUrl/jobs/$id").body()
            response.data ?: mockJobs.first { it.id == id }
        } catch (e: Exception) {
            mockJobs.firstOrNull { it.id == id } ?: throw Exception("Job not found")
        }
    }

    suspend fun createJob(req: CreateJobRequest): JobDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobDto> = client.post("$baseUrl/jobs") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(req)
            }.body()
            val created = response.data ?: throw Exception("Failed to post job")
            mockJobs.add(0, created)
            created
        } catch (e: Exception) {
            val newJob = JobDto(
                id = (mockJobs.size + 100).toLong(),
                title = req.title,
                category = req.category,
                description = req.description,
                location = req.location,
                budgetAmount = req.budgetAmount,
                posterId = currentUser?.id ?: 2,
                businessId = req.businessId,
                employmentType = req.employmentType,
                payType = req.payType,
                payAmountMin = req.payAmountMin,
                payAmountMax = req.payAmountMax,
                payFrequency = req.payFrequency,
                applicationDeadline = req.applicationDeadline,
                qualifications = req.qualifications,
                responsibilities = req.responsibilities,
                contactInstructions = req.contactInstructions,
                publicationStatus = req.publicationStatus,
                createdAt = "Just now"
            )
            mockJobs.add(0, newJob)
            newJob
        }
    }

    suspend fun updateJobStatus(jobId: Long, status: String): JobDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobDto> = client.patch("$baseUrl/jobs/$jobId/status") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf("status" to status))
            }.body()
            response.data ?: throw Exception("Failed to update job status")
        } catch (e: Exception) {
            val idx = mockJobs.indexOfFirst { it.id == jobId }
            if (idx >= 0) {
                val updated = mockJobs[idx].copy(publicationStatus = status)
                mockJobs[idx] = updated
                updated
            } else throw Exception("Job not found")
        }
    }

    suspend fun applyForJob(jobId: Long, proposedRate: Double, coverNote: String): JobApplicationDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobApplicationDto> = client.post("$baseUrl/jobs/$jobId/applications") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf("proposedRate" to proposedRate, "coverNote" to coverNote))
            }.body()
            val app = response.data ?: throw Exception("Failed to apply")
            mockJobApplications.add(0, app)
            app
        } catch (e: Exception) {
            val jobTitle = mockJobs.firstOrNull { it.id == jobId }?.title ?: "Job #$jobId"
            val newApp = JobApplicationDto(
                id = (mockJobApplications.size + 500).toLong(),
                jobId = jobId,
                jobTitle = jobTitle,
                providerId = currentUser?.id ?: 1,
                applicantName = currentUser?.fullName ?: "Applicant",
                applicantEmail = currentUser?.email ?: "applicant@mtanav.tz",
                applicantPhone = currentUser?.phoneNumber ?: "+255700000000",
                proposedRate = proposedRate,
                coverNote = coverNote,
                status = "SUBMITTED",
                createdAt = "Just now"
            )
            mockJobApplications.add(0, newApp)
            newApp
        }
    }

    suspend fun getMyJobApplications(): List<JobApplicationDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<JobApplicationDto>> = client.get("$baseUrl/me/job-applications") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockJobApplications
        } catch (e: Exception) {
            mockJobApplications
        }
    }

    suspend fun withdrawJobApplication(id: Long): JobApplicationDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobApplicationDto> = client.delete("$baseUrl/me/job-applications/$id") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: throw Exception("Failed to withdraw application")
        } catch (e: Exception) {
            val idx = mockJobApplications.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val updated = mockJobApplications[idx].copy(status = "WITHDRAWN")
                mockJobApplications[idx] = updated
                updated
            } else throw Exception("Application not found")
        }
    }

    suspend fun getOwnerJobs(): List<JobDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<JobDto>> = client.get("$baseUrl/jobs/owner") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockJobs
        } catch (e: Exception) {
            mockJobs
        }
    }

    suspend fun getJobApplicationsForJob(jobId: Long): List<JobApplicationDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<JobApplicationDto>> = client.get("$baseUrl/jobs/$jobId/applications") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockJobApplications.filter { it.jobId == jobId }
        } catch (e: Exception) {
            mockJobApplications.filter { it.jobId == jobId }
        }
    }

    suspend fun updateJobApplicationStatus(jobId: Long, applicationId: Long, status: String): JobApplicationDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<JobApplicationDto> = client.patch("$baseUrl/jobs/$jobId/applications/$applicationId/status") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf("status" to status))
            }.body()
            response.data ?: throw Exception("Failed to update application status")
        } catch (e: Exception) {
            val idx = mockJobApplications.indexOfFirst { it.id == applicationId }
            if (idx >= 0) {
                val updated = mockJobApplications[idx].copy(status = status)
                mockJobApplications[idx] = updated
                updated
            } else throw Exception("Application not found")
        }
    }

    // =====================================
    // PROMOTIONS API METHODS
    // =====================================

    suspend fun getPromotions(
        query: String? = null,
        category: String? = null,
        offerType: String? = null
    ): List<PromotionDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<PromotionDto>> = client.get("$baseUrl/promotions") {
                query?.let { parameter("query", it) }
                category?.let { parameter("category", it) }
                offerType?.let { parameter("offerType", it) }
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockPromotions
        } catch (e: Exception) {
            mockPromotions.filter { p ->
                (query == null || p.title.contains(query, ignoreCase = true) || p.description.contains(query, ignoreCase = true)) &&
                (category == null || p.category.equals(category, ignoreCase = true)) &&
                (offerType == null || p.offerType.equals(offerType, ignoreCase = true))
            }.map { p -> p.copy(isSaved = mockSavedPromotions.contains(p.id)) }
        }
    }

    suspend fun getPromotionDetails(id: Long): PromotionDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<PromotionDto> = client.get("$baseUrl/promotions/$id") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockPromotions.first { it.id == id }
        } catch (e: Exception) {
            mockPromotions.firstOrNull { it.id == id }?.copy(isSaved = mockSavedPromotions.contains(id))
                ?: throw Exception("Promotion not found")
        }
    }

    suspend fun createPromotion(req: CreatePromotionRequest): PromotionDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<PromotionDto> = client.post("$baseUrl/promotions") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(req)
            }.body()
            val created = response.data ?: throw Exception("Failed to create promotion")
            mockPromotions.add(0, created)
            created
        } catch (e: Exception) {
            val discount = if (req.originalPrice != null && req.promotionalPrice != null && req.originalPrice > req.promotionalPrice) {
                (((req.originalPrice - req.promotionalPrice) / req.originalPrice) * 100).toInt()
            } else 0

            val newPromo = PromotionDto(
                id = (mockPromotions.size + 100).toLong(),
                businessId = req.businessId,
                businessName = getFallbackMyBusinesses().firstOrNull { it.id == req.businessId }?.name ?: "My Business",
                productId = req.productId,
                serviceId = req.serviceId,
                title = req.title,
                description = req.description,
                category = req.category,
                originalPrice = req.originalPrice,
                promotionalPrice = req.promotionalPrice,
                discountPercentage = discount,
                termsAndConditions = req.termsAndConditions,
                offerType = req.offerType,
                imageUrl = req.imageUrl ?: "https://picsum.photos/seed/promo/600/400",
                startDate = req.startDate ?: "2026-10-10",
                expiryDate = req.expiryDate ?: "2026-10-31",
                status = req.status,
                isSponsored = req.isSponsored
            )
            mockPromotions.add(0, newPromo)
            newPromo
        }
    }

    suspend fun updatePromotionStatus(id: Long, status: String): PromotionDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<PromotionDto> = client.patch("$baseUrl/promotions/$id/status") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf("status" to status))
            }.body()
            response.data ?: throw Exception("Failed to update status")
        } catch (e: Exception) {
            val idx = mockPromotions.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val updated = mockPromotions[idx].copy(status = status)
                mockPromotions[idx] = updated
                updated
            } else throw Exception("Promotion not found")
        }
    }

    suspend fun getOwnerPromotions(): List<PromotionDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<PromotionDto>> = client.get("$baseUrl/owner/promotions") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockPromotions
        } catch (e: Exception) {
            mockPromotions
        }
    }

    suspend fun savePromotion(id: Long): Boolean = withContext(Dispatchers.Default) {
        try {
            client.post("$baseUrl/promotions/$id/save") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            mockSavedPromotions.add(id)
            true
        } catch (e: Exception) {
            mockSavedPromotions.add(id)
            true
        }
    }

    suspend fun unsavePromotion(id: Long): Boolean = withContext(Dispatchers.Default) {
        try {
            client.delete("$baseUrl/promotions/$id/save") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            mockSavedPromotions.remove(id)
            true
        } catch (e: Exception) {
            mockSavedPromotions.remove(id)
            true
        }
    }

    suspend fun getSavedPromotions(): List<PromotionDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<PromotionDto>> = client.get("$baseUrl/me/saved-promotions") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: mockPromotions.filter { mockSavedPromotions.contains(it.id) }.map { it.copy(isSaved = true) }
        } catch (e: Exception) {
            mockPromotions.filter { mockSavedPromotions.contains(it.id) }.map { it.copy(isSaved = true) }
        }
    }

    suspend fun getAdminBusinesses(status: String? = null): List<BusinessDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<BusinessDto>> = client.get("$baseUrl/admin/businesses") {
                status?.let { parameter("status", it) }
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            getFallbackAdminBusinesses()
        }
    }

    suspend fun updateVerificationStatus(businessId: Long, status: String): BusinessDto = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<BusinessDto> = client.put("$baseUrl/admin/businesses/$businessId/verification") {
                contentType(ContentType.Application.Json)
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf("status" to status))
            }.body()
            response.data ?: throw Exception("Failed to update status")
        } catch (e: Exception) {
            val b = getFallbackAdminBusinesses().first { it.id == businessId }
            b.copy(verificationStatus = status)
        }
    }

    suspend fun getAuditLogs(): List<AuditLogDto> = withContext(Dispatchers.Default) {
        try {
            val response: ApiResponseWrapper<List<AuditLogDto>> = client.get("$baseUrl/admin/audit-logs") {
                authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            response.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getFallbackMyBusinesses(): List<BusinessDto> = listOf(
        BusinessDto(
            id = 1,
            ownerId = 2,
            name = "Demo Household Shop (Kariakoo)",
            description = "Kitchenware and household items",
            phone = "+255711000001",
            email = "info@demohousehold.co.tz",
            verificationStatus = "VERIFIED",
            supportsDelivery = true,
            supportsPickup = true,
            latitude = -6.8182,
            longitude = 39.2778,
            address = "Swahili Street",
            city = "Dar es Salaam",
            region = "Ilala",
            buildingName = "Kariakoo Complex",
            shopNumber = "A-12",
            rating = 4.8,
            reviewCount = 42,
            paidPhotoAllowance = 0,
            imageUrls = listOf(
                "https://picsum.photos/seed/shop1_1/600/400",
                "https://picsum.photos/seed/shop1_2/600/400",
                "https://picsum.photos/seed/shop1_3/600/400",
                "https://picsum.photos/seed/shop1_4/600/400",
                "https://picsum.photos/seed/shop1_5/600/400"
            )
        )
    )

    private fun getFallbackAdminBusinesses(): List<BusinessDto> = listOf(
        BusinessDto(
            id = 1, ownerId = 2, name = "Demo Household Shop (Kariakoo)", description = "Kitchenware shop",
            phone = "+255711000001", email = "info@demohousehold.co.tz", verificationStatus = "VERIFIED",
            supportsDelivery = true, supportsPickup = true, latitude = -6.8182, longitude = 39.2778,
            address = "Swahili Street", city = "Dar es Salaam", region = "Ilala", buildingName = "Kariakoo Complex",
            floor = "1", shopNumber = "A-12", street = "Swahili St", landmark = "Msimbazi", logoUrl = null,
            rating = 4.8, reviewCount = 42, paidPhotoAllowance = 0, imageUrls = listOf("https://picsum.photos/seed/shop1/600/400")
        ),
        BusinessDto(
            id = 2, ownerId = 3, name = "Demo Electronics Store (Mlimani)", description = "Smartphones and accessories",
            phone = "+255711000002", email = "sales@demoelectronics.co.tz", verificationStatus = "VERIFIED",
            supportsDelivery = true, supportsPickup = true, latitude = -6.7725, longitude = 39.2215,
            address = "Mlimani Mall", city = "Dar es Salaam", region = "Ubungo", buildingName = "Mlimani Block B",
            floor = "Ground", shopNumber = "B-104", street = "Sam Nujoma Rd", landmark = "Opposite UDSM", logoUrl = null,
            rating = 4.6, reviewCount = 28, paidPhotoAllowance = 0, imageUrls = listOf("https://picsum.photos/seed/shop2/600/400")
        ),
        BusinessDto(
            id = 8, ownerId = 2, name = "New Pending Hardware Shop", description = "Pending verification shop",
            phone = "+255711000008", email = "newshop@mtanav.tz", verificationStatus = "PENDING",
            supportsDelivery = true, supportsPickup = true, latitude = -6.8000, longitude = 39.2500,
            address = "Shekilango Rd", city = "Dar es Salaam", region = "Kinondoni", buildingName = "Sinza Tower",
            floor = "1", shopNumber = "S-01", street = "Shekilango", landmark = "Mori Stop", logoUrl = null,
            rating = 0.0, reviewCount = 0, paidPhotoAllowance = 0, imageUrls = emptyList()
        )
    )

    private fun getFallbackMapBusinesses(
        productId: Long?,
        serviceId: Long?,
        query: String?,
        minPrice: Double?,
        maxPrice: Double?,
        distanceKm: Double?,
        userLat: Double?,
        userLng: Double?,
        availableNow: Boolean?,
        sort: SortOption?
    ): List<BusinessMapDto> {
        val allBusinesses = listOf(
            BusinessMapDto(1, "Demo Household Shop (Kariakoo)", -6.8182, 39.2778, "Swahili Street / Aggrey", "Dar es Salaam", "Kariakoo Commercial Complex", "A-12", 4.8, 42, null, "Spoon", 2800.0),
            BusinessMapDto(2, "Demo Electronics Store (Mlimani)", -6.7725, 39.2215, "Mlimani City Mall", "Dar es Salaam", "Mlimani Mall Block B", "B-104", 4.6, 28, null, "Phones", 2400000.0),
            BusinessMapDto(3, "Demo Kitchen & Hardware (Sinza)", -6.7845, 39.2280, "Sinza Mori", "Dar es Salaam", "Mori Plaza", "Shop 5", 4.5, 19, null, "Shoes", 35000.0),
            BusinessMapDto(4, "Demo Auto Garage & Car Wash (Mikocheni)", -6.7580, 39.2450, "Mikocheni B", "Dar es Salaam", "Mikocheni Auto Yard", "Yard 1", 4.7, 35, null, "Garage", 80000.0),
            BusinessMapDto(5, "Demo Printing & Tech Hub (Posta)", -6.8150, 39.2880, "Posta Mpya", "Dar es Salaam", "Samora Tower", "204", 4.4, 15, null, "Phone Repair", 35000.0),
            BusinessMapDto(6, "Demo Footwear & Fashion (Kariakoo)", -6.8190, 39.2760, "Msimbazi Street", "Dar es Salaam", "Kariakoo Plaza", "G-08", 4.8, 56, null, "Shoes", 45000.0),
            BusinessMapDto(7, "Demo Beauty Salon & Spa (Kinondoni)", -6.7910, 39.2620, "Kinondoni Studio", "Dar es Salaam", "Studio Arcade", "Shop 2", 4.9, 64, null, "Salon", 25000.0)
        )

        var filtered = when {
            productId == 3L || (query != null && query.contains("phone", ignoreCase = true) && !query.contains("repair", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 2L }.map { it.copy(matchingItemName = "Phones", startingPrice = 2400000.0) }
            }
            productId == 5L || (query != null && query.contains("shoe", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 3L || it.id == 6L }.map { it.copy(matchingItemName = "Shoes") }
            }
            productId == 2L || (query != null && query.contains("rice", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 1L }.map { it.copy(matchingItemName = "Rice", startingPrice = 3500.0) }
            }
            productId == 4L || (query != null && query.contains("charger", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 2L || it.id == 5L }.map { it.copy(matchingItemName = "Chargers", startingPrice = 25000.0) }
            }
            productId == 1L || (query != null && query.contains("spoon", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 1L || it.id == 3L }.map { it.copy(matchingItemName = "Spoon", startingPrice = 2800.0) }
            }
            serviceId == 2L || (query != null && query.contains("garage", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 4L }.map { it.copy(matchingItemName = "Garage", startingPrice = 80000.0) }
            }
            serviceId == 9L || (query != null && (query.contains("car wash", ignoreCase = true) || query.contains("wash", ignoreCase = true))) -> {
                allBusinesses.filter { it.id == 4L }.map { it.copy(matchingItemName = "Car Wash", startingPrice = 15000.0) }
            }
            serviceId == 4L || (query != null && query.contains("salon", ignoreCase = true)) -> {
                allBusinesses.filter { it.id == 7L }.map { it.copy(matchingItemName = "Salon", startingPrice = 25000.0) }
            }
            serviceId == 7L || (query != null && (query.contains("phone repair", ignoreCase = true) || query.contains("repair", ignoreCase = true))) -> {
                allBusinesses.filter { it.id == 5L }.map { it.copy(matchingItemName = "Phone Repair", startingPrice = 35000.0) }
            }
            query != null && query.isNotBlank() -> {
                val q = query.trim().lowercase()
                allBusinesses.filter {
                    it.name.lowercase().contains(q) || it.address.lowercase().contains(q) || (it.matchingItemName?.lowercase()?.contains(q) == true)
                }
            }
            else -> allBusinesses
        }

        if (minPrice != null) {
            filtered = filtered.filter { it.startingPrice == null || it.startingPrice >= minPrice }
        }
        if (maxPrice != null) {
            filtered = filtered.filter { it.startingPrice != null && it.startingPrice <= maxPrice }
        }
        if (distanceKm != null) {
            val centerLat = userLat ?: -6.8182
            val centerLng = userLng ?: 39.2778
            filtered = filtered.filter {
                calculateDistanceKm(centerLat, centerLng, it.latitude, it.longitude) <= distanceKm
            }
        }
        if (availableNow == true) {
            filtered = filtered.filter { it.supportsDelivery || it.supportsPickup }
        }

        if (sort != null) {
            filtered = when (sort) {
                SortOption.LOWEST_PRICE -> filtered.sortedBy { it.startingPrice ?: Double.MAX_VALUE }
                SortOption.HIGHEST_RATING -> filtered.sortedByDescending { it.rating }
                SortOption.NEAREST -> {
                    val centerLat = userLat ?: -6.8182
                    val centerLng = userLng ?: 39.2778
                    filtered.sortedBy { calculateDistanceKm(centerLat, centerLng, it.latitude, it.longitude) }
                }
            }
        }

        return filtered
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = (lat2 - lat1) * (PI / 180.0)
        val dLon = (lon2 - lon1) * (PI / 180.0)
        val a = sin(dLat / 2).pow(2.0) + cos(lat1 * (PI / 180.0)) * cos(lat2 * (PI / 180.0)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun String.capitalizeWords(): String = this.split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
}
