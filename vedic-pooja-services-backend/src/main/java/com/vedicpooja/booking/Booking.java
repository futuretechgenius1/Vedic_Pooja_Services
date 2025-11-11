package com.vedicpooja.booking;

import com.vedicpooja.catalog.PoojaService;
import com.vedicpooja.purohit.Purohit;
import com.vedicpooja.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "purohit_id", nullable = false)
    private Purohit purohit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private PoojaService service;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "address_line1")
    private String addressLine1;
    @Column(name = "address_line2")
    private String addressLine2;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.HOLD;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "hold_expires_at")
    private OffsetDateTime holdExpiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}