package tz.mtanav.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String verificationStatus;
    private boolean supportsDelivery;
    private boolean supportsPickup;
    private Double latitude;
    private Double longitude;
    private String address;
    private String city;
    private String region;
    private String country;
    private String buildingName;
    private String floor;
    private String shopNumber;
    private String street;
    private String landmark;
    private String logoUrl;
    private Double rating;
    private Integer reviewCount;
    private Integer paidPhotoAllowance;
    private List<String> imageUrls;
}
