package edu.booking.hotel_booking.entity;


import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestEntity {
    private UUID guestId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String date_of_birth;
    private String phone;
}