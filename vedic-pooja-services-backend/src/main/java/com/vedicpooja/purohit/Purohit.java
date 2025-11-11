package com.vedicpooja.purohit;

import com.vedicpooja.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "purohits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purohit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 255)
    private String specialization;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // Store languages as comma-separated for simplicity (can be JSON mapping)
    @Column(name = "languages", columnDefinition = "JSON")
    private String languagesJson;

    @Column(name = "location_city", length = 120)
    private String locationCity;

    @Column(name = "location_state", length = 120)
    private String locationState;

    private Double latitude;
    private Double longitude;

    @Column(name = "service_radius_km")
    private Integer serviceRadiusKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurohitStatus status = PurohitStatus.PENDING;

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