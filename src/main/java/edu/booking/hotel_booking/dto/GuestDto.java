package edu.booking.hotel_booking.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDto {
    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private String phone;
}