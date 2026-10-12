package tz.mtanav.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessMapDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String buildingName;
    private String shopNumber;
    private Double rating;
    private Integer reviewCount;
    private String logoUrl;
    private String matchingItemName;
    private BigDecimal startingPrice;
    private boolean supportsDelivery;
    private boolean supportsPickup;
}
