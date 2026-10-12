package tz.mtanav.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {
    private Long id;
    private String name;
    private Integer quantity;
    private String unit;
    private BigDecimal price;
    private Integer availableQuantity;
}
