package com.orderms.order_service.dto;

import com.orderms.order_service.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "Order Id required")
    @Positive(message = "Order Id must be greater than 0")
    private Long orderId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01",message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
