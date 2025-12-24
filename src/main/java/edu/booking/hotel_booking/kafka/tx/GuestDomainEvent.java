package edu.booking.hotel_booking.kafka.tx;

import edu.booking.hotel_booking.kafka.event.EventType;
import java.util.UUID;

public record GuestDomainEvent(UUID guestId, EventType type) {}
