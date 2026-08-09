package com.orderms.order_service.mapper;

import com.orderms.order_service.dto.OrderRequestDto;
import com.orderms.order_service.dto.OrderResponseDto;
import com.orderms.order_service.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .userId(orderRequestDto.getUserId())
                .productId(orderRequestDto.getProductId())
                .quantity(orderRequestDto.getQuantity())
                .build();
    }

    public OrderResponseDto toResponseDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productid(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createdDate(order.getOrderDate())
                .build();
    }
}
