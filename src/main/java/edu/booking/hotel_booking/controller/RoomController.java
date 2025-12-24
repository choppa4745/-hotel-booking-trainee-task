package edu.booking.hotel_booking.controller;

import edu.booking.hotel_booking.dto.RoomDto;
import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.entity.Room;
import edu.booking.hotel_booking.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService roomService) {
        this.service = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok(service.getAllRooms(page, size));
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRoom(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable UUID id, @RequestBody RoomDto request) {
        return ResponseEntity.ok(service.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteRoom(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Поиск свободных комнат на интервал дат.
     * Пример запроса:
     * GET /api/rooms/free?checkInDate=2025-01-10&checkOutDate=2025-01-12&minGuests=2
     */
    @GetMapping("/free")
    public List<Room> freeRooms(
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(required = false) Integer minGuests
    ) {
        return service.findFreeRooms(checkInDate, checkOutDate, minGuests);
    }
}
