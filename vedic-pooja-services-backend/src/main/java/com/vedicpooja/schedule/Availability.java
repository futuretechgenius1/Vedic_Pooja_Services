package com.vedicpooja.schedule;

import com.vedicpooja.purohit.Purohit;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "availability",
        uniqueConstraints = @UniqueConstraint(name = "uq_availability", columnNames = {"purohit_id","date","time_slot"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "purohit_id", nullable = false)
    private Purohit purohit;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot; // e.g. "09:00-11:00"

    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

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