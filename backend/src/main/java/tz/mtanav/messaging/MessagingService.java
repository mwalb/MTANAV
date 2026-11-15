package tz.mtanav.messaging;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public MessagingService(ConversationRepository conversationRepository,
                            MessageRepository messageRepository,
                            BusinessRepository businessRepository,
                            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ConversationDto getOrCreateConversation(Long customerId, Long businessId, String initialMessage) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found with ID: " + businessId));

        if (business.getOwner() == null) {
            throw new RuntimeException("Business does not have an assigned owner");
        }

        Long shopOwnerId = business.getOwner().getId();

        Conversation conversation = conversationRepository.findByCustomerIdAndBusinessId(customerId, businessId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setCustomerId(customerId);
                    c.setBusinessId(businessId);
                    c.setShopOwnerId(shopOwnerId);
                    return conversationRepository.save(c);
                });

        if (initialMessage != null && !initialMessage.isBlank()) {
            sendMessageInternal(customerId, conversation, initialMessage.trim());
        }

        return toConversationDto(conversation, customerId);
    }

    public List<ConversationDto> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findAllForUser(userId);
        return conversations.stream()
                .map(c -> toConversationDto(c, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public ConversationDto getConversationDetails(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        validateUserInConversation(userId, conversation);
        messageRepository.markAsReadForRecipient(conversationId, userId);

        return toConversationDto(conversation, userId);
    }

    @Transactional
    public List<MessageDto> getMessages(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        validateUserInConversation(userId, conversation);
        messageRepository.markAsReadForRecipient(conversationId, userId);

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return messages.stream()
                .map(m -> toMessageDto(m, conversation))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(Long senderId, Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        validateUserInConversation(senderId, conversation);

        Message saved = sendMessageInternal(senderId, conversation, content.trim());
        return toMessageDto(saved, conversation);
    }

    @Transactional
    public void markAsRead(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        validateUserInConversation(userId, conversation);
        messageRepository.markAsReadForRecipient(conversationId, userId);
    }

    private Message sendMessageInternal(Long senderId, Conversation conversation, String content) {
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setContent(content);
        message.setIsRead(false);
        message.setCreatedAt(OffsetDateTime.now());

        Message saved = messageRepository.save(message);

        conversation.setLastMessageContent(content.length() > 450 ? content.substring(0, 450) + "..." : content);
        conversation.setLastMessageAt(saved.getCreatedAt());
        conversation.setUpdatedAt(saved.getCreatedAt());
        conversationRepository.save(conversation);

        return saved;
    }

    private void validateUserInConversation(Long userId, Conversation conversation) {
        if (!conversation.getCustomerId().equals(userId) && !conversation.getShopOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You are not a participant in this conversation");
        }
    }

    private ConversationDto toConversationDto(Conversation c, Long currentUserId) {
        ConversationDto dto = new ConversationDto();
        dto.setId(c.getId());
        dto.setCustomerId(c.getCustomerId());
        userRepository.findById(c.getCustomerId()).ifPresent(u -> dto.setCustomerName(u.getFullName()));

        dto.setBusinessId(c.getBusinessId());
        businessRepository.findById(c.getBusinessId()).ifPresent(b -> dto.setBusinessName(b.getName()));

        dto.setShopOwnerId(c.getShopOwnerId());
        userRepository.findById(c.getShopOwnerId()).ifPresent(u -> dto.setShopOwnerName(u.getFullName()));

        dto.setLastMessageContent(c.getLastMessageContent());
        dto.setLastMessageAt(c.getLastMessageAt());
        dto.setCreatedAt(c.getCreatedAt());

        long unread = messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(c.getId(), currentUserId);
        dto.setUnreadCount(unread);

        return dto;
    }

    private MessageDto toMessageDto(Message m, Conversation conversation) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setConversationId(m.getConversationId());
        dto.setSenderId(m.getSenderId());
        userRepository.findById(m.getSenderId()).ifPresent(u -> dto.setSenderName(u.getFullName()));

        if (m.getSenderId().equals(conversation.getCustomerId())) {
            dto.setSenderRole("CUSTOMER");
        } else {
            dto.setSenderRole("SHOP_OWNER");
        }

        dto.setContent(m.getContent());
        dto.setIsRead(m.getIsRead());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}
