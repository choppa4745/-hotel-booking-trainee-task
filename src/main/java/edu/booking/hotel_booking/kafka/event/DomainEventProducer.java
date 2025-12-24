package edu.booking.hotel_booking.kafka.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, DomainEvent<?> event) {
        kafkaTemplate.send(topic, event.getEntityId().toString(), event);
    }
}
