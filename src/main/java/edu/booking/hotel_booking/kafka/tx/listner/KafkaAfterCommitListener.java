package edu.booking.hotel_booking.kafka.tx.listner;

import edu.booking.hotel_booking.kafka.producer.KafkaEventProducer;
import edu.booking.hotel_booking.kafka.event.BookingEvent;
import edu.booking.hotel_booking.kafka.event.GuestEvent;
import edu.booking.hotel_booking.kafka.event.RoomEvent;
import edu.booking.hotel_booking.kafka.tx.BookingDomainEvent;
import edu.booking.hotel_booking.kafka.tx.GuestDomainEvent;
import edu.booking.hotel_booking.kafka.tx.RoomDomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.time.Instant;

@Component
public class KafkaAfterCommitListener {

    private final KafkaEventProducer producer;

    public KafkaAfterCommitListener(KafkaEventProducer producer) {
        this.producer = producer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGuest(GuestDomainEvent e) {
        producer.send(new GuestEvent(e.guestId(), e.type(), Instant.now()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoom(RoomDomainEvent e) {
        producer.send(new RoomEvent(e.roomId(), e.type(), Instant.now()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBooking(BookingDomainEvent e) {
        producer.send(new BookingEvent(e.bookingId(), e.roomId(), e.guestId(), e.type(), Instant.now()));
    }
}
