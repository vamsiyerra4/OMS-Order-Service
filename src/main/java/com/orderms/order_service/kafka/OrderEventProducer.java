package com.orderms.order_service.kafka;

import com.orderms.order_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String ORDER_EVENT_TOPIC = "order-events";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {

        try {

            System.out.println(

                    ">>> PUBLISHING ORDER EVENT: " + orderPlacedEvent

            );

            var result = kafkaTemplate.send(

                    ORDER_EVENT_TOPIC,

                    String.valueOf(orderPlacedEvent.orderId()),

                    orderPlacedEvent

            ).get();

            System.out.println(

                    ">>> KAFKA PUBLISH SUCCESS: topic=" +

                            result.getRecordMetadata().topic() +

                            ", partition=" +

                            result.getRecordMetadata().partition() +

                            ", offset=" +

                            result.getRecordMetadata().offset()

            );

        } catch (Exception e) {

            System.err.println(

                    ">>> KAFKA PUBLISH FAILED: " + e.getMessage()

            );

            e.printStackTrace();

        }
    }
}
