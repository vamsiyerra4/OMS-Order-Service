package com.orderms.order_service.dto;


import com.orderms.order_service.entity.PaymentMethod;
import com.orderms.order_service.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long id;
    private Long OrderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
