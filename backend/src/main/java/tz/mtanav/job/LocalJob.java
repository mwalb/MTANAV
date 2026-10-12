package tz.mtanav.job;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "local_jobs")
public class LocalJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "budget_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetAmount;

    @Column(nullable = false)
    private String currency = "TZS";

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, ASSIGNED, COMPLETED, DISPUTED, CLOSED

    @Column(name = "publication_status", nullable = false)
    private String publicationStatus = "PUBLISHED"; // DRAFT, PUBLISHED, PAUSED, CLOSED

    @Column(name = "poster_id", nullable = false)
    private Long posterId;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "employment_type", nullable = false)
    private String employmentType = "FULL_TIME"; // FULL_TIME, PART_TIME, CONTRACT, CASUAL, TEMPORARY

    @Column(name = "pay_type")
    private String payType = "FIXED"; // FIXED, RANGE, HOURLY, DAILY, MONTHLY, NEGOTIABLE

    @Column(name = "pay_amount_min", precision = 12, scale = 2)
    private BigDecimal payAmountMin;

    @Column(name = "pay_amount_max", precision = 12, scale = 2)
    private BigDecimal payAmountMax;

    @Column(name = "pay_frequency")
    private String payFrequency = "ONE_TIME";

    @Column(name = "application_deadline")
    private OffsetDateTime applicationDeadline;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "contact_instructions", columnDefinition = "TEXT")
    private String contactInstructions;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPublicationStatus() { return publicationStatus; }
    public void setPublicationStatus(String publicationStatus) { this.publicationStatus = publicationStatus; }

    public Long getPosterId() { return posterId; }
    public void setPosterId(Long posterId) { this.posterId = posterId; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public BigDecimal getPayAmountMin() { return payAmountMin; }
    public void setPayAmountMin(BigDecimal payAmountMin) { this.payAmountMin = payAmountMin; }

    public BigDecimal getPayAmountMax() { return payAmountMax; }
    public void setPayAmountMax(BigDecimal payAmountMax) { this.payAmountMax = payAmountMax; }

    public String getPayFrequency() { return payFrequency; }
    public void setPayFrequency(String payFrequency) { this.payFrequency = payFrequency; }

    public OffsetDateTime getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(OffsetDateTime applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public String getContactInstructions() { return contactInstructions; }
    public void setContactInstructions(String contactInstructions) { this.contactInstructions = contactInstructions; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
