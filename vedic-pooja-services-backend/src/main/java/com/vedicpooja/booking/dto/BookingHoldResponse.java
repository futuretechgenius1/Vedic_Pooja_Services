package com.vedicpooja.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingHoldResponse {
    private Long bookingId;
    private String status;
    private String holdExpiresAt;
}