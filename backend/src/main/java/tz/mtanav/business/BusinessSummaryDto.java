package tz.mtanav.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessSummaryDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Double rating;
    private Integer reviewCount;
    private String logoUrl;
    private Double latitude;
    private Double longitude;
}
