package tz.mtanav.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long businessId;
    private String businessName;
    private Long serviceId;
    private String serviceName;
    private String bookingStatus;
    private LocalDate bookingDate;
    private String bookingTime;
    private BigDecimal price;
    private String notes;
    private LocalDateTime createdAt;
}
