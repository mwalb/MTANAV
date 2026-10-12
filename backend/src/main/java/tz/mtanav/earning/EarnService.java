package tz.mtanav.earning;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class EarnService {

    private final ReferralLinkRepository referralLinkRepository;
    private final CommissionRepository commissionRepository;
    private final EarningLedgerRepository earningLedgerRepository;

    public EarnService(
            ReferralLinkRepository referralLinkRepository,
            CommissionRepository commissionRepository,
            EarningLedgerRepository earningLedgerRepository
    ) {
        this.referralLinkRepository = referralLinkRepository;
        this.commissionRepository = commissionRepository;
        this.earningLedgerRepository = earningLedgerRepository;
    }

    public List<ReferralLink> getReferralLinks(Long userId) {
        List<ReferralLink> links = referralLinkRepository.findByUserId(userId);
        if (links.isEmpty()) {
            // Generate default link
            ReferralLink link = new ReferralLink();
            link.setUserId(userId);
            link.setCode("MTN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            link.setTargetType("GENERAL");
            link.setClicksCount(14);
            link.setConversionsCount(3);
            links.add(referralLinkRepository.save(link));
        }
        return links;
    }

    public ReferralLink createReferralLink(Long userId, String targetType, Long targetId) {
        ReferralLink link = new ReferralLink();
        link.setUserId(userId);
        link.setCode("MTN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        link.setTargetType(targetType != null ? targetType : "GENERAL");
        link.setTargetId(targetId);
        return referralLinkRepository.save(link);
    }

    public List<Commission> getCommissions(Long userId) {
        List<Commission> commissions = commissionRepository.findByUserId(userId);
        if (commissions.isEmpty()) {
            // Seed sample commission for demonstration
            Commission c = new Commission();
            c.setUserId(userId);
            c.setAmount(new BigDecimal("1500.00"));
            c.setCurrency("TZS");
            c.setStatus("AVAILABLE");
            commissions.add(commissionRepository.save(c));
        }
        return commissions;
    }

    public BigDecimal getAvailableBalance(Long userId) {
        List<Commission> available = commissionRepository.findByUserIdAndStatus(userId, "AVAILABLE");
        return available.stream().map(Commission::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
