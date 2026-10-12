package tz.mtanav.promotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.product.BusinessProductRepository;
import tz.mtanav.service.BusinessServiceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final SavedPromotionRepository savedPromotionRepository;
    private final BusinessRepository businessRepository;
    private final BusinessProductRepository businessProductRepository;
    private final BusinessServiceRepository businessServiceRepository;

    public PromotionService(PromotionRepository promotionRepository,
                            SavedPromotionRepository savedPromotionRepository,
                            BusinessRepository businessRepository,
                            BusinessProductRepository businessProductRepository,
                            BusinessServiceRepository businessServiceRepository) {
        this.promotionRepository = promotionRepository;
        this.savedPromotionRepository = savedPromotionRepository;
        this.businessRepository = businessRepository;
        this.businessProductRepository = businessProductRepository;
        this.businessServiceRepository = businessServiceRepository;
    }

    public List<PromotionDto> getActivePromotions(String query, String category, String offerType, Long userId) {
        OffsetDateTime now = OffsetDateTime.now();
        List<Promotion> promotions = promotionRepository.findActiveEligiblePromotions(
                now,
                query != null && !query.isBlank() ? query.trim() : null,
                category != null && !category.isBlank() ? category.trim() : null,
                offerType != null && !offerType.isBlank() ? offerType.trim() : null
        );

        return promotions.stream()
                .map(p -> toDto(p, userId))
                .collect(Collectors.toList());
    }

    public PromotionDto getPromotionById(Long id, Long userId) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        return toDto(promotion, userId);
    }

    @Transactional
    public PromotionDto createPromotion(Long ownerUserId, Promotion payload) {
        Business business = businessRepository.findById(payload.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (business.getOwner() == null || !business.getOwner().getId().equals(ownerUserId)) {
            throw new RuntimeException("Unauthorized: You can only create promotions for businesses you own");
        }

        if (payload.getStartDate() == null) {
            payload.setStartDate(OffsetDateTime.now());
        }
        if (payload.getExpiryDate() == null) {
            payload.setExpiryDate(OffsetDateTime.now().plusDays(14));
        }

        calculateDiscountPercentage(payload);

        if (payload.getStatus() == null) {
            payload.setStatus("ACTIVE");
        }

        Promotion saved = promotionRepository.save(payload);
        return toDto(saved, ownerUserId);
    }

    @Transactional
    public PromotionDto updatePromotion(Long ownerUserId, Long id, Promotion payload) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        Business business = businessRepository.findById(existing.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (business.getOwner() == null || !business.getOwner().getId().equals(ownerUserId)) {
            throw new RuntimeException("Unauthorized: You can only edit promotions for businesses you own");
        }

        if (payload.getTitle() != null) existing.setTitle(payload.getTitle());
        if (payload.getDescription() != null) existing.setDescription(payload.getDescription());
        if (payload.getCategory() != null) existing.setCategory(payload.getCategory());
        if (payload.getOriginalPrice() != null) existing.setOriginalPrice(payload.getOriginalPrice());
        if (payload.getPromotionalPrice() != null) existing.setPromotionalPrice(payload.getPromotionalPrice());
        if (payload.getTermsAndConditions() != null) existing.setTermsAndConditions(payload.getTermsAndConditions());
        if (payload.getOfferType() != null) existing.setOfferType(payload.getOfferType());
        if (payload.getImageUrl() != null) existing.setImageUrl(payload.getImageUrl());
        if (payload.getStartDate() != null) existing.setStartDate(payload.getStartDate());
        if (payload.getExpiryDate() != null) existing.setExpiryDate(payload.getExpiryDate());
        if (payload.getStatus() != null) existing.setStatus(payload.getStatus());
        if (payload.getIsSponsored() != null) existing.setIsSponsored(payload.getIsSponsored());

        calculateDiscountPercentage(existing);
        existing.setUpdatedAt(OffsetDateTime.now());

        Promotion saved = promotionRepository.save(existing);
        return toDto(saved, ownerUserId);
    }

    @Transactional
    public PromotionDto updatePromotionStatus(Long ownerUserId, Long id, String newStatus) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        Business business = businessRepository.findById(existing.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (business.getOwner() == null || !business.getOwner().getId().equals(ownerUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this business");
        }

        existing.setStatus(newStatus.toUpperCase());
        existing.setUpdatedAt(OffsetDateTime.now());
        Promotion saved = promotionRepository.save(existing);
        return toDto(saved, ownerUserId);
    }

    public List<PromotionDto> getOwnerPromotions(Long ownerUserId) {
        List<Business> myBusinesses = businessRepository.findByOwnerId(ownerUserId);
        List<Long> bizIds = myBusinesses.stream().map(Business::getId).collect(Collectors.toList());

        return bizIds.stream()
                .flatMap(bizId -> promotionRepository.findByBusinessId(bizId).stream())
                .map(p -> toDto(p, ownerUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePromotion(Long userId, Long promotionId) {
        if (!savedPromotionRepository.existsByUserIdAndPromotionId(userId, promotionId)) {
            SavedPromotion saved = new SavedPromotion();
            saved.setUserId(userId);
            saved.setPromotionId(promotionId);
            savedPromotionRepository.save(saved);
        }
    }

    @Transactional
    public void unsavePromotion(Long userId, Long promotionId) {
        savedPromotionRepository.deleteByUserIdAndPromotionId(userId, promotionId);
    }

    public List<PromotionDto> getSavedPromotions(Long userId) {
        List<SavedPromotion> savedList = savedPromotionRepository.findByUserId(userId);
        return savedList.stream()
                .map(sp -> promotionRepository.findById(sp.getPromotionId()).orElse(null))
                .filter(p -> p != null)
                .map(p -> {
                    PromotionDto dto = toDto(p, userId);
                    dto.setIsSaved(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<String> getPromotionCategories() {
        return Arrays.asList(
                "Electronics",
                "Household",
                "Automotive",
                "Fashion & Footwear",
                "Beauty & Salon",
                "Printing & Office",
                "Services & Repairs",
                "Food & Groceries"
        );
    }

    private void calculateDiscountPercentage(Promotion p) {
        if (p.getOriginalPrice() != null && p.getPromotionalPrice() != null
                && p.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0
                && p.getOriginalPrice().compareTo(p.getPromotionalPrice()) > 0) {

            BigDecimal diff = p.getOriginalPrice().subtract(p.getPromotionalPrice());
            BigDecimal percentage = diff.multiply(new BigDecimal(100)).divide(p.getOriginalPrice(), 0, RoundingMode.HALF_UP);
            p.setDiscountPercentage(percentage.intValue());
        } else if (p.getDiscountPercentage() == null) {
            p.setDiscountPercentage(0);
        }
    }

    private PromotionDto toDto(Promotion p, Long currentUserId) {
        PromotionDto dto = new PromotionDto();
        dto.setId(p.getId());
        dto.setBusinessId(p.getBusinessId());

        businessRepository.findById(p.getBusinessId()).ifPresent(b -> {
            dto.setBusinessName(b.getName());
            dto.setBusinessLatitude(b.getLatitude());
            dto.setBusinessLongitude(b.getLongitude());
        });

        dto.setProductId(p.getProductId());
        if (p.getProductId() != null) {
            businessProductRepository.findById(p.getProductId()).ifPresent(bp -> {
                dto.setProductName(bp.getProduct() != null ? bp.getProduct().getName() : "Product #" + bp.getId());
            });
        }

        dto.setServiceId(p.getServiceId());
        if (p.getServiceId() != null) {
            businessServiceRepository.findById(p.getServiceId()).ifPresent(bs -> {
                dto.setServiceName(bs.getService() != null ? bs.getService().getName() : "Service #" + bs.getId());
            });
        }

        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setOriginalPrice(p.getOriginalPrice());
        dto.setPromotionalPrice(p.getPromotionalPrice());
        dto.setDiscountPercentage(p.getDiscountPercentage());
        dto.setTermsAndConditions(p.getTermsAndConditions());
        dto.setOfferType(p.getOfferType());
        dto.setImageUrl(p.getImageUrl());
        dto.setStartDate(p.getStartDate());
        dto.setExpiryDate(p.getExpiryDate());

        OffsetDateTime now = OffsetDateTime.now();
        if (p.getExpiryDate() != null && p.getExpiryDate().isBefore(now)) {
            dto.setStatus("EXPIRED");
        } else {
            dto.setStatus(p.getStatus());
        }

        dto.setIsSponsored(p.getIsSponsored() != null ? p.getIsSponsored() : false);
        dto.setImpressionsCount(p.getImpressionsCount());
        dto.setClicksCount(p.getClicksCount());
        dto.setCreatedAt(p.getCreatedAt());

        if (currentUserId != null) {
            dto.setIsSaved(savedPromotionRepository.existsByUserIdAndPromotionId(currentUserId, p.getId()));
        } else {
            dto.setIsSaved(false);
        }

        return dto;
    }
}
