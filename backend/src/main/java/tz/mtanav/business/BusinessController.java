package tz.mtanav.business;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;

import tz.mtanav.product.BusinessProductDto;
import tz.mtanav.product.ProductService;
import tz.mtanav.security.UserPrincipal;
import tz.mtanav.service.BusinessServiceDto;
import tz.mtanav.service.ServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final ProductService productService;
    private final ServiceService serviceService;

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<BusinessMapDto>>> getMapBusinesses(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxDistanceKm,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLng,
            @RequestParam(required = false) Boolean availableNow,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(ApiResponse.success(businessService.getMapBusinesses(
                productId, serviceId, query, minPrice, maxPrice, maxDistanceKm, userLat, userLng, availableNow, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessDto>> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(businessService.getBusinessById(id)));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<List<BusinessProductDto>>> getBusinessProducts(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBusinessProducts(id)));
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<ApiResponse<List<BusinessServiceDto>>> getBusinessServices(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(serviceService.getBusinessServices(id)));
    }

    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<List<BusinessDto>>> getMyBusinesses(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(businessService.getBusinessesByOwner(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessDto>> createBusiness(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateBusinessRequest request) {
        return ResponseEntity.ok(ApiResponse.success(businessService.createBusiness(user.getId(), request), "Business created successfully"));
    }

    @GetMapping("/{id}/photo-usage")
    public ResponseEntity<ApiResponse<PhotoUsageDto>> getPhotoUsage(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(businessService.getPhotoUsage(id)));
    }

    @PostMapping("/{id}/photo-payment")
    public ResponseEntity<ApiResponse<PhotoUsageDto>> payForPhotos(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody PhotoPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(businessService.payForPhotos(user.getId(), id, request), "Photo allowance updated successfully"));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<BusinessDto>> addBusinessPhoto(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody AddPhotoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(businessService.addBusinessPhoto(user.getId(), id, request.getImageUrl()), "Photo added successfully"));
    }
}
