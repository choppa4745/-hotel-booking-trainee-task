package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.RoomDto;
import edu.booking.hotel_booking.entity.Room;
import edu.booking.hotel_booking.exception.NotFoundException;
import edu.booking.hotel_booking.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomDao roomDao;

    public RoomService(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    public List<Room> getAllRooms() {
        return roomDao.findAll();
    }

    public Room createRoom(RoomDto request) {
        validateRoom(request);
        return roomDao.create(request);
    }

    public Room updateRoom(UUID id, RoomDto request) {
        validateRoom(request);

        boolean updated = roomDao.update(id, request);
        if (!updated) {
            throw new NotFoundException("Room not found: " + id);
        }

        return roomDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found after update: " + id));
    }

    public void deleteRoom(UUID id) {
        boolean deleted = roomDao.delete(id);
        if (!deleted) {
            throw new NotFoundException("Room not found: " + id);
        }
    }

    public List<Room> findFreeRooms(String checkIn, String checkOut, Integer minGuests) {
        LocalDate checkI = parseDate(checkIn, "checkIn");
        LocalDate checkO = parseDate(checkOut, "checkIn");
        return roomDao.findFreeRooms(checkI, checkO, minGuests);
    }

    private void validateRoom(RoomDto request) {
        if (request == null) throw new ValidationException("Request is null");
        if (request.getFloor() <= 0) throw new ValidationException("floor must be > 0");
        if (isBlank(request.getNumber())) throw new ValidationException("number is blank");
        if (request.getNumberOfGuests() <= 0) throw new ValidationException("numberOfGuests must be > 0");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must be in format yyyy-MM-dd");
        }
    }
}
