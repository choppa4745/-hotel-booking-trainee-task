package edu.booking.hotel_booking.it;

import edu.booking.hotel_booking.dao.BookingDao;
import edu.booking.hotel_booking.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingDaoIT extends AbstractPostgresIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private BookingDao bookingDao;

    @BeforeEach
    void clean() {
        jdbc.execute("DELETE FROM hotel.booking");
        jdbc.execute("DELETE FROM hotel.room");
        jdbc.execute("DELETE FROM hotel.guest");
    }

    @Test
    void findAll_returnsAllBookings() {
        UUID guestId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        jdbc.update("""
                INSERT INTO hotel.guest(guest_id, first_name, last_name, middle_name, date_of_birth, phone)
                VALUES (?, ?, ?, ?, ?, ?)
                """, guestId, "Ivan", "Ivanov", null, LocalDate.of(1990, 1, 1), "+79990000001");

        jdbc.update("""
                INSERT INTO hotel.room(room_id, floor, number, number_of_guests)
                VALUES (?, ?, ?, ?)
                """, roomId, 1, "101", 2);

        UUID b1 = UUID.randomUUID();
        UUID b2 = UUID.randomUUID();

        jdbc.update("""
                INSERT INTO hotel.booking(booking_id, check_in_date, check_out_date, guest_id, room_id, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """, b1, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3), guestId, roomId, "ACTIVE");

        jdbc.update("""
                INSERT INTO hotel.booking(booking_id, check_in_date, check_out_date, guest_id, room_id, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """, b2, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 3), guestId, roomId, "CANCELLED");

        List<Booking> bookings = bookingDao.findAll(10,0);

        assertThat(bookings).hasSize(2);
        assertThat(bookings)
                .extracting(Booking::getBookingId)
                .containsExactlyInAnyOrder(b1, b2);
    }
}
