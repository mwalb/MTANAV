package tz.mtanav.earning;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EarningLedgerRepository extends JpaRepository<EarningLedger, Long> {
    List<EarningLedger> findByUserId(Long userId);
}
