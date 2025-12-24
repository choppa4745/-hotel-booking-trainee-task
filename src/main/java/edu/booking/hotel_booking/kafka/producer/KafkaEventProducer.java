package edu.booking.hotel_booking.kafka.producer;

import edu.booking.hotel_booking.kafka.KafkaTopicsProperties;
import edu.booking.hotel_booking.kafka.event.BookingEvent;
import edu.booking.hotel_booking.kafka.event.GuestEvent;
import edu.booking.hotel_booking.kafka.event.RoomEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              KafkaTopicsProperties topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    public void send(GuestEvent event) {
        kafkaTemplate.send(topics.guest(), event.guestId().toString(), event);
    }

    public void send(RoomEvent event) {
        kafkaTemplate.send(topics.room(), event.roomId().toString(), event);
    }

    public void send(BookingEvent event) {
        kafkaTemplate.send(topics.booking(), event.bookingId().toString(), event);
    }
}
