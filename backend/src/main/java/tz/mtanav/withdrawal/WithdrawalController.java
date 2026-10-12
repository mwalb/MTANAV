package tz.mtanav.withdrawal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L;
    }

    @GetMapping("/destinations")
    public ResponseEntity<ApiResponse<List<WithdrawalDestination>>> getDestinations(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(withdrawalService.getDestinations(userId), "Destinations retrieved"));
    }

    @PostMapping("/destinations")
    public ResponseEntity<ApiResponse<WithdrawalDestination>> addDestination(@RequestBody Map<String, String> payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        WithdrawalDestination d = withdrawalService.addDestination(
                userId,
                payload.getOrDefault("providerName", "M_PESA"),
                payload.getOrDefault("accountNumber", ""),
                payload.getOrDefault("accountHolderName", "")
        );
        return ResponseEntity.ok(ApiResponse.success(d, "Withdrawal destination added"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WithdrawalRequest>>> getRequests(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(withdrawalService.getRequests(userId), "Withdrawal requests retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WithdrawalRequest>> requestWithdrawal(@RequestBody Map<String, Object> payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        Long destinationId = Long.valueOf(payload.getOrDefault("destinationId", 1).toString());
        BigDecimal amount = new BigDecimal(payload.getOrDefault("amount", "10000").toString());
        WithdrawalRequest req = withdrawalService.requestWithdrawal(userId, destinationId, amount);
        return ResponseEntity.ok(ApiResponse.success(req, "Withdrawal requested successfully"));
    }
}
