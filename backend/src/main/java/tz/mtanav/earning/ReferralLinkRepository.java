package tz.mtanav.earning;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReferralLinkRepository extends JpaRepository<ReferralLink, Long> {
    List<ReferralLink> findByUserId(Long userId);
    Optional<ReferralLink> findByCode(String code);
}
