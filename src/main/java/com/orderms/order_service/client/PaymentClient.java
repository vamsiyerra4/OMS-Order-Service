package com.orderms.order_service.client;

import com.orderms.order_service.dto.PaymentRequestDTO;
import com.orderms.order_service.dto.PaymentResponseDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",url = "${payment-service.url:http://localhost:8080}")
public interface PaymentClient {

    @PostMapping("/api/payments/addPayment")
    PaymentResponseDTO addPayment(@Valid @RequestBody PaymentRequestDTO PaymentRequestDTO);
}
