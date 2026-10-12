package tz.mtanav.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessServiceRepository extends JpaRepository<BusinessServiceEntity, Long> {
    List<BusinessServiceEntity> findByBusinessId(Long businessId);

    @Query("SELECT bs FROM BusinessServiceEntity bs WHERE bs.service.id = :serviceId AND bs.available = true")
    List<BusinessServiceEntity> findAvailableByServiceId(@Param("serviceId") Long serviceId);
}
