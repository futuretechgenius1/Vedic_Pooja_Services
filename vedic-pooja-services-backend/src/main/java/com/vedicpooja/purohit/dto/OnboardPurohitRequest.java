package com.vedicpooja.purohit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OnboardPurohitRequest {
    @Min(0)
    private Integer experienceYears;

    @NotBlank
    private String specialization;

    private String bio;

    private List&lt;String&gt; languages;

    private String locationCity;
    private String locationState;
    private Double latitude;
    private Double longitude;

    @Min(0)
    private Integer serviceRadiusKm = 50;
}