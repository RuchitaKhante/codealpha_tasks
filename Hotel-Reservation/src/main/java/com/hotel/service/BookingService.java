package com.hotel.service;

import com.hotel.controller.BookingController;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    // Make a new booking
    public Booking createBooking(Booking booking) {
        // Calculate total amount = pricePerNight × number of nights
        Room room = booking.getRoom();
        long nights = booking.getNumNights();
        double total = room.getPricePerNight() * nights;
        booking.setTotalAmount(total);

        // Set status to PENDING (confirmed after payment)
        booking.setStatus(Booking.BookingStatus.PENDING);

        // Mark room as unavailable
        roomService.markRoomUnavailable(room.getId());

        return bookingRepository.save(booking);
    }

    // Confirm booking (after payment)
    public Booking confirmBooking(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isPresent()) {
            Booking booking = opt.get();
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            return bookingRepository.save(booking);
        }
        return null;
    }

    // Cancel a booking
    public boolean cancelBooking(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isPresent()) {
            Booking booking = opt.get();
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Free up the room
            roomService.markRoomAvailable(booking.getRoom().getId());
            return true;
        }
        return false;
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get bookings by email
    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    // Get booking by ID
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Transactional
    public void deleteBooking(Long id) {

        paymentRepository.deleteByBookingId(id); // delete child first
        bookingRepository.deleteById(id);        // then parent
    }
}