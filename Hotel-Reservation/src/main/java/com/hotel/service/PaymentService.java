package com.hotel.service;

import com.hotel.entity.Booking;
import com.hotel.entity.Payment;
import com.hotel.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingService bookingService;

    // Simulate payment processing
    public Payment processPayment(Long bookingId, String methodStr) {
        Booking booking = bookingService.getBookingById(bookingId).orElseThrow();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentTime(LocalDateTime.now());

        // Convert string to enum
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(methodStr.toUpperCase()));

        // Simulate: 90% chance of success (for demo, always SUCCESS)
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Payment saved = paymentRepository.save(payment);

        // Confirm the booking after successful payment
        if (payment.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
            bookingService.confirmBooking(bookingId);
        }

        return saved;
    }

    // Get payment details by booking ID
    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElse(null);
    }
}