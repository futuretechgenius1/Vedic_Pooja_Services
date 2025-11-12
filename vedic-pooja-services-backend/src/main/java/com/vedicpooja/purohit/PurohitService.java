package com.vedicpooja.purohit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedicpooja.auth.Role;
import com.vedicpooja.purohit.dto.OnboardPurohitRequest;
import com.vedicpooja.schedule.Availability;
import com.vedicpooja.schedule.AvailabilityRepository;
import com.vedicpooja.schedule.dto.BulkUpsertAvailabilityRequest;
import com.vedicpooja.user.User;
import com.vedicpooja.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class PurohitService {

    private final PurohitRepository purohitRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public PurohitService(PurohitRepository purohitRepository,
                          AvailabilityRepository availabilityRepository,
                          UserRepository userRepository,
                          ObjectMapper objectMapper) {
        this.purohitRepository = purohitRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Purohit onboard(User user, OnboardPurohitRequest req) {
        if (purohitRepository.findByUser(user).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Purohit profile already exists");
        }
        String languagesJson = null;
        if (req.getLanguages() != null) {
            try {
                languagesJson = objectMapper.writeValueAsString(req.getLanguages());
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid languages");
            }
        }

        Purohit p = Purohit.builder()
                .user(user)
                .experienceYears(req.getExperienceYears())
                .specialization(req.getSpecialization())
                .bio(req.getBio())
                .languagesJson(languagesJson)
                .locationCity(req.getLocationCity())
                .locationState(req.getLocationState())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .serviceRadiusKm(req.getServiceRadiusKm())
                .status(PurohitStatus.PENDING)
                .build();
        Purohit saved = purohitRepository.save(p);

        // elevate user role to PUROHIT if not admin
        if (user.getRole() == Role.USER) {
            user.setRole(Role.PUROHIT);
            userRepository.save(user);
        }
        return saved;
    }

    @Transactional
    public int upsertAvailability(User user, BulkUpsertAvailabilityRequest req) {
        Purohit purohit = purohitRepository.findByUser(user)
                .orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purohit profile not found"));

        int count = 0;
        for (var item : req.getSlots()) {
            LocalDate date;
            try {
                date = LocalDate.parse(item.getDate());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date: " + item.getDate());
            }
            String timeSlot = item.getTimeSlot();
            var existingOpt = availabilityRepository.findByPurohitIdAndDateAndTimeSlot(purohit.getId(), date, timeSlot);
            Availability entity = existingOpt.orElseGet(() -&gt; Availability.builder()
                    .purohit(purohit)
                    .date(date)
                    .timeSlot(timeSlot)
                    .build());
            entity.setAvailable(item.getIsAvailable() == null ? Boolean.TRUE : item.getIsAvailable());
            availabilityRepository.save(entity);
            count++;
        }
        return count;
    }
}