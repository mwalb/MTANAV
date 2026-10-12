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
public class PhotoUsageDto {
    private Long businessId;
    private int currentPhotoCount;
    private int freePhotoLimit;
    private int paidPhotoAllowance;
    private int maxAllowedPhotos;
    private BigDecimal pricePerExtraPhoto;
    private int additionalPhotosToPayFor;
    private BigDecimal totalCostForAdditionalPhotos;
}
