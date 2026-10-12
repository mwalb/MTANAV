package tz.mtanav.reward;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community-rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityReward>>> getRewards(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(rewardService.getRewards(userId), "Community rewards retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommunityReward>> submitReward(@RequestBody Map<String, String> payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        CommunityReward r = rewardService.submitRewardClaim(
                userId,
                payload.getOrDefault("activityType", "CLOSED_BUSINESS"),
                payload.getOrDefault("targetEntity", ""),
                payload.getOrDefault("description", "")
        );
        return ResponseEntity.ok(ApiResponse.success(r, "Community reward claim submitted successfully"));
    }
}
