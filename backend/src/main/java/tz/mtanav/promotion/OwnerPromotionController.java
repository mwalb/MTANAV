package tz.mtanav.promotion;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/owner/promotions")
public class OwnerPromotionController {

    private final PromotionService promotionService;

    public OwnerPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getOwnerPromotions(Authentication authentication) {
        Long userId = getUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        return ResponseEntity.ok(ApiResponse.success(promotionService.getOwnerPromotions(userId), "Owner promotions retrieved"));
    }
}
