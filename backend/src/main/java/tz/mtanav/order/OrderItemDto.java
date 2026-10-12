package tz.mtanav.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long businessProductId;
    private Long variantId;
    private String productName;
    private String variantName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
