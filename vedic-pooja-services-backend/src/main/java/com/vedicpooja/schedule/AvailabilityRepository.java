package com.vedicpooja.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository&lt;Availability, Long&gt; {
    List&lt;Availability&gt; findByPurohitIdAndDateBetween(Long purohitId, LocalDate start, LocalDate end);
    Optional&lt;Availability&gt; findByPurohitIdAndDateAndTimeSlot(Long purohitId, LocalDate date, String timeSlot);
}