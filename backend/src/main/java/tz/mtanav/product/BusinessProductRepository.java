package tz.mtanav.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessProductRepository extends JpaRepository<BusinessProduct, Long> {
    List<BusinessProduct> findByBusinessId(Long businessId);
    
    Optional<BusinessProduct> findByBusinessIdAndProductId(Long businessId, Long productId);

    @Query("SELECT bp FROM BusinessProduct bp WHERE bp.product.id = :productId AND bp.availabilityStatus = 'IN_STOCK'")
    List<BusinessProduct> findAvailableByProductId(@Param("productId") Long productId);

    @Query("SELECT bp FROM BusinessProduct bp WHERE LOWER(bp.product.name) LIKE LOWER(CONCAT('%', :query, '%')) AND bp.availabilityStatus = 'IN_STOCK'")
    List<BusinessProduct> searchAvailableByProductName(@Param("query") String query);
}
