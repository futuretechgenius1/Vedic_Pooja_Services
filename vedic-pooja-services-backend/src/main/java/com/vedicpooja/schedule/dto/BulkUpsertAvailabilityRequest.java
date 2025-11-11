package com.vedicpooja.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkUpsertAvailabilityRequest {
    @NotNull
    private List&lt;Item&gt; slots;

    @Data
    public static class Item {
        @NotBlank
        private String date; // ISO-8601 yyyy-MM-dd
        @NotBlank
        private String timeSlot; // e.g. 09:00-11:00
        private Boolean isAvailable = true;
    }
}