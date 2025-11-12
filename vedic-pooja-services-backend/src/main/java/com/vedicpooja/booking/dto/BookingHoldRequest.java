package com.vedicpooja.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingHoldRequest {
    @NotNull
    private Long purohitId;
    @NotNull
    private Long serviceId;
    @NotNull
    private String desiredStart; // ISO-8601, e.g., 2025-12-10T04:30:00Z

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;

    private String notes;
}