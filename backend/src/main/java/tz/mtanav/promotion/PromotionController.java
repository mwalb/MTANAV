package tz.mtanav.promotion;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getActivePromotions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String offerType,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        List<PromotionDto> list = promotionService.getActivePromotions(query, category, offerType, userId);
        return ResponseEntity.ok(ApiResponse.success(list, "Active promotions retrieved"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(promotionService.getPromotionCategories(), "Promotion categories retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionDto>> getPromotionById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(promotionService.getPromotionById(id, userId), "Promotion details retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionDto>> createPromotion(@RequestBody Promotion payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        PromotionDto created = promotionService.createPromotion(userId, payload);
        return ResponseEntity.ok(ApiResponse.success(created, "Promotion created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionDto>> updatePromotion(@PathVariable Long id, @RequestBody Promotion payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        PromotionDto updated = promotionService.updatePromotion(userId, id, payload);
        return ResponseEntity.ok(ApiResponse.success(updated, "Promotion updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PromotionDto>> updatePromotionStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        String status = body.getOrDefault("status", "ACTIVE");
        PromotionDto updated = promotionService.updatePromotionStatus(userId, id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Promotion status updated"));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<ApiResponse<String>> savePromotion(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        promotionService.savePromotion(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Saved", "Promotion saved to favorites"));
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<ApiResponse<String>> unsavePromotion(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        promotionService.unsavePromotion(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Removed", "Promotion removed from favorites"));
    }
}
