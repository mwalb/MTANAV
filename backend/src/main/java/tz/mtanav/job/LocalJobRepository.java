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
           "AND (:category IS NULL OR LOWER(j.category) = LOWER(:category) " +
           "     OR (LOWER(:category) LIKE '%delivery%' AND (LOWER(j.category) LIKE '%delivery%' OR LOWER(j.category) LIKE '%logistics%')) " +
           "     OR (LOWER(:category) LIKE '%tech%' AND (LOWER(j.category) LIKE '%tech%' OR LOWER(j.category) LIKE '%it%' OR LOWER(j.category) LIKE '%repair%')) " +
           "     OR (LOWER(:category) LIKE '%market%' AND (LOWER(j.category) LIKE '%market%' OR LOWER(j.category) LIKE '%sales%')) " +
           "     OR (LOWER(:category) LIKE '%sales%' AND (LOWER(j.category) LIKE '%sales%' OR LOWER(j.category) LIKE '%retail%')) " +
           "     OR (LOWER(:category) LIKE '%hospit%' AND (LOWER(j.category) LIKE '%hospit%' OR LOWER(j.category) LIKE '%food%'))) " +
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
