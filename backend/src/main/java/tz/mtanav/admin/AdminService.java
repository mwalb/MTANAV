package tz.mtanav.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.auth.AuthService;
import tz.mtanav.auth.UserDto;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessDto;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.business.BusinessService;
import tz.mtanav.business.VerificationStatus;
import tz.mtanav.user.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BusinessRepository businessRepository;
    private final BusinessService businessService;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Transactional
    public BusinessDto updateBusinessVerification(Long adminUserId, Long businessId, VerificationStatus status) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        business.setVerificationStatus(status);
        Business saved = businessRepository.save(business);

        auditLogRepository.save(AuditLog.builder()
                .userId(adminUserId)
                .action("VERIFICATION_UPDATE")
                .entity("BUSINESS")
                .entityId(businessId)
                .details("Updated status to " + status.name())
                .build());

        return businessService.mapToBusinessDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BusinessDto> getBusinessesByStatus(VerificationStatus status) {
        if (status == null) {
            return businessRepository.findAll().stream()
                    .map(businessService::mapToBusinessDto)
                    .collect(Collectors.toList());
        }
        return businessRepository.findByVerificationStatus(status).stream()
                .map(businessService::mapToBusinessDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAdminUsers() {
        return userRepository.findByRolesName(RoleName.ROLE_ADMIN).stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createAdminUser(Long currentUserId, CreateAdminRequest request) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isSuperAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_SUPER_ADMIN);

        if (!isSuperAdmin) {
            throw new SecurityException("Only SUPER_ADMIN can create admin accounts");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User adminUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .active(true)
                .roles(roles)
                .build();

        User saved = userRepository.save(adminUser);

        auditLogRepository.save(AuditLog.builder()
                .userId(currentUserId)
                .action("CREATE_ADMIN")
                .entity("USER")
                .entityId(saved.getId())
                .details("Created ADMIN account: " + saved.getEmail())
                .build());

        return authService.mapToUserDto(saved);
    }

    @Transactional
    public UserDto deactivateUser(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isSuperAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_SUPER_ADMIN);

        if (!isSuperAdmin) {
            throw new SecurityException("Only SUPER_ADMIN can deactivate user accounts");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        boolean targetIsSuperAdmin = targetUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_SUPER_ADMIN);

        if (targetIsSuperAdmin) {
            throw new SecurityException("Cannot deactivate a SUPER_ADMIN account");
        }

        targetUser.setActive(false);
        User saved = userRepository.save(targetUser);

        auditLogRepository.save(AuditLog.builder()
                .userId(currentUserId)
                .action("DEACTIVATE_USER")
                .entity("USER")
                .entityId(saved.getId())
                .details("Deactivated user account: " + saved.getEmail())
                .build());

        return authService.mapToUserDto(saved);
    }
}
