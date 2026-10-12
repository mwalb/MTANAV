package tz.mtanav.promotion;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/me/saved-promotions")
public class CustomerPromotionController {

    private final PromotionService promotionService;

    public CustomerPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getSavedPromotions(Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        return ResponseEntity.ok(ApiResponse.success(promotionService.getSavedPromotions(userId), "Saved promotions retrieved"));
    }
}
