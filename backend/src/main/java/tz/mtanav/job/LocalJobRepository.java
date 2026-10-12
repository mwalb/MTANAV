package tz.mtanav.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalJobRepository extends JpaRepository<LocalJob, Long> {

    List<LocalJob> findByPublicationStatus(String publicationStatus);

    List<LocalJob> findByPosterId(Long posterId);

    List<LocalJob> findByBusinessId(Long businessId);

    @Query("SELECT j FROM LocalJob j WHERE j.publicationStatus = 'PUBLISHED' " +
           "AND (:category IS NULL OR LOWER(j.category) = LOWER(:category)) " +
           "AND (:employmentType IS NULL OR LOWER(j.employmentType) = LOWER(:employmentType)) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:query IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(j.qualifications) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY j.createdAt DESC")
    List<LocalJob> searchPublishedJobs(
            @Param("query") String query,
            @Param("category") String category,
            @Param("employmentType") String employmentType,
            @Param("location") String location
    );
}
