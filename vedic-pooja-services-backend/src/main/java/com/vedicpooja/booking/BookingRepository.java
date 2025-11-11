package com.vedicpooja.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository&lt;Booking, Long&gt; {
    List&lt;Booking&gt; findByPurohitIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long purohitId, OffsetDateTime start, OffsetDateTime end);
}