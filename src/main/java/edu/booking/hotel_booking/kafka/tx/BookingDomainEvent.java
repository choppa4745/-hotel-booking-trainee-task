package edu.booking.hotel_booking.kafka.tx;

import edu.booking.hotel_booking.kafka.event.EventType;
import java.util.UUID;

public record BookingDomainEvent(
        UUID bookingId,
        UUID roomId,
        UUID guestId,
        EventType type
) {}
