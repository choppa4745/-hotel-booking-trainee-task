package edu.booking.hotel_booking.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private UUID bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Guest guest;
    private Room room;
    private String status;

}