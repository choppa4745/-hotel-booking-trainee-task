package edu.booking.hotel_booking.controller;

import edu.booking.hotel_booking.dto.GuestDto;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.service.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping
    public ResponseEntity<Guest> create(@RequestBody GuestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guest> update(@PathVariable UUID id, @RequestBody GuestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.updateGuest(id, request));
    }
}
