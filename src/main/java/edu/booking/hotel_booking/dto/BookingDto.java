package edu.booking.hotel_booking.dto;

import edu.booking.hotel_booking.entity.Guest;
import edu.booking.hotel_booking.entity.Room;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {
    private String checkInDate;
    private String checkOutDate;
    private Guest guest;
    private Room room;
}