package edu.booking.hotel_booking.controller;

import edu.booking.hotel_booking.dto.BookingDto;
import edu.booking.hotel_booking.entity.Booking;
import edu.booking.hotel_booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable UUID id, @RequestBody BookingDto request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();

    }
}
