package tz.mtanav.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickProductDto {
    private Long id;
    private String name;
    private String icon;
    private String description;
}
