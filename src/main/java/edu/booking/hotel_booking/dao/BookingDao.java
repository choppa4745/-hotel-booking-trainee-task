package edu.booking.hotel_booking.dao;

import edu.booking.hotel_booking.dto.BookingDto;
import edu.booking.hotel_booking.entity.Booking;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.entity.Room;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class BookingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookingDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Booking> BOOKING_WITH_JOIN_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            Guest guest = Guest.builder()
                    .guestId(rs.getObject("g_guest_id", UUID.class))
                    .firstName(rs.getString("g_first_name"))
                    .lastName(rs.getString("g_last_name"))
                    .middleName(rs.getString("g_middle_name"))
                    .dateOfBirth(rs.getObject("g_date_of_birth", LocalDate.class))
                    .phone(rs.getString("g_phone"))
                    .build();

            Room room = Room.builder()
                    .roomId(rs.getObject("r_room_id", UUID.class))
                    .floor(rs.getInt("r_floor"))
                    .number(rs.getString("r_number"))
                    .numberOfGuests(rs.getInt("r_number_of_guests"))
                    .build();

            return Booking.builder()
                    .bookingId(rs.getObject("b_booking_id", UUID.class))
                    .checkInDate(rs.getObject("b_check_in_date", LocalDate.class))
                    .checkOutDate(rs.getObject("b_check_out_date", LocalDate.class))
                    .guest(guest)
                    .room(room)
                    .status(rs.getString("b_status"))
                    .build();
        }
    };

    public List<Booking> findAll(int limit, int offset) {
        String sql = """
        SELECT booking_id, check_in_date, check_out_date, guest_id, room_id, status
          FROM hotel.booking
         ORDER BY check_in_date DESC, booking_id
         LIMIT :limit OFFSET :offset
        """;

        Map<String, Object> params = Map.of("limit", limit, "offset", offset);
        return jdbcTemplate.query(sql, params, BOOKING_WITH_JOIN_ROW_MAPPER);
    }

    public boolean existsConflict(UUID roomId, LocalDate checkIn, LocalDate checkOut, UUID excludeBookingId) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                      FROM booking b
                    WHERE b.room_id = :room_id
                       AND b.status = 'ACTIVE'
                       AND b.check_in_date < :check_out
                       AND b.check_out_date > :check_in
                       AND (:exclude_id IS NULL OR b.booking_id <> :exclude_id)
                )
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("room_id", roomId)
                .addValue("check_in", checkIn)
                .addValue("check_out", checkOut)
                .addValue("exclude_id", excludeBookingId);

        Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    public Booking create(BookingDto dto) {
        UUID bookingId = UUID.randomUUID();

        UUID guestId = dto.getGuest().getGuestId();
        UUID roomId = dto.getRoom().getRoomId();

        String sql = """
                INSERT INTO booking (booking_id, check_in_date, check_out_date, guest_id, room_id, status)
                VALUES (:booking_id, :check_in, :check_out, :guest_id, :room_id, 'ACTIVE')
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("booking_id", bookingId)
                .addValue("check_in",  LocalDate.parse(dto.getCheckInDate()))
                .addValue("check_out", LocalDate.parse(dto.getCheckOutDate()))
                .addValue("guest_id", guestId)
                .addValue("room_id", roomId);


        jdbcTemplate.update(sql, params);

        return Booking.builder()
                .bookingId(bookingId)
                .checkInDate(LocalDate.parse(dto.getCheckInDate()))
                .checkOutDate(LocalDate.parse(dto.getCheckOutDate()))
                .guest(dto.getGuest())
                .room(dto.getRoom())
                .status("ACTIVE")
                .build();
    }

    public boolean update(UUID bookingId, BookingDto dto) {
        UUID guestId = dto.getGuest().getGuestId();
        UUID roomId = dto.getRoom().getRoomId();

        String sql = """
                UPDATE booking
                   SET check_in_date = :check_in,
                       check_out_date = :check_out,
                       guest_id = :guest_id,
                       room_id = :room_id
                WHERE booking_id = :booking_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("booking_id", bookingId)
                .addValue("check_in", LocalDate.parse(dto.getCheckInDate()))
                .addValue("check_out", LocalDate.parse(dto.getCheckOutDate()))
                .addValue("guest_id", guestId)
                .addValue("room_id", roomId);

        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean cancel(UUID bookingId) {
        String sql = """
                UPDATE booking
                    SET status = 'CANCELLED'
                WHERE booking_id = :booking_id
                   AND status <> 'CANCELLED'
                """;
        return jdbcTemplate.update(sql, Map.of("booking_id", bookingId)) > 0;
    }

    public Optional<Booking> findById(UUID bookingId) {
        String sql = """
                SELECT
                    b.booking_id AS b_booking_id,
                    b.check_in_date AS b_check_in_date,
                    b.check_out_date AS b_check_out_date,
                    b.status AS b_status,

                    g.guest_id AS g_guest_id,
                    g.first_name AS g_first_name,
                    g.last_name AS g_last_name,
                    g.middle_name AS g_middle_name,
                    g.date_of_birth AS g_date_of_birth,
                    g.phone AS g_phone,

                    r.room_id AS r_room_id,
                    r.floor AS r_floor,
                    r.number AS r_number,
                    r.number_of_guests AS r_number_of_guests
                FROM booking b
                JOIN guest g USING (guest_id)
                JOIN room r USING (room_id)
                WHERE b.booking_id = :booking_id
                """;

        var rows = jdbcTemplate.query(sql, Map.of("booking_id", bookingId), BOOKING_WITH_JOIN_ROW_MAPPER);
        return rows.stream().findFirst();
    }
}
