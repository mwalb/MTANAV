package tz.mtanav.job;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class JobDto {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String location;
    private BigDecimal budgetAmount;
    private String currency;
    private String status;
    private String publicationStatus;
    private Long posterId;
    private Long businessId;
    private String businessName;
    private String employmentType;
    private String payType;
    private BigDecimal payAmountMin;
    private BigDecimal payAmountMax;
    private String payFrequency;
    private OffsetDateTime applicationDeadline;
    private String qualifications;
    private String responsibilities;
    private String contactInstructions;
    private OffsetDateTime createdAt;

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

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

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
}
