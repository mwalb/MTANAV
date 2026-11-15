package tz.mtanav.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByCustomerIdAndBusinessId(Long customerId, Long businessId);

    @Query("SELECT c FROM Conversation c WHERE c.customerId = :userId OR c.shopOwnerId = :userId ORDER BY c.lastMessageAt DESC")
    List<Conversation> findAllForUser(@Param("userId") Long userId);

    List<Conversation> findByCustomerIdOrderByLastMessageAtDesc(Long customerId);

    List<Conversation> findByShopOwnerIdOrderByLastMessageAtDesc(Long shopOwnerId);
}
