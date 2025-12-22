package edu.booking.hotel_booking.unit;

import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.entity.Room;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RoomDaoUnitTest {

    @Test
    void findAvailableRooms_buildsExpectedSqlAndParams() {
        NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);

        RoomDao dao = new RoomDao(jdbc);

        when(jdbc.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of());

        Integer minGuests = 2;
        LocalDate checkIn = LocalDate.of(2025, 1, 10);
        LocalDate checkOut = LocalDate.of(2025, 1, 12);

        List<Room> result = dao.findFreeRooms(checkIn, checkOut, minGuests);

        assertThat(result).isEmpty();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

        verify(jdbc).query(sqlCaptor.capture(), paramsCaptor.capture(), any(RowMapper.class));

        String usedSql = sqlCaptor.getValue();
        MapSqlParameterSource params = paramsCaptor.getValue();

        assertThat(usedSql).contains("FROM room r");
        assertThat(usedSql).contains("NOT EXISTS");
        assertThat(params.getValue("min_guests")).isEqualTo(minGuests);
        assertThat(params.getValue("check_in")).isEqualTo(checkIn);
        assertThat(params.getValue("check_out")).isEqualTo(checkOut);
    }
}
