CREATE SCHEMA IF NOT EXISTS hotel;

CREATE TABLE IF NOT EXISTS hotel.guest (
    guest_id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE NOT NULL,
    phone VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS hotel.room (
    room_id UUID PRIMARY KEY,
    floor INT NOT NULL,
    number VARCHAR(10) NOT NULL UNIQUE,
    number_of_guests INT NOT NULL
    );

CREATE TABLE IF NOT EXISTS hotel.booking (
     booking_id UUID PRIMARY KEY,
     check_in_date DATE NOT NULL,
     check_out_date DATE NOT NULL,
     guest_id UUID NOT NULL REFERENCES hotel.guest(guest_id),
    room_id UUID NOT NULL REFERENCES hotel.room(room_id),
    status VARCHAR(10) NOT NULL
    );
