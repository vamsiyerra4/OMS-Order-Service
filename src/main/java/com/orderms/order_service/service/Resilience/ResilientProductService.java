package com.orderms.order_service.service.Resilience;


import com.orderms.order_service.client.ProductClient;
import com.orderms.order_service.dto.ProductResponseDto;
import com.orderms.order_service.exception.InvalidOrderException;
import com.orderms.order_service.exception.ProductServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResilientProductService {

    private final ProductClient productClient;

    @Retry(name = "productService")
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "productFallBack"
    )
    public ProductResponseDto getProductById(Long productId) {
        try {
           return productClient.getProductById(productId);
        }catch (FeignException.NotFound ex){

            throw new InvalidOrderException("Product not found with id " + productId);
        }
    }

    public ProductResponseDto productFallBack(Long productId, Throwable ex) {

        throw new ProductServiceUnavailableException("Product service is unavailable right now, please try again later");
    }
}
