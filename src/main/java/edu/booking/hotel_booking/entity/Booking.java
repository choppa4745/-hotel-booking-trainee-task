package edu.booking.hotel_booking.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {
    private UUID bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private GuestEntity guest;
    private RoomEntity room;

}