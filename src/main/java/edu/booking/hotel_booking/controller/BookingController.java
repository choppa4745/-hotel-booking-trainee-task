package edu.booking.hotel_booking.controller;

import edu.booking.hotel_booking.dto.BookingDto;
import edu.booking.hotel_booking.entity.Booking;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService bookingService) {
        this.service = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAll(){
        return ResponseEntity.ok(service.getAllBookings());
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBooking(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable UUID id, @RequestBody BookingDto request) {
        return ResponseEntity.ok(service.updateBooking(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        service.cancelBooking(id);
        return ResponseEntity.noContent().build();

    }
}
