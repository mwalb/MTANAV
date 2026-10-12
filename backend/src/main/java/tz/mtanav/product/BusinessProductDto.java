package tz.mtanav.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessProductDto {
    private Long id;
    private Long businessId;
    private String businessName;
    private Long productId;
    private String productName;
    private String productDescription;
    private String sku;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer minOrderQuantity;
    private Integer availableQuantity;
    private String unit;
    private String availabilityStatus;
    private String imageUrl;
    private List<String> imageUrls;
    private List<ProductVariantDto> variants;
}
