package edu.booking.hotel_booking.service;

import edu.booking.hotel_booking.dao.GuestDao;
import edu.booking.hotel_booking.dto.GuestDto;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.kafka.event.EventType;
import edu.booking.hotel_booking.kafka.tx.GuestDomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import edu.booking.hotel_booking.exception.NotFoundException;
import edu.booking.hotel_booking.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GuestService {

    private final GuestDao guestDao;
    private final ApplicationEventPublisher eventPublisher;


    public GuestService(GuestDao guestDao, ApplicationEventPublisher eventPublisher) {
        this.guestDao = guestDao;
        this.eventPublisher = eventPublisher;

    }

    public List<Guest> getAllGuests(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 0);

        int limit = safeSize;
        int offset = safePage * safeSize;

        return guestDao.findAll(limit, offset);
    }

    public Guest createGuest(GuestDto request) {
        validateGuest(request);
        Guest created = guestDao.create(request);
        eventPublisher.publishEvent(new GuestDomainEvent(created.getGuestId(), EventType.CREATED));
        return created;
    }

    public Guest updateGuest(UUID id, GuestDto request) {
        validateGuest(request);

        boolean updated = guestDao.update(id, request);
        if (!updated) {
            throw new NotFoundException("Guest not found: " + id);
        }

        Guest result = guestDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Guest not found after update: " + id));

        eventPublisher.publishEvent(new GuestDomainEvent(id, EventType.UPDATED));
        return result;
    }

    private void validateGuest(GuestDto request) {
        if (request == null) throw new ValidationException("Request is null");

        if (isBlank(request.getFirstName())) throw new ValidationException("firstName is blank");
        if (isBlank(request.getLastName())) throw new ValidationException("lastName is blank");
        if (isBlank(request.getPhone())) throw new ValidationException("phone is blank");
        if (isBlank(request.getDateOfBirth())) throw new ValidationException("dateOfBirth is blank");

        LocalDate dob;
        try {
            dob = LocalDate.parse(request.getDateOfBirth());
        } catch (Exception e) {
            throw new ValidationException("date_of_birth must be ISO date (yyyy-MM-dd)");
        }

        if (dob.isAfter(LocalDate.now())) {
            throw new ValidationException("date_of_birth cannot be in the future");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
