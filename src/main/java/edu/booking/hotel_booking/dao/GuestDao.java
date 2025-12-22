package edu.booking.hotel_booking.dao;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import edu.booking.hotel_booking.dto.GuestDto;
import edu.booking.hotel_booking.entity.Guest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuestDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GuestDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Guest> GUEST_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Guest mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Guest.builder()
                    .guestId(rs.getObject("guest_id", UUID.class))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .middleName(rs.getString("middle_name"))
                    .dateOfBirth(rs.getObject("date_of_birth", LocalDate.class))
                    .phone(rs.getString("phone"))
                    .build();
        }
    };

    public Guest create(GuestDto dto) {
        UUID id = UUID.randomUUID();

        String sql = """
                INSERT INTO guest (guest_id, first_name, last_name, middle_name, date_of_birth, phone)
                VALUES (:guest_id, :first_name, :last_name, :middle_name, :date_of_birth, :phone)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("guest_id", id)
                .addValue("first_name", dto.getFirstName())
                .addValue("last_name", dto.getLastName())
                .addValue("middle_name", dto.getMiddleName())
                .addValue("date_of_birth", LocalDate.parse(dto.getDateOfBirth()))
                .addValue("phone", dto.getPhone());

        jdbcTemplate.update(sql, params);

        return Guest.builder()
                .guestId(id)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .dateOfBirth(LocalDate.parse(dto.getDateOfBirth()))
                .phone(dto.getPhone())
                .build();
    }

    public Optional<Guest> findById(UUID guestId) {
        String sql = """
                SELECT * FROM guest 
                WHERE guest_id = :guest_id
                """;

        var rows = jdbcTemplate.query(sql, Map.of("guest_id", guestId), GUEST_ROW_MAPPER);
        return rows.stream().findFirst();
    }

    public boolean update(UUID guestId, GuestDto dto) {
        String sql = """
                UPDATE guest
                   SET first_name = :first_name,
                       last_name = :last_name,
                       middle_name = :middle_name,
                       date_of_birth = :date_of_birth,
                       phone = :phone
                WHERE guest_id = :guest_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("guest_id", guestId)
                .addValue("first_name", dto.getFirstName())
                .addValue("last_name", dto.getLastName())
                .addValue("middle_name", dto.getMiddleName())
                .addValue("date_of_birth", dto.getDateOfBirth())
                .addValue("phone", dto.getPhone());

        return jdbcTemplate.update(sql, params) > 0;
    }
}
