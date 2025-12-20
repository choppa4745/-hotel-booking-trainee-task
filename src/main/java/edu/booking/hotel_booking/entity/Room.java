package edu.booking.hotel_booking.entity;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomEntity {
    private UUID roomId;
    private int floor;
    private String number;
    private int numberOfGuests;
}