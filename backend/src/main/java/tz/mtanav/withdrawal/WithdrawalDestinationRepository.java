package tz.mtanav.withdrawal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawalDestinationRepository extends JpaRepository<WithdrawalDestination, Long> {
    List<WithdrawalDestination> findByUserId(Long userId);
}
