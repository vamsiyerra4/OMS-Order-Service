package com.orderms.order_service.service;

import com.orderms.order_service.dto.OrderRequestDto;
import com.orderms.order_service.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrder();

    OrderResponseDto updateOrder(Long id,OrderRequestDto orderRequestDto);

    void deleteOrder(Long id);
}
