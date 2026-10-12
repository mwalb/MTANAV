package tz.mtanav.business;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddPhotoRequest {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
