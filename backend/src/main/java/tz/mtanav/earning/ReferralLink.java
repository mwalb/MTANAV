package tz.mtanav.earning;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "referral_links")
public class ReferralLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "target_type", nullable = false)
    private String targetType; // PRODUCT, SERVICE, BUSINESS, GENERAL

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "clicks_count")
    private Integer clicksCount = 0;

    @Column(name = "conversions_count")
    private Integer conversionsCount = 0;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Integer getClicksCount() { return clicksCount; }
    public void setClicksCount(Integer clicksCount) { this.clicksCount = clicksCount; }
    public Integer getConversionsCount() { return conversionsCount; }
    public void setConversionsCount(Integer conversionsCount) { this.conversionsCount = conversionsCount; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
