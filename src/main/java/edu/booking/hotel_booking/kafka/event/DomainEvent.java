package edu.booking.hotel_booking.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DomainEvent<T> {

    private String eventType;
    private String entityType;
    private UUID entityId;
    private T payload;
    private Instant createdAt;
}
