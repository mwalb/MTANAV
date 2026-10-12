package tz.mtanav.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val email: String,
    val fullName: String,
    val phoneNumber: String? = null,
    val roles: List<String> = emptyList()
)

@Serializable
data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val user: UserDto
)

@Serializable
data class CategoryDto(
    val id: Long,
    val name: String,
    val icon: String? = null,
    val description: String? = null
)

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val isQuickProduct: Boolean = false,
    val quickProductIcon: String? = null,
    val description: String? = null,
    val categoryId: Long? = null
)

@Serializable
data class QuickProductDto(
    val id: Long,
    val name: String,
    val icon: String,
    val description: String? = null
)

@Serializable
data class BusinessMapDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val buildingName: String? = null,
    val shopNumber: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val logoUrl: String? = null,
    val matchingItemName: String? = null,
    val startingPrice: Double? = null,
    val supportsDelivery: Boolean = true,
    val supportsPickup: Boolean = true
)

@Serializable
data class BusinessDto(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val verificationStatus: String,
    val supportsDelivery: Boolean,
    val supportsPickup: Boolean,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val region: String? = null,
    val country: String? = null,
    val buildingName: String? = null,
    val floor: String? = null,
    val shopNumber: String? = null,
    val street: String? = null,
    val landmark: String? = null,
    val logoUrl: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val paidPhotoAllowance: Int = 0,
    val imageUrls: List<String> = emptyList()
)

@Serializable
data class ProductVariantDto(
    val id: Long,
    val name: String,
    val quantity: Int,
    val unit: String = "pcs",
    val price: Double,
    val availableQuantity: Int
)

@Serializable
data class BusinessProductDto(
    val id: Long,
    val businessId: Long,
    val businessName: String,
    val productId: Long,
    val productName: String,
    val productDescription: String? = null,
    val sku: String? = null,
    val price: Double,
    val salePrice: Double? = null,
    val minOrderQuantity: Int = 1,
    val availableQuantity: Int = 100,
    val unit: String = "pcs",
    val availabilityStatus: String = "IN_STOCK",
    val imageUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val variants: List<ProductVariantDto> = emptyList()
)

@Serializable
data class BusinessServiceDto(
    val id: Long,
    val businessId: Long,
    val businessName: String,
    val serviceId: Long,
    val serviceName: String,
    val serviceIcon: String? = null,
    val serviceDescription: String? = null,
    val price: Double,
    val durationMinutes: Int = 60,
    val available: Boolean = true
)

@Serializable
data class CartItem(
    val businessProductId: Long,
    val businessId: Long,
    val businessName: String,
    val productName: String,
    val variantId: Long? = null,
    val variantName: String? = null,
    val unitPrice: Double,
    val quantity: Int
)

@Serializable
data class OrderDto(
    val id: Long,
    val customerId: Long,
    val customerName: String,
    val businessId: Long,
    val businessName: String,
    val orderStatus: String,
    val orderType: String,
    val totalAmount: Double,
    val deliveryAddress: String? = null,
    val notes: String? = null,
    val createdAt: String
)

@Serializable
data class BookingDto(
    val id: Long,
    val customerId: Long,
    val customerName: String,
    val businessId: Long,
    val businessName: String,
    val serviceId: Long,
    val serviceName: String,
    val bookingStatus: String,
    val bookingDate: String,
    val bookingTime: String,
    val price: Double,
    val notes: String? = null,
    val createdAt: String
)

@Serializable
data class PhotoUsageDto(
    val businessId: Long,
    val currentPhotoCount: Int,
    val freePhotoLimit: Int,
    val paidPhotoAllowance: Int,
    val maxAllowedPhotos: Int,
    val pricePerExtraPhoto: Double,
    val additionalPhotosToPayFor: Int = 0,
    val totalCostForAdditionalPhotos: Double = 0.0
)

@Serializable
data class PhotoPaymentRequest(
    val additionalPhotos: Int,
    val paymentMethod: String = "MOBILE_MONEY"
)

@Serializable
data class AddPhotoRequest(
    val imageUrl: String
)

@Serializable
data class CreateBusinessRequest(
    val name: String,
    val description: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val supportsDelivery: Boolean = true,
    val supportsPickup: Boolean = true,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val region: String? = null,
    val buildingName: String? = null,
    val floor: String? = null,
    val shopNumber: String? = null,
    val street: String? = null,
    val landmark: String? = null,
    val logoUrl: String? = null
)

@Serializable
data class AuditLogDto(
    val id: Long,
    val userId: Long? = null,
    val action: String,
    val entity: String,
    val entityId: Long? = null,
    val details: String? = null,
    val createdAt: String? = null
)

// LOCAL JOBS MODELS
@Serializable
data class JobDto(
    val id: Long,
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val budgetAmount: Double,
    val currency: String = "TZS",
    val status: String = "OPEN",
    val publicationStatus: String = "PUBLISHED",
    val posterId: Long,
    val businessId: Long? = null,
    val businessName: String? = null,
    val employmentType: String = "FULL_TIME",
    val payType: String? = "FIXED",
    val payAmountMin: Double? = null,
    val payAmountMax: Double? = null,
    val payFrequency: String? = "ONE_TIME",
    val applicationDeadline: String? = null,
    val qualifications: String? = null,
    val responsibilities: String? = null,
    val contactInstructions: String? = null,
    val createdAt: String? = null
)

@Serializable
data class JobApplicationDto(
    val id: Long,
    val jobId: Long,
    val jobTitle: String? = null,
    val providerId: Long,
    val applicantName: String? = null,
    val applicantEmail: String? = null,
    val applicantPhone: String? = null,
    val proposedRate: Double,
    val coverNote: String? = null,
    val status: String = "SUBMITTED",
    val createdAt: String? = null
)

@Serializable
data class CreateJobRequest(
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val budgetAmount: Double,
    val businessId: Long? = null,
    val employmentType: String = "FULL_TIME",
    val payType: String = "FIXED",
    val payAmountMin: Double? = null,
    val payAmountMax: Double? = null,
    val payFrequency: String = "MONTHLY",
    val applicationDeadline: String? = null,
    val qualifications: String? = null,
    val responsibilities: String? = null,
    val contactInstructions: String? = null,
    val publicationStatus: String = "PUBLISHED"
)

@Serializable
data class CreateJobApplicationRequest(
    val proposedRate: Double,
    val coverNote: String
)

// PROMOTIONS MODELS
@Serializable
data class PromotionDto(
    val id: Long,
    val businessId: Long,
    val businessName: String? = null,
    val businessLatitude: Double? = null,
    val businessLongitude: Double? = null,
    val productId: Long? = null,
    val productName: String? = null,
    val serviceId: Long? = null,
    val serviceName: String? = null,
    val title: String,
    val description: String,
    val category: String,
    val originalPrice: Double? = null,
    val promotionalPrice: Double? = null,
    val discountPercentage: Int? = null,
    val termsAndConditions: String? = null,
    val offerType: String = "DISCOUNT",
    val imageUrl: String? = null,
    val startDate: String? = null,
    val expiryDate: String? = null,
    val status: String = "ACTIVE",
    val isSponsored: Boolean = false,
    val isSaved: Boolean = false,
    val impressionsCount: Int = 0,
    val clicksCount: Int = 0,
    val createdAt: String? = null
)

@Serializable
data class CreatePromotionRequest(
    val businessId: Long,
    val productId: Long? = null,
    val serviceId: Long? = null,
    val title: String,
    val description: String,
    val category: String,
    val originalPrice: Double? = null,
    val promotionalPrice: Double? = null,
    val termsAndConditions: String? = null,
    val offerType: String = "DISCOUNT",
    val imageUrl: String? = null,
    val startDate: String? = null,
    val expiryDate: String? = null,
    val status: String = "ACTIVE",
    val isSponsored: Boolean = false
)

@Serializable
data class ApiResponseWrapper<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class SearchSuggestionDto(
    val id: Long,
    val name: String,
    val type: String, // "PRODUCT", "SERVICE", "BUSINESS"
    val thumbnailUrl: String? = null,
    val price: Double? = null,
    val businessId: Long? = null,
    val businessName: String? = null,
    val availabilityStatus: String? = null,
    val targetId: Long? = null
)

