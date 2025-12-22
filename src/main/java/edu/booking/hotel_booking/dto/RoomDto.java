package edu.booking.hotel_booking.dto;

import lombok.Data;

@Data
public class RoomDto {
    private int floor;
    private String number;
    private int numberOfGuests;
}