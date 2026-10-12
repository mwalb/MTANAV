package tz.mtanav.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Business ID is required")
    private Long businessId;

    @NotNull(message = "Order type is required (DELIVERY or PICKUP)")
    private OrderType orderType;

    private String deliveryAddress;
    private String notes;

    @NotEmpty(message = "Order items list cannot be empty")
    private List<CreateOrderItemRequest> items;

    @Data
    public static class CreateOrderItemRequest {
        @NotNull(message = "Business Product ID is required")
        private Long businessProductId;

        private Long variantId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}
