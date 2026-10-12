package tz.mtanav.creator;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CreatorService {

    private final CreatorCampaignRepository campaignRepository;

    public CreatorService(CreatorCampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<CreatorCampaign> getAllCampaigns() {
        List<CreatorCampaign> list = campaignRepository.findAll();
        if (list.isEmpty()) {
            CreatorCampaign c = new CreatorCampaign();
            c.setTitle("Kariakoo Market Product Showcase");
            c.setBrandOwnerId(2L);
            c.setBudget(new BigDecimal("500000.00"));
            c.setCompensationAmount(new BigDecimal("50000.00"));
            c.setRequirements("Create a 30s video review showcasing cookware items available at Kariakoo.");
            list.add(campaignRepository.save(c));
        }
        return list;
    }

    public CreatorCampaign createCampaign(Long userId, String title, BigDecimal budget, BigDecimal compensationAmount, String requirements) {
        CreatorCampaign c = new CreatorCampaign();
        c.setTitle(title);
        c.setBrandOwnerId(userId);
        c.setBudget(budget);
        c.setCompensationAmount(compensationAmount);
        c.setRequirements(requirements);
        c.setStatus("ACTIVE");
        return campaignRepository.save(c);
    }
}
