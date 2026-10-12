package tz.mtanav.creator;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/creator-campaigns")
public class CreatorController {

    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CreatorCampaign>>> getCampaigns() {
        return ResponseEntity.ok(ApiResponse.success(creatorService.getAllCampaigns(), "Campaigns retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreatorCampaign>> createCampaign(@RequestBody Map<String, Object> payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        CreatorCampaign c = creatorService.createCampaign(
                userId,
                payload.getOrDefault("title", "").toString(),
                new BigDecimal(payload.getOrDefault("budget", "100000").toString()),
                new BigDecimal(payload.getOrDefault("compensationAmount", "20000").toString()),
                payload.getOrDefault("requirements", "").toString()
        );
        return ResponseEntity.ok(ApiResponse.success(c, "Creator campaign created successfully"));
    }
}
