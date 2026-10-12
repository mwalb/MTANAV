package tz.mtanav.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.booking.Booking;
import tz.mtanav.booking.BookingRepository;
import tz.mtanav.order.Order;
import tz.mtanav.order.OrderRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockPaymentService implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentDto processPayment(Long userId, ProcessPaymentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderId()));
        }

        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + request.getBookingId()));
        }

        String ref = "MTN-PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .booking(booking)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionReference(ref)
                .build();

        Payment saved = paymentRepository.save(payment);

        return PaymentDto.builder()
                .id(saved.getId())
                .orderId(order != null ? order.getId() : null)
                .bookingId(booking != null ? booking.getId() : null)
                .userId(user.getId())
                .amount(saved.getAmount())
                .paymentMethod(saved.getPaymentMethod().name())
                .paymentStatus(saved.getPaymentStatus().name())
                .transactionReference(saved.getTransactionReference())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
