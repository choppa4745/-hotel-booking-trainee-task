package edu.booking.hotel_booking.it;

import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomDaoIT extends AbstractPostgresIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private RoomDao roomDao;

    @BeforeEach
    void clean() {
        jdbc.execute("DELETE FROM hotel.booking");
        jdbc.execute("DELETE FROM hotel.room");
        jdbc.execute("DELETE FROM hotel.guest");
    }

    @Test
    void findAll_returnsAllRoomsOrderedOrNot() {
        UUID r1 = UUID.randomUUID();
        UUID r2 = UUID.randomUUID();

        jdbc.update("""
                INSERT INTO hotel.room(room_id, floor, number, number_of_guests)
                VALUES (?, ?, ?, ?)
                """, r1, 2, "201", 2);

        jdbc.update("""
                INSERT INTO hotel.room(room_id, floor, number, number_of_guests)
                VALUES (?, ?, ?, ?)
                """, r2, 1, "101", 3);

        List<Room> rooms = roomDao.findAll();

        assertThat(rooms).hasSize(2);
        assertThat(rooms)
                .extracting(Room::getRoomId)
                .containsExactlyInAnyOrder(r1, r2);
    }
}
