package tz.mtanav.business;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PhotoPaymentRequest {
    @Min(value = 1, message = "Must purchase at least 1 photo")
    private int additionalPhotos;
    private String paymentMethod;
}
