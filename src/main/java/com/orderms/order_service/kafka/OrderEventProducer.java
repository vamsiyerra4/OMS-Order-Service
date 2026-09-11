package com.orderms.order_service.kafka;

import com.orderms.order_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String ORDER_EVENT_TOPIC = "order-events";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {

        try {

            System.out.println(

                    ">>> PUBLISHING ORDER EVENT: " + orderPlacedEvent

            );

            String correlationId = MDC.get("correlationId");

            ProducerRecord<String,OrderPlacedEvent> record =
                    new ProducerRecord<>(
                            ORDER_EVENT_TOPIC,
                            String.valueOf(orderPlacedEvent.orderId()),
                            orderPlacedEvent
                    );

            if(correlationId != null || !correlationId.isBlank()) {
                record.headers().add(CORRELATION_ID_HEADER,
                        correlationId.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );

                System.out.println(">>> KAFKA CORRELATION ID ADDED: " + correlationId);

            }

            var result = kafkaTemplate.send(record).get();

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
