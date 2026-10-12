package tz.mtanav.reward;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RewardService {

    private final CommunityRewardRepository rewardRepository;

    public RewardService(CommunityRewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public List<CommunityReward> getRewards(Long userId) {
        List<CommunityReward> list = rewardRepository.findByReporterId(userId);
        if (list.isEmpty()) {
            CommunityReward r = new CommunityReward();
            r.setReporterId(userId);
            r.setActivityType("CLOSED_BUSINESS");
            r.setTargetEntity("Demo Shop Kariakoo");
            r.setDescription("Reported shop relocation to new building.");
            r.setRewardAmount(new BigDecimal("5000.00"));
            r.setStatus("APPROVED");
            list.add(rewardRepository.save(r));
        }
        return list;
    }

    public CommunityReward submitRewardClaim(Long userId, String activityType, String targetEntity, String description) {
        CommunityReward r = new CommunityReward();
        r.setReporterId(userId);
        r.setActivityType(activityType);
        r.setTargetEntity(targetEntity);
        r.setDescription(description);
        r.setRewardAmount(new BigDecimal("5000.00"));
        r.setStatus("PENDING");
        return rewardRepository.save(r);
    }
}
