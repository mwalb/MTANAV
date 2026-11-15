package tz.mtanav.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.business.VerificationStatus;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MessagingIntegrationTest {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private User customer;
    private User owner;
    private User unauthorizedUser;
    private Business business;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setEmail("customer@test.com");
        customer.setFullName("Customer User");
        customer.setPassword("hashedpass");
        customer.setPhoneNumber("+255700111222");
        customer = userRepository.save(customer);

        owner = new User();
        owner.setEmail("owner@test.com");
        owner.setFullName("Shop Owner");
        owner.setPassword("hashedpass");
        owner.setPhoneNumber("+255700333444");
        owner = userRepository.save(owner);

        unauthorizedUser = new User();
        unauthorizedUser.setEmail("unauthorized@test.com");
        unauthorizedUser.setFullName("Unauthorized User");
        unauthorizedUser.setPassword("hashedpass");
        unauthorizedUser = userRepository.save(unauthorizedUser);

        business = new Business();
        business.setName("Kariakoo Electronics");
        business.setOwner(owner);
        business.setAddress("Kariakoo");
        business.setCity("Dar es Salaam");
        business.setLatitude(-6.8182);
        business.setLongitude(39.2778);
        business.setVerificationStatus(VerificationStatus.VERIFIED);
        business = businessRepository.save(business);
    }

    @Test
    void testStartConversationAndSendMessage() {
        // Customer starts conversation with shop owner
        ConversationDto conversation = messagingService.getOrCreateConversation(
                customer.getId(), business.getId(), "Hello, is the fast charger available?"
        );

        assertNotNull(conversation);
        assertNotNull(conversation.getId());
        assertEquals(customer.getId(), conversation.getCustomerId());
        assertEquals(owner.getId(), conversation.getShopOwnerId());
        assertEquals(business.getId(), conversation.getBusinessId());

        // Verify initial message was saved
        List<MessageDto> messages = messagingService.getMessages(customer.getId(), conversation.getId());
        assertEquals(1, messages.size());
        assertEquals("Hello, is the fast charger available?", messages.get(0).getContent());
        assertEquals("CUSTOMER", messages.get(0).getSenderRole());

        // Owner replies to conversation
        MessageDto reply = messagingService.sendMessage(owner.getId(), conversation.getId(), "Yes, we have 10 units in stock!");
        assertNotNull(reply);
        assertEquals("SHOP_OWNER", reply.getSenderRole());

        // Verify full message history
        List<MessageDto> history = messagingService.getMessages(customer.getId(), conversation.getId());
        assertEquals(2, history.size());
        assertEquals("Hello, is the fast charger available?", history.get(0).getContent());
        assertEquals("Yes, we have 10 units in stock!", history.get(1).getContent());
    }

    @Test
    void testUnauthorizedUserBlockedFromPrivateConversation() {
        ConversationDto conversation = messagingService.getOrCreateConversation(
                customer.getId(), business.getId(), "Private inquiry"
        );

        // Unauthorized user attempts to read conversation
        assertThrows(RuntimeException.class, () -> {
            messagingService.getMessages(unauthorizedUser.getId(), conversation.getId());
        });

        // Unauthorized user attempts to send message to conversation
        assertThrows(RuntimeException.class, () -> {
            messagingService.sendMessage(unauthorizedUser.getId(), conversation.getId(), "Hacked message");
        });
    }

    @Test
    void testNoDuplicateConversationsCreated() {
        ConversationDto conv1 = messagingService.getOrCreateConversation(customer.getId(), business.getId(), "Message 1");
        ConversationDto conv2 = messagingService.getOrCreateConversation(customer.getId(), business.getId(), "Message 2");

        assertEquals(conv1.getId(), conv2.getId());
    }
}
