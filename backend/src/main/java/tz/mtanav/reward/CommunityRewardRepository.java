package tz.mtanav.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityRewardRepository extends JpaRepository<CommunityReward, Long> {
    List<CommunityReward> findByReporterId(Long reporterId);
}
