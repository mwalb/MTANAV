package tz.mtanav.job;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/me/job-applications")
public class CustomerJobApplicationController {

    private final JobService jobService;

    public CustomerJobApplicationController(JobService jobService) {
        this.jobService = jobService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplicationDto>>> getMyJobApplications(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(jobService.getMyApplications(userId), "My job applications retrieved"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<JobApplicationDto>> withdrawApplication(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(jobService.withdrawApplication(userId, id), "Application withdrawn"));
    }
}
