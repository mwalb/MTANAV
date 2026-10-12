package tz.mtanav.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tz.mtanav.business.VerificationStatus;

@Data
public class UpdateVerificationRequest {
    @NotNull(message = "Verification status is required")
    private VerificationStatus status;
}
