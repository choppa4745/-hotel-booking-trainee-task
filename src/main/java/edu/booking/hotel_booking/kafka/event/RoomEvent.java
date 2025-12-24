package edu.booking.hotel_booking.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record RoomEvent(
        UUID roomId,
        EventType type,
        Instant occurredAt
) {}
