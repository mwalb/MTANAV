package tz.mtanav.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.admin.AuditLog;
import tz.mtanav.admin.AuditLogRepository;
import tz.mtanav.product.BusinessProduct;
import tz.mtanav.product.BusinessProductRepository;
import tz.mtanav.product.Product;
import tz.mtanav.product.ProductRepository;
import tz.mtanav.service.BusinessServiceEntity;
import tz.mtanav.service.BusinessServiceRepository;
import tz.mtanav.service.ServiceEntity;
import tz.mtanav.service.ServiceRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final BusinessProductRepository businessProductRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessServiceRepository businessServiceRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Value("${mtanav.photo.free-limit:5}")
    private int freePhotoLimit = 5;

    @Value("${mtanav.photo.price-per-extra:10.0}")
    private BigDecimal pricePerExtraPhoto = new BigDecimal("10.0");

    @Transactional(readOnly = true)
    public List<BusinessMapDto> getMapBusinesses(Long productId, Long serviceId, String query) {
        return getMapBusinesses(productId, serviceId, query, null, null, null, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public List<BusinessMapDto> getMapBusinesses(
            Long productId,
            Long serviceId,
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double maxDistanceKm,
            Double userLat,
            Double userLng,
            Boolean availableNow,
            String sort) {

        List<Business> businesses;
        String matchingItemName = null;

        if (productId != null) {
            Product product = productRepository.findById(productId).orElse(null);
            matchingItemName = product != null ? product.getName() : null;
            businesses = businessRepository.findBusinessesSellingProduct(productId);
        } else if (serviceId != null) {
            ServiceEntity service = serviceRepository.findById(serviceId).orElse(null);
            matchingItemName = service != null ? service.getName() : null;
            businesses = businessRepository.findBusinessesProvidingService(serviceId);
        } else if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();

            var productOpt = productRepository.findByNameIgnoreCase(trimmedQuery);
            if (productOpt.isPresent()) {
                matchingItemName = productOpt.get().getName();
                businesses = businessRepository.findBusinessesSellingProduct(productOpt.get().getId());
            } else {
                var serviceOpt = serviceRepository.findByNameIgnoreCase(trimmedQuery);
                if (serviceOpt.isPresent()) {
                    matchingItemName = serviceOpt.get().getName();
                    businesses = businessRepository.findBusinessesProvidingService(serviceOpt.get().getId());
                } else {
                    businesses = businessRepository.searchBusinesses(trimmedQuery);
                }
            }
        } else {
            businesses = businessRepository.findByVerificationStatus(VerificationStatus.VERIFIED);
        }

        final String finalItemName = matchingItemName;
        List<BusinessMapDto> dtoList = businesses.stream()
                .map(b -> mapToBusinessMapDto(b, productId, serviceId, finalItemName))
                .collect(Collectors.toList());

        // Apply budget & distance filters (NO rating filter)
        dtoList = dtoList.stream().filter(dto -> {
            if (minPrice != null && dto.getStartingPrice() != null && dto.getStartingPrice().compareTo(minPrice) < 0) {
                return false;
            }
            if (maxPrice != null && dto.getStartingPrice() != null && dto.getStartingPrice().compareTo(maxPrice) > 0) {
                return false;
            }
            if (maxDistanceKm != null && userLat != null && userLng != null) {
                double dist = calculateDistanceKm(userLat, userLng, dto.getLatitude(), dto.getLongitude());
                if (dist > maxDistanceKm) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        // Apply sorting
        if (sort != null) {
            switch (sort.toUpperCase()) {
                case "LOWEST_PRICE":
                    dtoList.sort((a, b) -> {
                        if (a.getStartingPrice() == null && b.getStartingPrice() == null) return 0;
                        if (a.getStartingPrice() == null) return 1;
                        if (b.getStartingPrice() == null) return -1;
                        return a.getStartingPrice().compareTo(b.getStartingPrice());
                    });
                    break;
                case "HIGHEST_RATING":
                case "HIGHEST_RATED":
                    dtoList.sort((a, b) -> Double.compare(
                            b.getRating() != null ? b.getRating() : 0.0,
                            a.getRating() != null ? a.getRating() : 0.0
                    ));
                    break;
                case "NEAREST":
                default:
                    if (userLat != null && userLng != null) {
                        dtoList.sort((a, b) -> {
                            double distA = calculateDistanceKm(userLat, userLng, a.getLatitude(), a.getLongitude());
                            double distB = calculateDistanceKm(userLat, userLng, b.getLatitude(), b.getLongitude());
                            return Double.compare(distA, distB);
                        });
                    }
                    break;
            }
        }

        return dtoList;
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional(readOnly = true)
    public BusinessDto getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + id));
        return mapToBusinessDto(business);
    }

    @Transactional(readOnly = true)
    public List<BusinessDto> getBusinessesByOwner(Long ownerId) {
        return businessRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToBusinessDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BusinessDto createBusiness(Long ownerId, CreateBusinessRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found: " + ownerId));

        Business business = Business.builder()
                .owner(owner)
                .name(request.getName())
                .description(request.getDescription())
                .phone(request.getPhone())
                .email(request.getEmail())
                .verificationStatus(VerificationStatus.PENDING)
                .supportsDelivery(request.isSupportsDelivery())
                .supportsPickup(request.isSupportsPickup())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .city(request.getCity())
                .region(request.getRegion())
                .country("Tanzania")
                .buildingName(request.getBuildingName())
                .floor(request.getFloor())
                .shopNumber(request.getShopNumber())
                .street(request.getStreet())
                .landmark(request.getLandmark())
                .logoUrl(request.getLogoUrl())
                .rating(0.0)
                .reviewCount(0)
                .paidPhotoAllowance(0)
                .images(new ArrayList<>())
                .build();

        Business saved = businessRepository.save(business);
        return mapToBusinessDto(saved);
    }

    @Transactional(readOnly = true)
    public PhotoUsageDto getPhotoUsage(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        int currentCount = business.getImages().size();
        int paidAllowance = business.getPaidPhotoAllowance() != null ? business.getPaidPhotoAllowance() : 0;
        int maxAllowed = freePhotoLimit + paidAllowance;

        return PhotoUsageDto.builder()
                .businessId(businessId)
                .currentPhotoCount(currentCount)
                .freePhotoLimit(freePhotoLimit)
                .paidPhotoAllowance(paidAllowance)
                .maxAllowedPhotos(maxAllowed)
                .pricePerExtraPhoto(pricePerExtraPhoto)
                .additionalPhotosToPayFor(0)
                .totalCostForAdditionalPhotos(BigDecimal.ZERO)
                .build();
    }

    @Transactional
    public PhotoUsageDto payForPhotos(Long ownerId, Long businessId, PhotoPaymentRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        if (!business.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this business");
        }

        int additional = request.getAdditionalPhotos();
        if (additional <= 0) {
            throw new IllegalArgumentException("Additional photos count must be greater than 0");
        }

        BigDecimal totalCost = pricePerExtraPhoto.multiply(BigDecimal.valueOf(additional));

        int currentAllowance = business.getPaidPhotoAllowance() != null ? business.getPaidPhotoAllowance() : 0;
        business.setPaidPhotoAllowance(currentAllowance + additional);
        businessRepository.save(business);

        auditLogRepository.save(AuditLog.builder()
                .userId(ownerId)
                .action("PHOTO_ALLOWANCE_PURCHASE")
                .entity("BUSINESS")
                .entityId(businessId)
                .details("Purchased " + additional + " extra photo allowance for TSh " + totalCost)
                .build());

        return getPhotoUsage(businessId);
    }

    @Transactional
    public BusinessDto addBusinessPhoto(Long ownerId, Long businessId, String imageUrl) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        if (!business.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Unauthorized: You do not own this business");
        }

        int currentCount = business.getImages().size();
        int paidAllowance = business.getPaidPhotoAllowance() != null ? business.getPaidPhotoAllowance() : 0;
        int maxAllowed = freePhotoLimit + paidAllowance;

        if (currentCount >= maxAllowed) {
            throw new IllegalStateException("Photo upload limit reached (" + maxAllowed + "). Please purchase extra photo allowance (TSh " + pricePerExtraPhoto + " per photo).");
        }

        BusinessImage image = BusinessImage.builder()
                .business(business)
                .imageUrl(imageUrl)
                .displayOrder(currentCount + 1)
                .build();

        business.getImages().add(image);
        Business saved = businessRepository.save(business);
        return mapToBusinessDto(saved);
    }

    private BusinessMapDto mapToBusinessMapDto(Business b, Long productId, Long serviceId, String matchingItemName) {
        BigDecimal startingPrice = null;

        if (productId != null) {
            startingPrice = businessProductRepository.findByBusinessIdAndProductId(b.getId(), productId)
                    .map(BusinessProduct::getPrice)
                    .orElse(null);
        } else if (serviceId != null) {
            startingPrice = businessServiceRepository.findByBusinessId(b.getId()).stream()
                    .filter(bs -> bs.getService().getId().equals(serviceId))
                    .findFirst()
                    .map(BusinessServiceEntity::getPrice)
                    .orElse(null);
        }

        return BusinessMapDto.builder()
                .id(b.getId())
                .name(b.getName())
                .latitude(b.getLatitude())
                .longitude(b.getLongitude())
                .address(b.getAddress())
                .city(b.getCity())
                .buildingName(b.getBuildingName())
                .shopNumber(b.getShopNumber())
                .rating(b.getRating())
                .reviewCount(b.getReviewCount())
                .logoUrl(b.getLogoUrl())
                .matchingItemName(matchingItemName)
                .startingPrice(startingPrice)
                .supportsDelivery(b.isSupportsDelivery())
                .supportsPickup(b.isSupportsPickup())
                .build();
    }

    public BusinessDto mapToBusinessDto(Business b) {
        List<String> imageUrls = b.getImages().stream()
                .map(BusinessImage::getImageUrl)
                .collect(Collectors.toList());

        return BusinessDto.builder()
                .id(b.getId())
                .ownerId(b.getOwner().getId())
                .name(b.getName())
                .description(b.getDescription())
                .phone(b.getPhone())
                .email(b.getEmail())
                .verificationStatus(b.getVerificationStatus().name())
                .supportsDelivery(b.isSupportsDelivery())
                .supportsPickup(b.isSupportsPickup())
                .latitude(b.getLatitude())
                .longitude(b.getLongitude())
                .address(b.getAddress())
                .city(b.getCity())
                .region(b.getRegion())
                .country(b.getCountry())
                .buildingName(b.getBuildingName())
                .floor(b.getFloor())
                .shopNumber(b.getShopNumber())
                .street(b.getStreet())
                .landmark(b.getLandmark())
                .logoUrl(b.getLogoUrl())
                .rating(b.getRating())
                .reviewCount(b.getReviewCount())
                .paidPhotoAllowance(b.getPaidPhotoAllowance())
                .imageUrls(imageUrls)
                .build();
    }
}
