package tz.mtanav.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessProductRepository businessProductRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public List<QuickProductDto> getQuickProducts() {
        return productRepository.findByQuickProductTrue().stream()
                .map(p -> QuickProductDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .icon(p.getQuickProductIcon() != null ? p.getQuickProductIcon() : "📦")
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return mapToProductDto(product);
    }

    @Transactional(readOnly = true)
    public List<BusinessProductDto> getBusinessProducts(Long businessId) {
        return businessProductRepository.findByBusinessId(businessId).stream()
                .map(this::mapToBusinessProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusinessProductDto getBusinessProductById(Long id) {
        BusinessProduct bp = businessProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business product not found: " + id));
        return mapToBusinessProductDto(bp);
    }

    public ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .isQuickProduct(product.isQuickProduct())
                .quickProductIcon(product.getQuickProductIcon())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .build();
    }

    public BusinessProductDto mapToBusinessProductDto(BusinessProduct bp) {
        List<ProductVariantDto> variantDtos = productVariantRepository.findByBusinessProductId(bp.getId()).stream()
                .map(v -> ProductVariantDto.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .quantity(v.getQuantity())
                        .unit(v.getUnit())
                        .price(v.getPrice())
                        .availableQuantity(v.getAvailableQuantity())
                        .build())
                .collect(Collectors.toList());

        String pName = bp.getProduct().getName().toLowerCase();
        String imgUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500";
        if (pName.contains("spoon")) {
            imgUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500";
        } else if (pName.contains("rice")) {
            imgUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500";
        } else if (pName.contains("phone")) {
            imgUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500";
        } else if (pName.contains("charger")) {
            imgUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500";
        } else if (pName.contains("shoe")) {
            imgUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500";
        }

        return BusinessProductDto.builder()
                .id(bp.getId())
                .businessId(bp.getBusiness().getId())
                .businessName(bp.getBusiness().getName())
                .productId(bp.getProduct().getId())
                .productName(bp.getProduct().getName())
                .productDescription(bp.getProduct().getDescription())
                .sku(bp.getSku())
                .price(bp.getPrice())
                .salePrice(bp.getSalePrice())
                .minOrderQuantity(bp.getMinOrderQuantity())
                .availableQuantity(bp.getAvailableQuantity())
                .unit(bp.getUnit())
                .availabilityStatus(bp.getAvailabilityStatus().name())
                .imageUrl(imgUrl)
                .imageUrls(List.of(imgUrl))
                .variants(variantDtos)
                .build();
    }
}
