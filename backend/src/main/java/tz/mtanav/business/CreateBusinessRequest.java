package tz.mtanav.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBusinessRequest {
    @NotBlank(message = "Business name is required")
    private String name;

    private String description;
    private String phone;
    private String email;
    private boolean supportsDelivery = true;
    private boolean supportsPickup = true;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String region;
    private String buildingName;
    private String floor;
    private String shopNumber;
    private String street;
    private String landmark;
    private String logoUrl;
}
