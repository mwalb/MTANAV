package tz.mtanav.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessServiceDto {
    private Long id;
    private Long businessId;
    private String businessName;
    private Long serviceId;
    private String serviceName;
    private String serviceIcon;
    private String serviceDescription;
    private BigDecimal price;
    private Integer durationMinutes;
    private boolean available;
}
