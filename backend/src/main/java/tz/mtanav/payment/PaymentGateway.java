package tz.mtanav.payment;

public interface PaymentGateway {
    PaymentDto processPayment(Long userId, ProcessPaymentRequest request);
}
