package com.vedicpooja.catalog;

import com.vedicpooja.purohit.Purohit;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "purohit_services", uniqueConstraints = {
        @UniqueConstraint(name = "uq_purohit_service", columnNames = {"purohit_id", "service_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurohitServiceOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "purohit_id", nullable = false)
    private Purohit purohit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private PoojaService service;

    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false)
    private Boolean active = true;

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