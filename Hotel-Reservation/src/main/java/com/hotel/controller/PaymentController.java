package com.hotel.controller;

import com.hotel.entity.Booking;
import com.hotel.entity.Payment;
import com.hotel.service.BookingService;
import com.hotel.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    // Show payment page for a booking
    @GetMapping("/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        if (booking.isEmpty()) return "redirect:/rooms";

        model.addAttribute("booking", booking.get());
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
        return "payment";
    }

    // Process the payment
    @PostMapping("/process")
    public String processPayment(@RequestParam Long bookingId,
                                 @RequestParam String paymentMethod,
                                 Model model) {
        Payment payment = paymentService.processPayment(bookingId, paymentMethod);
        Booking booking = payment.getBooking();

        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "payment-success";
    }
}