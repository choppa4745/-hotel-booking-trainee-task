package edu.booking.hotel_booking.dao;

import edu.booking.hotel_booking.dto.RoomDto;
import edu.booking.hotel_booking.entity.Room;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RoomDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Room> ROOM_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Room.builder()
                    .roomId(rs.getObject("room_id", UUID.class))
                    .floor(rs.getInt("floor"))
                    .number(rs.getString("number"))
                    .numberOfGuests(rs.getInt("number_of_guests"))
                    .build();
        }
    };

    public Room create(RoomDto dto) {
        UUID id = UUID.randomUUID();

        String sql = """
                INSERT INTO room (room_id, floor, number, number_of_guests)
                VALUES (:room_id, :floor, :number, :number_of_guests)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("room_id", id)
                .addValue("floor", dto.getFloor())
                .addValue("number", dto.getNumber())
                .addValue("number_of_guests", dto.getNumberOfGuests());

        jdbcTemplate.update(sql, params);

        return Room.builder()
                .roomId(id)
                .floor(dto.getFloor())
                .number(dto.getNumber())
                .numberOfGuests(dto.getNumberOfGuests())
                .build();
    }

    public Optional<Room> findById(UUID roomId) {
        String sql = """
                SELECT * FROM room 
                WHERE room_id = :room_id
                """;
        var rows = jdbcTemplate.query(sql, Map.of("room_id", roomId), ROOM_ROW_MAPPER);
        return rows.stream().findFirst();
    }

    public boolean update(UUID roomId, RoomDto dto) {
        String sql = """
                UPDATE room
                   SET floor = :floor,
                       number = :number,
                       number_of_guests = :number_of_guests
                WHERE room_id = :room_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("room_id", roomId)
                .addValue("floor", dto.getFloor())
                .addValue("number", dto.getNumber())
                .addValue("number_of_guests", dto.getNumberOfGuests());

        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean delete(UUID roomId) {
        String sql = "DELETE FROM room WHERE room_id = :room_id";
        return jdbcTemplate.update(sql, Map.of("room_id", roomId)) > 0;
    }

    /**
     * Свободные комнаты на интервал дат.
     * Если minGuests == null — не фильтруем по вместимости.
     */
    public List<Room> findFreeRooms(LocalDate checkIn, LocalDate checkOut, Integer minGuests) {
        String sql = """
                SELECT r.*
                  FROM room r
                WHERE (:min_guests IS NULL OR r.number_of_guests >= :min_guests)
                   AND NOT EXISTS (
                        SELECT 1
                          FROM booking b
                        WHERE b.room_id = r.room_id
                           AND b.status = 'ACTIVE'
                           AND b.check_in_date < :check_out
                           AND b.check_out_date > :check_in
                   )
                ORDER BY r.floor, r.number
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("check_in", checkIn)
                .addValue("check_out", checkOut)
                .addValue("min_guests", minGuests);

        return jdbcTemplate.query(sql, params, ROOM_ROW_MAPPER);
    }
}
