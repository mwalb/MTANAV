package tz.mtanav.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.product.*;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessProductRepository businessProductRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public OrderDto createOrder(Long customerId, CreateOrderRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + request.getBusinessId()));

        Order order = Order.builder()
                .customer(customer)
                .business(business)
                .orderStatus(OrderStatus.PENDING)
                .orderType(request.getOrderType())
                .deliveryAddress(request.getDeliveryAddress())
                .notes(request.getNotes())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CreateOrderRequest.CreateOrderItemRequest itemReq : request.getItems()) {
            BusinessProduct bp = businessProductRepository.findById(itemReq.getBusinessProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Business Product not found: " + itemReq.getBusinessProductId()));

            BigDecimal itemUnitPrice = bp.getSalePrice() != null ? bp.getSalePrice() : bp.getPrice();
            String variantName = null;

            if (itemReq.getVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(itemReq.getVariantId())
                        .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + itemReq.getVariantId()));
                itemUnitPrice = variant.getPrice();
                variantName = variant.getName();
            }

            BigDecimal subtotal = itemUnitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            grandTotal = grandTotal.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .businessProductId(bp.getId())
                    .variantId(itemReq.getVariantId())
                    .productName(bp.getProduct().getName())
                    .variantName(variantName)
                    .unitPrice(itemUnitPrice)
                    .quantity(itemReq.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(grandTotal);
        Order saved = orderRepository.save(order);
        return mapToOrderDto(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getBusinessOrders(Long businessId) {
        return orderRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setOrderStatus(status);
        return mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto mapToOrderDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(i -> OrderItemDto.builder()
                        .id(i.getId())
                        .businessProductId(i.getBusinessProductId())
                        .variantId(i.getVariantId())
                        .productName(i.getProductName())
                        .variantName(i.getVariantName())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFullName())
                .businessId(order.getBusiness().getId())
                .businessName(order.getBusiness().getName())
                .orderStatus(order.getOrderStatus().name())
                .orderType(order.getOrderType().name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .notes(order.getNotes())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
