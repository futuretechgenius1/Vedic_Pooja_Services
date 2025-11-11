package com.vedicpooja.booking;

import com.vedicpooja.booking.dto.BookingHoldRequest;
import com.vedicpooja.booking.dto.BookingHoldResponse;
import com.vedicpooja.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/hold")
    public ResponseEntity&lt;BookingHoldResponse&gt; hold(Authentication authentication,
                                                      @Valid @RequestBody BookingHoldRequest request) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(service.holdBooking(user, request));
    }
}