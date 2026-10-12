package tz.mtanav.job;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    private Long getUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) auth.getPrincipal()).getId();
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDto>>> getJobs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String location) {
        List<JobDto> jobs = jobService.getPublishedJobs(query, category, employmentType, location);
        return ResponseEntity.ok(ApiResponse.success(jobs, "Jobs retrieved successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobCategories(), "Job categories retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDto>> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(id), "Job details retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobDto>> createJob(@RequestBody LocalJob jobPayload, Authentication authentication) {
        Long userId = getUserId(authentication);
        JobDto created = jobService.createJob(userId, jobPayload);
        return ResponseEntity.ok(ApiResponse.success(created, "Job posted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDto>> updateJob(@PathVariable Long id, @RequestBody LocalJob jobPayload, Authentication authentication) {
        Long userId = getUserId(authentication);
        JobDto updated = jobService.updateJob(userId, id, jobPayload);
        return ResponseEntity.ok(ApiResponse.success(updated, "Job updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobDto>> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        Long userId = getUserId(authentication);
        String status = body.getOrDefault("status", "PUBLISHED");
        JobDto updated = jobService.updateJobPublicationStatus(userId, id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Job status updated successfully"));
    }

    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<List<JobDto>>> getOwnerJobs(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(jobService.getOwnerJobs(userId), "Owner jobs retrieved"));
    }

    @PostMapping("/{id}/applications")
    public ResponseEntity<ApiResponse<JobApplicationDto>> applyForJob(@PathVariable Long id, @RequestBody Map<String, Object> payload, Authentication authentication) {
        Long userId = getUserId(authentication);
        BigDecimal proposedRate = payload.get("proposedRate") != null ? new BigDecimal(payload.get("proposedRate").toString()) : null;
        String coverNote = payload.getOrDefault("coverNote", "").toString();
        JobApplicationDto app = jobService.applyForJob(userId, id, proposedRate, coverNote);
        return ResponseEntity.ok(ApiResponse.success(app, "Application submitted successfully"));
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<ApiResponse<List<JobApplicationDto>>> getJobApplications(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(jobService.getApplicationsForJob(userId, id), "Applications retrieved"));
    }

    @PatchMapping("/{jobId}/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<JobApplicationDto>> updateApplicationStatus(
            @PathVariable Long jobId,
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        String status = body.getOrDefault("status", "UNDER_REVIEW");
        JobApplicationDto updated = jobService.updateApplicationStatus(userId, jobId, applicationId, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Application status updated successfully"));
    }
}
