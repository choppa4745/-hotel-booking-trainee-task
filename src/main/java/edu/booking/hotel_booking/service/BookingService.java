package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.BookingDao;
import edu.booking.hotel_booking.dao.GuestDao;
import edu.booking.hotel_booking.dao.RoomDao;
import edu.booking.hotel_booking.dto.BookingDto;
import edu.booking.hotel_booking.entity.Booking;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.entity.Room;
import edu.booking.hotel_booking.exception.ConflictException;
import edu.booking.hotel_booking.exception.NotFoundException;
import edu.booking.hotel_booking.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingDao bookingDao;
    private final GuestDao guestDao;
    private final RoomDao roomDao;

    public BookingService(BookingDao bookingDao, GuestDao guestDao, RoomDao roomDao) {
        this.bookingDao = bookingDao;
        this.guestDao = guestDao;
        this.roomDao = roomDao;
    }

    @Transactional
    public Booking createBooking(BookingDto dto) {
        validateBookingDto(dto);

        LocalDate checkIn = parseDate(dto.getCheckInDate(), "checkInDate");
        LocalDate checkOut = parseDate(dto.getCheckOutDate(), "checkOutDate");

        if (!checkIn.isBefore(checkOut)) {
            throw new ValidationException("checkInDate must be before checkOutDate");
        }

        UUID guestId = dto.getGuest().getGuestId();
        UUID roomId = dto.getRoom().getRoomId();

        Guest guest = guestDao.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found: " + guestId));

        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        boolean conflict = bookingDao.existsConflict(roomId, checkIn, checkOut, null);
        if (conflict) {
            throw new ConflictException("Room is already booked for this period");
        }
        dto.setGuest(guest);
        dto.setRoom(room);

        return bookingDao.create(dto);
    }

    @Transactional
    public Booking updateBooking(UUID bookingId, BookingDto dto) {
        if (bookingId == null) throw new ValidationException("bookingId is required");
        validateBookingDto(dto);

        bookingDao.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        LocalDate checkIn = parseDate(dto.getCheckInDate(), "checkInDate");
        LocalDate checkOut = parseDate(dto.getCheckOutDate(), "checkOutDate");

        if (!checkIn.isBefore(checkOut)) {
            throw new ValidationException("checkInDate must be before checkOutDate");
        }

        UUID guestId = dto.getGuest().getGuestId();
        UUID roomId = dto.getRoom().getRoomId();

        Guest guest = guestDao.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found: " + guestId));

        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        boolean conflict = bookingDao.existsConflict(roomId, checkIn, checkOut, bookingId);
        if (conflict) {
            throw new ConflictException("Room is already booked for this period");
        }

        dto.setGuest(guest);
        dto.setRoom(room);

        boolean updated = bookingDao.update(bookingId, dto);
        if (!updated) {
            throw new NotFoundException("Booking not found: " + bookingId);
        }

        return bookingDao.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found after update: " + bookingId));
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        if (bookingId == null) throw new ValidationException("bookingId is required");

        boolean cancelled = bookingDao.cancel(bookingId);
        if (!cancelled) {
            throw new NotFoundException("Booking not found or already cancelled: " + bookingId);
        }
    }

    private void validateBookingDto(BookingDto dto) {
        if (dto == null) throw new ValidationException("BookingDto is null");

        if (dto.getCheckInDate() == null || dto.getCheckInDate().isBlank()) {
            throw new ValidationException("checkInDate is required");
        }
        if (dto.getCheckOutDate() == null || dto.getCheckOutDate().isBlank()) {
            throw new ValidationException("checkOutDate is required");
        }

        if (dto.getGuest() == null || dto.getGuest().getGuestId() == null) {
            throw new ValidationException("guest.guestId is required");
        }
        if (dto.getRoom() == null || dto.getRoom().getRoomId() == null) {
            throw new ValidationException("room.roomId is required");
        }
    }

    private LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must be in format yyyy-MM-dd");
        }
    }
}
