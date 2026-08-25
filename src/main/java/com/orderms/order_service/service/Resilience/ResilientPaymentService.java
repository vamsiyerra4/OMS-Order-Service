package com.orderms.order_service.service.Resilience;

import com.orderms.order_service.client.PaymentClient;
import com.orderms.order_service.dto.PaymentRequestDTO;
import com.orderms.order_service.dto.PaymentResponseDTO;
import com.orderms.order_service.exception.PaymentServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResilientPaymentService {

    private final PaymentClient paymentClient;

    @Retry(name = "paymentService")
    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "paymentFallBack"
    )
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        return paymentClient.addPayment(paymentRequest);
    }

    private PaymentResponseDTO paymentFallBack(PaymentRequestDTO paymentRequest,Throwable ex) {

        throw new PaymentServiceUnavailableException("Payment service is unavailable! Please try again later.");

    }
}
