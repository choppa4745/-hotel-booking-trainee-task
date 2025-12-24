package edu.booking.hotel_booking.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record GuestEvent(
        UUID guestId,
        EventType type,
        Instant occurredAt
) {}
