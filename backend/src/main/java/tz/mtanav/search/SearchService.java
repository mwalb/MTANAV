package tz.mtanav.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.business.VerificationStatus;
import tz.mtanav.product.BusinessProduct;
import tz.mtanav.product.BusinessProductRepository;
import tz.mtanav.product.ProductService;
import tz.mtanav.service.BusinessServiceEntity;
import tz.mtanav.service.BusinessServiceRepository;
import tz.mtanav.service.ServiceEntity;
import tz.mtanav.service.ServiceRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BusinessProductRepository businessProductRepository;
    private final BusinessServiceRepository businessServiceRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<SearchSuggestionDto> getSearchSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String q = query.trim().toLowerCase();
        List<SearchSuggestionDto> suggestions = new ArrayList<>();

        // 1. PRODUCTS MATCHING QUERY
        List<BusinessProduct> products = businessProductRepository.searchAvailableByProductName(q);
        for (BusinessProduct bp : products) {
            if (suggestions.size() >= 8) break;
            var bpDto = productService.mapToBusinessProductDto(bp);
            suggestions.add(SearchSuggestionDto.builder()
                    .id(bp.getId())
                    .name(bp.getProduct().getName())
                    .type("PRODUCT")
                    .thumbnailUrl(bpDto.getImageUrl())
                    .price(bp.getSalePrice() != null ? bp.getSalePrice() : bp.getPrice())
                    .businessId(bp.getBusiness().getId())
                    .businessName(bp.getBusiness().getName())
                    .availabilityStatus(bp.getAvailabilityStatus().name())
                    .targetId(bp.getProduct().getId())
                    .build());
        }

        // 2. SERVICES MATCHING QUERY
        if (suggestions.size() < 8) {
            List<ServiceEntity> services = serviceRepository.findByNameContainingIgnoreCase(q);
            for (ServiceEntity s : services) {
                if (suggestions.size() >= 8) break;
                String serviceImg = getServiceImageUrl(s.getName());
                suggestions.add(SearchSuggestionDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .type("SERVICE")
                        .thumbnailUrl(serviceImg)
                        .price(null)
                        .businessId(null)
                        .businessName(s.getDescription())
                        .availabilityStatus("AVAILABLE")
                        .targetId(s.getId())
                        .build());
            }
        }

        // 3. BUSINESSES MATCHING QUERY
        if (suggestions.size() < 8) {
            List<Business> businesses = businessRepository.searchBusinesses(q);
            for (Business b : businesses) {
                if (suggestions.size() >= 8) break;
                if (b.getVerificationStatus() == VerificationStatus.VERIFIED) {
                    String shopImg = b.getLogoUrl() != null ? b.getLogoUrl() : "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=500";
                    suggestions.add(SearchSuggestionDto.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .type("BUSINESS")
                            .thumbnailUrl(shopImg)
                            .price(null)
                            .businessId(b.getId())
                            .businessName(b.getCity() != null ? b.getCity() + " • " + b.getAddress() : b.getAddress())
                            .availabilityStatus("OPEN")
                            .targetId(b.getId())
                            .build());
                }
            }
        }

        return suggestions;
    }

    private String getServiceImageUrl(String name) {
        if (name == null) return "https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?w=500";
        String n = name.toLowerCase();
        if (n.contains("garage") || n.contains("repair")) {
            return "https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?w=500";
        } else if (n.contains("car wash") || n.contains("wash")) {
            return "https://images.unsplash.com/photo-1520340356584-f9917d1beb6d?w=500";
        } else if (n.contains("salon") || n.contains("beauty")) {
            return "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=500";
        } else if (n.contains("phone") || n.contains("tech")) {
            return "https://images.unsplash.com/photo-1597740985671-2a8a3b80502e?w=500";
        } else if (n.contains("bank") || n.contains("finance")) {
            return "https://images.unsplash.com/photo-1501167786227-4cba60f6d58f?w=500";
        }
        return "https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?w=500";
    }
}
