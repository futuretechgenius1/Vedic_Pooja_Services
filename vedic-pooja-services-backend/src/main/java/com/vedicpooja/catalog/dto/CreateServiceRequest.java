package com.vedicpooja.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateServiceRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(15)
    private Integer durationMinutes;

    @NotNull
    @Min(0)
    private Integer basePriceCents;

    private String currency = "INR";
}