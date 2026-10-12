package tz.mtanav.job;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobId(Long jobId);
    List<JobApplication> findByProviderId(Long providerId);
    Optional<JobApplication> findByJobIdAndProviderId(Long jobId, Long providerId);
    boolean existsByJobIdAndProviderIdAndStatusNotIn(Long jobId, Long providerId, List<String> inactiveStatuses);
}
