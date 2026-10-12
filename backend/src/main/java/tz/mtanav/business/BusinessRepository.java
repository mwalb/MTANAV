package tz.mtanav.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    List<Business> findByOwnerId(Long ownerId);

    List<Business> findByVerificationStatus(VerificationStatus status);

    // Filter Businesses selling a specific product ID (ONLY returning businesses selling that product)
    @Query("SELECT DISTINCT bp.business FROM BusinessProduct bp WHERE bp.product.id = :productId AND bp.availabilityStatus = 'IN_STOCK' AND bp.business.verificationStatus = 'VERIFIED'")
    List<Business> findBusinessesSellingProduct(@Param("productId") Long productId);

    // Filter Businesses selling product matching name (e.g. Spoon)
    @Query("SELECT DISTINCT bp.business FROM BusinessProduct bp WHERE LOWER(bp.product.name) = LOWER(:productName) AND bp.availabilityStatus = 'IN_STOCK' AND bp.business.verificationStatus = 'VERIFIED'")
    List<Business> findBusinessesSellingProductName(@Param("productName") String productName);

    // Filter Businesses providing a specific service ID
    @Query("SELECT DISTINCT bs.business FROM BusinessServiceEntity bs WHERE bs.service.id = :serviceId AND bs.available = true AND bs.business.verificationStatus = 'VERIFIED'")
    List<Business> findBusinessesProvidingService(@Param("serviceId") Long serviceId);

    // Filter Businesses providing service matching name
    @Query("SELECT DISTINCT bs.business FROM BusinessServiceEntity bs WHERE LOWER(bs.service.name) = LOWER(:serviceName) AND bs.available = true AND bs.business.verificationStatus = 'VERIFIED'")
    List<Business> findBusinessesProvidingServiceName(@Param("serviceName") String serviceName);

    // General text search for businesses, products, or services
    @Query("SELECT DISTINCT b FROM Business b " +
           "LEFT JOIN BusinessProduct bp ON bp.business.id = b.id " +
           "LEFT JOIN BusinessServiceEntity bs ON bs.business.id = b.id " +
           "WHERE (LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR LOWER(bp.product.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR LOWER(bs.service.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "  AND b.verificationStatus = 'VERIFIED'")
    List<Business> searchBusinesses(@Param("query") String query);
}
