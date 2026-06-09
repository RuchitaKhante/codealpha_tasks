package com.hotel.controller;

import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mybookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    // ==============================
    // 1. SHOW BOOKING FORM
    // ==============================
    @GetMapping("/new/{roomId}")
    public String showBookingForm(@PathVariable Long roomId, Model model) {

        Optional<Room> roomOpt = roomService.getRoomById(roomId);

        if (roomOpt.isEmpty()) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", roomOpt.get());
        model.addAttribute("booking", new Booking());
        model.addAttribute("today", LocalDate.now());

        // IMPORTANT: separate page for booking form
        return "booking-form";
    }

    // ==============================
    // 2. CONFIRM BOOKING
    // ==============================
    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam Long roomId,
            @RequestParam String guestName,
            @RequestParam String guestEmail,
            @RequestParam String guestPhone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam int numGuests) {

        Optional<Room> roomOpt = roomService.getRoomById(roomId);

        if (roomOpt.isEmpty()) {
            return "redirect:/rooms";
        }

        Booking booking = new Booking();
        booking.setRoom(roomOpt.get());
        booking.setGuestName(guestName);
        booking.setGuestEmail(guestEmail);
        booking.setGuestPhone(guestPhone);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumGuests(numGuests);

        Booking savedBooking = bookingService.createBooking(booking);

        // redirect to payment page
        return "redirect:/payment/" + savedBooking.getId();
    }

    // ==============================
    // 3. MY BOOKINGS PAGE (IMPORTANT)
    // ==============================
    @GetMapping
    public String myBookings(@RequestParam(required = false) String email, Model model) {

        List<Booking> bookings;

        if (email != null && !email.isBlank()) {
            bookings = bookingService.getBookingsByEmail(email);
            model.addAttribute("email", email);
        } else {
            bookings = bookingService.getAllBookings();
        }

        model.addAttribute("bookings", bookings);

        return "mybookings";
    }

    // ==============================
    // 4. CANCEL BOOKING
    // ==============================
    @PostMapping("/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String email) {

        bookingService.cancelBooking(id);

        if (email != null && !email.isBlank()) {
            return "redirect:/mybookings?email=" + email;
        }

        return "redirect:/mybookings";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {

        bookingService.deleteBooking(id);

        return "redirect:/mybookings";
    }
}