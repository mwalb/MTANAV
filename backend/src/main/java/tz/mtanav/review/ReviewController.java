package tz.mtanav.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.createReview(user.getId(), request), "Review submitted successfully"));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getBusinessReviews(@PathVariable Long businessId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getBusinessReviews(businessId)));
    }
}
