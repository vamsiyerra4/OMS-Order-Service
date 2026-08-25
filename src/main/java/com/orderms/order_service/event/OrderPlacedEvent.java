package com.orderms.order_service.event;

import java.time.LocalDateTime;

public record OrderPlacedEvent(

        Long orderId,
        Long userId,
        Long productId,
        String status,
        LocalDateTime timeStamp
) {
}
