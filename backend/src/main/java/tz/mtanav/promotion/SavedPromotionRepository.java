package tz.mtanav.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedPromotionRepository extends JpaRepository<SavedPromotion, Long> {
    List<SavedPromotion> findByUserId(Long userId);
    Optional<SavedPromotion> findByUserIdAndPromotionId(Long userId, Long promotionId);
    boolean existsByUserIdAndPromotionId(Long userId, Long promotionId);
    void deleteByUserIdAndPromotionId(Long userId, Long promotionId);
}
