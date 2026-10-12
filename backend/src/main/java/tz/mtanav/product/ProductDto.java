package tz.mtanav.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private boolean isQuickProduct;
    private String quickProductIcon;
    private String description;
    private Long categoryId;
}
