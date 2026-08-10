package com.orderms.order_service.service.Impl;

import com.orderms.order_service.client.ProductClient;
import com.orderms.order_service.dto.OrderRequestDto;
import com.orderms.order_service.dto.OrderResponseDto;
import com.orderms.order_service.dto.ProductResponseDto;
import com.orderms.order_service.entity.Order;
import com.orderms.order_service.exception.InvalidOrderException;
import com.orderms.order_service.exception.OrderNotFoundException;
import com.orderms.order_service.exception.ProductServiceUnavailableException;
import com.orderms.order_service.mapper.OrderMapper;
import com.orderms.order_service.repository.OrderRepository;
import com.orderms.order_service.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        ProductResponseDto product;

        try{
            product = productClient.getProductById(orderRequestDto.getProductId());
        }catch (FeignException.NotFound ex){
            throw new InvalidOrderException(
                    "Product not found with Id: "+orderRequestDto.getProductId()
            );
        }catch (FeignException ex){
            throw new ProductServiceUnavailableException(
                    "Product service is currently unavailable"
            );
        }

        if(product == null){
            throw new InvalidOrderException(
                    "Product not found with Id: "+orderRequestDto.getProductId()
            );
        }

        if(product.getStockQuantity() < orderRequestDto.getQuantity()){
            throw new InvalidOrderException(
                    "Insufficient stock quantity" + orderRequestDto.getProductId()
            );
        }



        Order order = orderMapper.toEntity(orderRequestDto);
        Order savedOrder = orderRepository.save(order);

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
