package tz.mtanav.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.business.VerificationStatus;
import tz.mtanav.promotion.Promotion;
import tz.mtanav.promotion.PromotionDto;
import tz.mtanav.promotion.PromotionService;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class JobAndPromotionFilteringTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private LocalJobRepository jobRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private User poster;
    private Business business;

    @BeforeEach
    void setUp() {
        poster = new User();
        poster.setEmail("poster@test.com");
        poster.setFullName("Poster User");
        poster.setPassword("hashedpass");
        poster.setPhoneNumber("+255700555666");
        poster = userRepository.save(poster);

        business = new Business();
        business.setName("Test Cargo & Logistics");
        business.setOwner(poster);
        business.setAddress("Kariakoo");
        business.setCity("Dar es Salaam");
        business.setLatitude(-6.8182);
        business.setLongitude(39.2778);
        business.setVerificationStatus(VerificationStatus.VERIFIED);
        business = businessRepository.save(business);

        // Seed test jobs
        LocalJob job1 = new LocalJob();
        job1.setTitle("Delivery Courier Rider");
        job1.setCategory("Delivery and logistics");
        job1.setDescription("Deliver parcels in Dar es Salaam");
        job1.setLocation("Kariakoo, Dar es Salaam");
        job1.setBudgetAmount(new BigDecimal("350000.00"));
        job1.setEmploymentType("PART_TIME");
        job1.setPublicationStatus("PUBLISHED");
        job1.setStatus("OPEN");
        job1.setPosterId(poster.getId());
        job1.setBusinessId(business.getId());
        jobRepository.save(job1);

        LocalJob job2 = new LocalJob();
        job2.setTitle("Full Stack Software Developer");
        job2.setCategory("Technology and IT");
        job2.setDescription("Build mobile app features in Kotlin");
        job2.setLocation("Posta, Dar es Salaam");
        job2.setBudgetAmount(new BigDecimal("1200000.00"));
        job2.setEmploymentType("FULL_TIME");
        job2.setPublicationStatus("PUBLISHED");
        job2.setStatus("OPEN");
        job2.setPosterId(poster.getId());
        job2.setBusinessId(business.getId());
        jobRepository.save(job2);

        LocalJob job3 = new LocalJob();
        job3.setTitle("Full Time Delivery Van Driver");
        job3.setCategory("Delivery and logistics");
        job3.setDescription("Heavy cargo van driver");
        job3.setLocation("Ubungo, Dar es Salaam");
        job3.setBudgetAmount(new BigDecimal("500000.00"));
        job3.setEmploymentType("FULL_TIME");
        job3.setPublicationStatus("PUBLISHED");
        job3.setStatus("OPEN");
        job3.setPosterId(poster.getId());
        job3.setBusinessId(business.getId());
        jobRepository.save(job3);

        // Seed test promotion
        Promotion promo = new Promotion();
        promo.setBusinessId(business.getId());
        promo.setTitle("Express Delivery 20% Discount");
        promo.setDescription("Get 20% off express parcel delivery this week!");
        promo.setCategory("Services & Repairs");
        promo.setOriginalPrice(new BigDecimal("15000.00"));
        promo.setPromotionalPrice(new BigDecimal("12000.00"));
        promo.setOfferType("DISCOUNT");
        promo.setStatus("ACTIVE");
        promo.setStartDate(OffsetDateTime.now().minusDays(1));
        promo.setExpiryDate(OffsetDateTime.now().plusDays(10));
        promotionService.createPromotion(poster.getId(), promo);
    }

    @Test
    void testJobCategoryFiltering() {
        // Test filtering by Delivery & Logistics
        List<JobDto> deliveryJobs = jobService.getPublishedJobs(null, "Delivery & Logistics", null, null);
        assertFalse(deliveryJobs.isEmpty());
        assertTrue(deliveryJobs.stream().allMatch(j -> j.getCategory().toLowerCase().contains("delivery") || j.getCategory().toLowerCase().contains("logistics")));

        // Test filtering by Technology
        List<JobDto> techJobs = jobService.getPublishedJobs(null, "Technology", null, null);
        assertFalse(techJobs.isEmpty());
        assertTrue(techJobs.stream().allMatch(j -> j.getCategory().toLowerCase().contains("technology") || j.getCategory().toLowerCase().contains("it")));
    }

    @Test
    void testJobTypeFiltering() {
        // Test filtering by PART_TIME
        List<JobDto> partTimeJobs = jobService.getPublishedJobs(null, null, "PART_TIME", null);
        assertEquals(1, partTimeJobs.size());
        assertEquals("Delivery Courier Rider", partTimeJobs.get(0).getTitle());

        // Test filtering by FULL_TIME
        List<JobDto> fullTimeJobs = jobService.getPublishedJobs(null, null, "FULL_TIME", null);
        assertEquals(2, fullTimeJobs.size());
    }

    @Test
    void testCombinedCategoryAndJobTypeFiltering() {
        // Category = Delivery & Logistics AND JobType = PART_TIME -> only 1 result (Rider)
        List<JobDto> combined = jobService.getPublishedJobs(null, "Delivery & Logistics", "PART_TIME", null);
        assertEquals(1, combined.size());
        assertEquals("Delivery Courier Rider", combined.get(0).getTitle());
    }

    @Test
    void testPromotionCategoryFiltering() {
        List<PromotionDto> promos = promotionService.getActivePromotions(null, "Services & Repairs", null, null);
        assertFalse(promos.isEmpty());
        assertEquals("Express Delivery 20% Discount", promos.get(0).getTitle());
    }
}
