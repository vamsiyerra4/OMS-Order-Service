package com.orderms.order_service.service.Impl;

import com.orderms.order_service.dto.*;
import com.orderms.order_service.entity.Order;
import com.orderms.order_service.entity.OrderStatus;
import com.orderms.order_service.entity.PaymentStatus;
import com.orderms.order_service.event.OrderPlacedEvent;
import com.orderms.order_service.exception.InvalidOrderException;
import com.orderms.order_service.exception.OrderNotFoundException;
import com.orderms.order_service.kafka.OrderEventProducer;
import com.orderms.order_service.mapper.OrderMapper;
import com.orderms.order_service.repository.OrderRepository;
import com.orderms.order_service.service.OrderService;
import com.orderms.order_service.service.Resilience.ResilientPaymentService;
import com.orderms.order_service.service.Resilience.ResilientProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ResilientProductService resilientProductService;
    private final ResilientPaymentService resilientPaymentService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;


    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        ProductResponseDto product = resilientProductService.getProductById(orderRequestDto.getProductId());

        if (product == null) {
            throw new InvalidOrderException(
                    "Product not found with Id: " + orderRequestDto.getProductId()
            );
        }

        if (product.getStockQuantity() < orderRequestDto.getQuantity()) {
            throw new InvalidOrderException(
                    "Insufficient stock quantity" + orderRequestDto.getProductId()
            );
        }

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setStatus(OrderStatus.CREATED);

        Order savedOrder = orderRepository.save(order);

        BigDecimal amount = product.getPrice()
                .multiply(BigDecimal.valueOf(orderRequestDto.getQuantity()));

        PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .orderId(savedOrder.getId())
                .amount(amount)
                .paymentMethod(orderRequestDto.getPaymentMethod())
                .build();

        try {

            PaymentResponseDTO paymentResponse = resilientPaymentService.processPayment(paymentRequest);

            if (paymentResponse.getPaymentStatus() == PaymentStatus.SUCCESS) {
                savedOrder.setStatus(OrderStatus.CONFIRMED);
            } else {
                savedOrder.setStatus(OrderStatus.CANCELLED);
            }

            savedOrder = orderRepository.save(savedOrder);

        } catch (Exception ex) {

            savedOrder.setStatus(OrderStatus.CANCELLED);
            savedOrder = orderRepository.save(savedOrder);

            throw ex;
        }

        if (savedOrder.getStatus().equals(OrderStatus.CONFIRMED)) {

            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    savedOrder.getProductId(),
                    savedOrder.getStatus().name(),
                    LocalDateTime.now()

            );

            orderEventProducer.publishOrderPlacedEvent(orderPlacedEvent);

        }

        return orderMapper.toResponseDto(savedOrder);

    }

    @Override
    public OrderResponseDto getOrderById(Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Order not found with id " + id));
        return orderMapper.toResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrder() {
        return orderRepository.findAll()
                .stream().map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrder(Long id, OrderRequestDto orderRequestDto) {


        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        order.setUserId(orderRequestDto.getUserId());
        order.setProductId(orderRequestDto.getProductId());
        order.setQuantity(orderRequestDto.getQuantity());

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));

        orderRepository.delete(order);
    }


}
