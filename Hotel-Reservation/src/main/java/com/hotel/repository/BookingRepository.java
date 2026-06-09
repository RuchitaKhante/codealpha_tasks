package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings by guest email
    List<Booking> findByGuestEmail(String guestEmail);

    // Find bookings by status
    List<Booking> findByStatus(Booking.BookingStatus status);
}