package tz.mtanav.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByBusinessId(Long businessId);

    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= :now AND p.expiryDate >= :now " +
           "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:offerType IS NULL OR LOWER(p.offerType) = LOWER(:offerType)) " +
           "AND (:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.isSponsored DESC, p.createdAt DESC")
    List<Promotion> findActiveEligiblePromotions(
            @Param("now") OffsetDateTime now,
            @Param("query") String query,
            @Param("category") String category,
            @Param("offerType") String offerType
    );
}
