package tz.mtanav.earning;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earn")
public class EarnController {

    private final EarnService earnService;

    public EarnController(EarnService earnService) {
        this.earnService = earnService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L; // fallback demo user
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(Authentication authentication) {
        Long userId = getUserId(authentication);
        BigDecimal availableBalance = earnService.getAvailableBalance(userId);
        List<ReferralLink> links = earnService.getReferralLinks(userId);
        List<Commission> commissions = earnService.getCommissions(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("availableBalance", availableBalance);
        data.put("pendingEarnings", new BigDecimal("4500.00"));
        data.put("totalEarned", availableBalance.add(new BigDecimal("4500.00")));
        data.put("reversedCommissions", BigDecimal.ZERO);
        data.put("referralLinks", links);
        data.put("commissions", commissions);
        data.put("conversionRate", "14.2%");

        return ResponseEntity.ok(ApiResponse.success(data, "Earn dashboard retrieved successfully"));
    }

    @GetMapping("/links")
    public ResponseEntity<ApiResponse<List<ReferralLink>>> getLinks(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(earnService.getReferralLinks(userId), "Referral links retrieved"));
    }

    @PostMapping("/links")
    public ResponseEntity<ApiResponse<ReferralLink>> createLink(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        ReferralLink link = earnService.createReferralLink(userId, targetType, targetId);
        return ResponseEntity.ok(ApiResponse.success(link, "Referral link created successfully"));
    }

    @GetMapping("/commissions")
    public ResponseEntity<ApiResponse<List<Commission>>> getCommissions(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(earnService.getCommissions(userId), "Commissions retrieved"));
    }
}
