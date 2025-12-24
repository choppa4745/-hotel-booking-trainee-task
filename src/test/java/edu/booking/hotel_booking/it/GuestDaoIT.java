package edu.booking.hotel_booking.it;

import edu.booking.hotel_booking.dao.GuestDao;
import edu.booking.hotel_booking.entity.Guest;
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
class GuestDaoIT extends AbstractPostgresIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private GuestDao guestDao;

    @BeforeEach
    void clean() {
        jdbc.execute("DELETE FROM hotel.booking");
        jdbc.execute("DELETE FROM hotel.room");
        jdbc.execute("DELETE FROM hotel.guest");
    }

    @Test
    void findAll_returnsAllGuests() {
        UUID g1 = UUID.randomUUID();
        UUID g2 = UUID.randomUUID();

        jdbc.update("""
                INSERT INTO hotel.guest(guest_id, first_name, last_name, middle_name, date_of_birth, phone)
                VALUES (?, ?, ?, ?, ?, ?)
                """, g1, "Ivan", "Ivanov", null, LocalDate.of(1990, 1, 1), "+79990000001");

        jdbc.update("""
                INSERT INTO hotel.guest(guest_id, first_name, last_name, middle_name, date_of_birth, phone)
                VALUES (?, ?, ?, ?, ?, ?)
                """, g2, "Petr", "Petrov", "Sergeevich", LocalDate.of(1985, 5, 5), "+79990000002");

        List<Guest> guests = guestDao.findAll(10,0);
        assertThat(guests).hasSize(2);
        assertThat(guests)
                .extracting(Guest::getGuestId)
                .containsExactlyInAnyOrder(g1, g2);
    }
}
