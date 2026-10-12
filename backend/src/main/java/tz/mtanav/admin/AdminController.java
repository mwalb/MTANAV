package tz.mtanav.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.auth.UserDto;
import tz.mtanav.business.BusinessDto;
import tz.mtanav.business.VerificationStatus;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/businesses")
    public ResponseEntity<ApiResponse<List<BusinessDto>>> getBusinesses(
            @RequestParam(required = false) VerificationStatus status) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getBusinessesByStatus(status)));
    }

    @PutMapping("/businesses/{id}/verification")
    public ResponseEntity<ApiResponse<BusinessDto>> updateVerification(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.updateBusinessVerification(user.getId(), id, request.getStatus()),
                "Business verification status updated"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAuditLogs()));
    }

    @GetMapping("/users/admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAdmins() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAdminUsers()));
    }

    @PostMapping("/users/admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createAdmin(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.createAdminUser(user.getId(), request),
                "Admin account created successfully"));
    }

    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> deactivateUser(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                adminService.deactivateUser(user.getId(), id),
                "User account deactivated successfully"));
    }
}
