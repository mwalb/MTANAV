package tz.mtanav.promotion;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PromotionDto {
    private Long id;
    private Long businessId;
    private String businessName;
    private Double businessLatitude;
    private Double businessLongitude;
    private Long productId;
    private String productName;
    private Long serviceId;
    private String serviceName;
    private String title;
    private String description;
    private String category;
    private BigDecimal originalPrice;
    private BigDecimal promotionalPrice;
    private Integer discountPercentage;
    private String termsAndConditions;
    private String offerType;
    private String imageUrl;
    private OffsetDateTime startDate;
    private OffsetDateTime expiryDate;
    private String status;
    private Boolean isSponsored;
    private Boolean isSaved;
    private Integer impressionsCount;
    private Integer clicksCount;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public Double getBusinessLatitude() { return businessLatitude; }
    public void setBusinessLatitude(Double businessLatitude) { this.businessLatitude = businessLatitude; }

    public Double getBusinessLongitude() { return businessLongitude; }
    public void setBusinessLongitude(Double businessLongitude) { this.businessLongitude = businessLongitude; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getPromotionalPrice() { return promotionalPrice; }
    public void setPromotionalPrice(BigDecimal promotionalPrice) { this.promotionalPrice = promotionalPrice; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public OffsetDateTime getStartDate() { return startDate; }
    public void setStartDate(OffsetDateTime startDate) { this.startDate = startDate; }

    public OffsetDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(OffsetDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsSponsored() { return isSponsored; }
    public void setIsSponsored(Boolean isSponsored) { this.isSponsored = isSponsored; }

    public Boolean getIsSaved() { return isSaved; }
    public void setIsSaved(Boolean isSaved) { this.isSaved = isSaved; }

    public Integer getImpressionsCount() { return impressionsCount; }
    public void setImpressionsCount(Integer impressionsCount) { this.impressionsCount = impressionsCount; }

    public Integer getClicksCount() { return clicksCount; }
    public void setClicksCount(Integer clicksCount) { this.clicksCount = clicksCount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
