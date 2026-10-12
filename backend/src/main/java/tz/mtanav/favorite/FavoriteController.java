package tz.mtanav.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteDto>> addFavorite(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) Long businessId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long serviceId) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.addFavorite(user.getId(), businessId, productId, serviceId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteDto>>> getMyFavorites(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.getUserFavorites(user.getId())));
    }

    @DeleteMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<Void>> removeBusinessFavorite(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long businessId) {
        favoriteService.removeBusinessFavorite(user.getId(), businessId);
        return ResponseEntity.ok(ApiResponse.success(null, "Favorite removed"));
    }
}
