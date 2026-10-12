package tz.mtanav.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestionDto {
    private Long id;
    private String name;
    private String type; // "PRODUCT", "SERVICE", "BUSINESS"
    private String thumbnailUrl;
    private BigDecimal price;
    private Long businessId;
    private String businessName;
    private String availabilityStatus;
    private Long targetId;
}
