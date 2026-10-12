package tz.mtanav.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.product.*;
import tz.mtanav.user.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BusinessFilteringIntegrationTest {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BusinessProductRepository businessProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Product spoon;
    private Product rice;
    private Business shopA;
    private Business shopB;
    private Business shopC;

    @BeforeEach
    void setUp() {
        // Clean setup for test isolation
        businessProductRepository.deleteAll();
        businessRepository.deleteAll();
        productRepository.deleteAll();

        // Create test user/owner
        User owner = userRepository.findByEmail("owner1@mtanav.tz").orElseGet(() -> {
            Role role = roleRepository.findByName(RoleName.ROLE_BUSINESS_OWNER)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_BUSINESS_OWNER).build()));
            return userRepository.save(User.builder()
                    .email("owner1@mtanav.tz")
                    .password("password")
                    .fullName("Test Owner")
                    .roles(java.util.Set.of(role))
                    .build());
        });

        // Create products
        spoon = productRepository.save(Product.builder()
                .name("Spoon")
                .quickProduct(true)
                .quickProductIcon("🥄")
                .description("Kitchen Spoon")
                .build());

        rice = productRepository.save(Product.builder()
                .name("Rice")
                .quickProduct(true)
                .quickProductIcon("🍚")
                .description("Super Rice")
                .build());

        // Create Shop A (sells Spoon)
        shopA = businessRepository.save(Business.builder()
                .owner(owner)
                .name("Shop A (Spoon Seller)")
                .verificationStatus(VerificationStatus.VERIFIED)
                .latitude(-6.8182)
                .longitude(39.2778)
                .address("Kariakoo")
                .city("Dar es Salaam")
                .build());

        // Create Shop B (sells Rice)
        shopB = businessRepository.save(Business.builder()
                .owner(owner)
                .name("Shop B (Rice Seller)")
                .verificationStatus(VerificationStatus.VERIFIED)
                .latitude(-6.7725)
                .longitude(39.2215)
                .address("Mlimani")
                .city("Dar es Salaam")
                .build());

        // Create Shop C (sells Spoon)
        shopC = businessRepository.save(Business.builder()
                .owner(owner)
                .name("Shop C (Spoon & Furniture Seller)")
                .verificationStatus(VerificationStatus.VERIFIED)
                .latitude(-6.7845)
                .longitude(39.2280)
                .address("Sinza")
                .city("Dar es Salaam")
                .build());

        // Associate Shop A -> Spoon
        businessProductRepository.save(BusinessProduct.builder()
                .business(shopA)
                .product(spoon)
                .price(BigDecimal.valueOf(3000))
                .availabilityStatus(ProductAvailability.IN_STOCK)
                .build());

        // Associate Shop B -> Rice
        businessProductRepository.save(BusinessProduct.builder()
                .business(shopB)
                .product(rice)
                .price(BigDecimal.valueOf(3500))
                .availabilityStatus(ProductAvailability.IN_STOCK)
                .build());

        // Associate Shop C -> Spoon
        businessProductRepository.save(BusinessProduct.builder()
                .business(shopC)
                .product(spoon)
                .price(BigDecimal.valueOf(3500))
                .availabilityStatus(ProductAvailability.IN_STOCK)
                .build());
    }

    @Test
    @DisplayName("STRICT FILTERING: Searching for Spoon returns ONLY Shop A and Shop C, excluding Shop B")
    void testFilteringByProductSpoon() {
        // When searching for Spoon
        List<BusinessMapDto> results = businessService.getMapBusinesses(spoon.getId(), null, null);

        // Then
        List<String> returnedShopNames = results.stream().map(BusinessMapDto::getName).toList();

        assertThat(results).hasSize(2);
        assertThat(returnedShopNames).contains("Shop A (Spoon Seller)", "Shop C (Spoon & Furniture Seller)");
        assertThat(returnedShopNames).doesNotContain("Shop B (Rice Seller)");
    }

    @Test
    @DisplayName("STRICT FILTERING: Searching for Rice returns ONLY Shop B")
    void testFilteringByProductRice() {
        // When searching for Rice
        List<BusinessMapDto> results = businessService.getMapBusinesses(rice.getId(), null, null);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Shop B (Rice Seller)");
    }
}
