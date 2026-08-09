package com.orderms.order_service.dto;

import com.orderms.order_service.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long id;
    private Long userId;
    private Long productid;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdDate;
}
