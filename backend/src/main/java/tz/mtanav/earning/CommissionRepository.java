package tz.mtanav.earning;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByUserId(Long userId);
    List<Commission> findByUserIdAndStatus(Long userId, String status);
}
