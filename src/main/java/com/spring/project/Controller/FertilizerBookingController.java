package com.spring.project.Controller;

import com.spring.project.entity.FertilizerBooking;
import com.spring.project.repository.FertilizerBookingRepository;
import com.spring.project.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "${frontend.url}")
public class FertilizerBookingController {

    @Autowired
    private FertilizerBookingRepository bookingRepository;

    @Autowired
    private MailService mailService;  // ✅ Injected MailService

    @PostMapping
    public String createBooking(@RequestBody FertilizerBooking booking) {
        bookingRepository.save(booking);

        // ✅ Send fertilizer booking confirmation email
        mailService.sendFertilizerBookingEmail(
                booking.getEmail(),
                booking.getCustomerName(),
                booking.getProductName()
        );

        return "Booking successful!";
    }
}
