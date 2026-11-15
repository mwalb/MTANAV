package tz.mtanav.messaging;

import java.time.OffsetDateTime;

public class ConversationDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long businessId;
    private String businessName;
    private Long shopOwnerId;
    private String shopOwnerName;
    private String lastMessageContent;
    private OffsetDateTime lastMessageAt;
    private long unreadCount;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public Long getShopOwnerId() { return shopOwnerId; }
    public void setShopOwnerId(Long shopOwnerId) { this.shopOwnerId = shopOwnerId; }

    public String getShopOwnerName() { return shopOwnerName; }
    public void setShopOwnerName(String shopOwnerName) { this.shopOwnerName = shopOwnerName; }

    public String getLastMessageContent() { return lastMessageContent; }
    public void setLastMessageContent(String lastMessageContent) { this.lastMessageContent = lastMessageContent; }

    public OffsetDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(OffsetDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
