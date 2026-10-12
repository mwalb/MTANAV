package tz.mtanav.job;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final LocalJobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public JobService(LocalJobRepository jobRepository,
                      JobApplicationRepository applicationRepository,
                      BusinessRepository businessRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    public List<JobDto> getPublishedJobs(String query, String category, String employmentType, String location) {
        List<LocalJob> jobs = jobRepository.searchPublishedJobs(
                query != null && !query.isBlank() ? query.trim() : null,
                category != null && !category.isBlank() ? category.trim() : null,
                employmentType != null && !employmentType.isBlank() ? employmentType.trim() : null,
                location != null && !location.isBlank() ? location.trim() : null
        );
        return jobs.stream().map(this::toDto).collect(Collectors.toList());
    }

    public JobDto getJobById(Long id) {
        LocalJob job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job listing not found with ID: " + id));
        return toDto(job);
    }

    @Transactional
    public JobDto createJob(Long posterUserId, LocalJob jobPayload) {
        jobPayload.setPosterId(posterUserId);
        if (jobPayload.getPublicationStatus() == null) {
            jobPayload.setPublicationStatus("PUBLISHED");
        }
        if (jobPayload.getStatus() == null) {
            jobPayload.setStatus("OPEN");
        }
        if (jobPayload.getCurrency() == null) {
            jobPayload.setCurrency("TZS");
        }
        LocalJob saved = jobRepository.save(jobPayload);
        return toDto(saved);
    }

    @Transactional
    public JobDto updateJob(Long posterUserId, Long jobId, LocalJob jobPayload) {
        LocalJob existing = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!existing.getPosterId().equals(posterUserId)) {
            throw new RuntimeException("Unauthorized: You can only edit your own job listings");
        }

        if (jobPayload.getTitle() != null) existing.setTitle(jobPayload.getTitle());
        if (jobPayload.getCategory() != null) existing.setCategory(jobPayload.getCategory());
        if (jobPayload.getDescription() != null) existing.setDescription(jobPayload.getDescription());
        if (jobPayload.getLocation() != null) existing.setLocation(jobPayload.getLocation());
        if (jobPayload.getBudgetAmount() != null) existing.setBudgetAmount(jobPayload.getBudgetAmount());
        if (jobPayload.getEmploymentType() != null) existing.setEmploymentType(jobPayload.getEmploymentType());
        if (jobPayload.getPayType() != null) existing.setPayType(jobPayload.getPayType());
        if (jobPayload.getPayAmountMin() != null) existing.setPayAmountMin(jobPayload.getPayAmountMin());
        if (jobPayload.getPayAmountMax() != null) existing.setPayAmountMax(jobPayload.getPayAmountMax());
        if (jobPayload.getPayFrequency() != null) existing.setPayFrequency(jobPayload.getPayFrequency());
        if (jobPayload.getApplicationDeadline() != null) existing.setApplicationDeadline(jobPayload.getApplicationDeadline());
        if (jobPayload.getQualifications() != null) existing.setQualifications(jobPayload.getQualifications());
        if (jobPayload.getResponsibilities() != null) existing.setResponsibilities(jobPayload.getResponsibilities());
        if (jobPayload.getContactInstructions() != null) existing.setContactInstructions(jobPayload.getContactInstructions());
        if (jobPayload.getPublicationStatus() != null) existing.setPublicationStatus(jobPayload.getPublicationStatus());
        if (jobPayload.getStatus() != null) existing.setStatus(jobPayload.getStatus());

        existing.setUpdatedAt(OffsetDateTime.now());
        return toDto(jobRepository.save(existing));
    }

    @Transactional
    public JobDto updateJobPublicationStatus(Long posterUserId, Long jobId, String newPublicationStatus) {
        LocalJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (!job.getPosterId().equals(posterUserId)) {
            throw new RuntimeException("Unauthorized: You can only update status for your own job listings");
        }
        job.setPublicationStatus(newPublicationStatus.toUpperCase());
        job.setUpdatedAt(OffsetDateTime.now());
        return toDto(jobRepository.save(job));
    }

    public List<JobDto> getOwnerJobs(Long posterUserId) {
        return jobRepository.findByPosterId(posterUserId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationDto applyForJob(Long userId, Long jobId, BigDecimal proposedRate, String coverNote) {
        LocalJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!"PUBLISHED".equalsIgnoreCase(job.getPublicationStatus()) || "CLOSED".equalsIgnoreCase(job.getStatus())) {
            throw new RuntimeException("This job listing is no longer open for applications");
        }

        // Prevent duplicate active applications
        List<String> inactiveStatuses = Arrays.asList("WITHDRAWN", "REJECTED");
        if (applicationRepository.existsByJobIdAndProviderIdAndStatusNotIn(jobId, userId, inactiveStatuses)) {
            throw new RuntimeException("You have already submitted an active application for this job");
        }

        User user = userRepository.findById(userId).orElse(null);

        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setProviderId(userId);
        application.setProposedRate(proposedRate != null ? proposedRate : job.getBudgetAmount());
        application.setCoverNote(coverNote);
        application.setStatus("SUBMITTED");

        if (user != null) {
            application.setApplicantName(user.getFullName());
            application.setApplicantEmail(user.getEmail());
            application.setApplicantPhone(user.getPhoneNumber());
        }

        JobApplication saved = applicationRepository.save(application);
        return toApplicationDto(saved, job.getTitle());
    }

    public List<JobApplicationDto> getMyApplications(Long userId) {
        return applicationRepository.findByProviderId(userId).stream()
                .map(app -> {
                    String title = jobRepository.findById(app.getJobId()).map(LocalJob::getTitle).orElse("Job #" + app.getJobId());
                    return toApplicationDto(app, title);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationDto withdrawApplication(Long userId, Long applicationId) {
        JobApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getProviderId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only withdraw your own applications");
        }

        app.setStatus("WITHDRAWN");
        app.setUpdatedAt(OffsetDateTime.now());
        JobApplication saved = applicationRepository.save(app);
        String title = jobRepository.findById(app.getJobId()).map(LocalJob::getTitle).orElse("Job #" + app.getJobId());
        return toApplicationDto(saved, title);
    }

    public List<JobApplicationDto> getApplicationsForJob(Long posterUserId, Long jobId) {
        LocalJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPosterId().equals(posterUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this job listing");
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(app -> toApplicationDto(app, job.getTitle()))
                .collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationDto updateApplicationStatus(Long posterUserId, Long jobId, Long applicationId, String newStatus) {
        LocalJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPosterId().equals(posterUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this job listing");
        }

        JobApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getJobId().equals(jobId)) {
            throw new RuntimeException("Application does not match specified job");
        }

        app.setStatus(newStatus.toUpperCase());
        app.setUpdatedAt(OffsetDateTime.now());
        JobApplication saved = applicationRepository.save(app);
        return toApplicationDto(saved, job.getTitle());
    }

    public List<String> getJobCategories() {
        return Arrays.asList(
                "Delivery and logistics",
                "Sales and marketing",
                "Technology and IT",
                "Repairs and technical work",
                "Construction and trades",
                "Hospitality and food services",
                "Cleaning and household services",
                "Retail and shop assistance",
                "Office and administrative work",
                "Temporary and casual work"
        );
    }

    private JobDto toDto(LocalJob job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCategory(job.getCategory());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setBudgetAmount(job.getBudgetAmount());
        dto.setCurrency(job.getCurrency());
        dto.setStatus(job.getStatus());
        dto.setPublicationStatus(job.getPublicationStatus());
        dto.setPosterId(job.getPosterId());
        dto.setBusinessId(job.getBusinessId());
        if (job.getBusinessId() != null) {
            businessRepository.findById(job.getBusinessId())
                    .ifPresent(b -> dto.setBusinessName(b.getName()));
        }
        dto.setEmploymentType(job.getEmploymentType());
        dto.setPayType(job.getPayType());
        dto.setPayAmountMin(job.getPayAmountMin());
        dto.setPayAmountMax(job.getPayAmountMax());
        dto.setPayFrequency(job.getPayFrequency());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setQualifications(job.getQualifications());
        dto.setResponsibilities(job.getResponsibilities());
        dto.setContactInstructions(job.getContactInstructions());
        dto.setCreatedAt(job.getCreatedAt());
        return dto;
    }

    private JobApplicationDto toApplicationDto(JobApplication app, String jobTitle) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJobId());
        dto.setJobTitle(jobTitle);
        dto.setProviderId(app.getProviderId());
        dto.setApplicantName(app.getApplicantName());
        dto.setApplicantEmail(app.getApplicantEmail());
        dto.setApplicantPhone(app.getApplicantPhone());
        dto.setProposedRate(app.getProposedRate());
        dto.setCoverNote(app.getCoverNote());
        dto.setStatus(app.getStatus());
        dto.setCreatedAt(app.getCreatedAt());
        return dto;
    }
}
