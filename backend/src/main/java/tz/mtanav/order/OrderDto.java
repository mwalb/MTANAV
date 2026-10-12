package tz.mtanav.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long businessId;
    private String businessName;
    private String orderStatus;
    private String orderType;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String notes;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
}
