package com.vedicpooja.booking;

import com.vedicpooja.booking.dto.BookingHoldRequest;
import com.vedicpooja.booking.dto.BookingHoldResponse;
import com.vedicpooja.catalog.PoojaService;
import com.vedicpooja.catalog.PoojaServiceRepository;
import com.vedicpooja.catalog.PurohitServiceOfferingRepository;
import com.vedicpooja.purohit.Purohit;
import com.vedicpooja.purohit.PurohitRepository;
import com.vedicpooja.schedule.AvailabilityRepository;
import com.vedicpooja.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PurohitRepository purohitRepository;
    private final PoojaServiceRepository serviceRepository;
    private final PurohitServiceOfferingRepository offeringRepository;
    private final AvailabilityRepository availabilityRepository;

    public BookingService(BookingRepository bookingRepository,
                          PurohitRepository purohitRepository,
                          PoojaServiceRepository serviceRepository,
                          PurohitServiceOfferingRepository offeringRepository,
                          AvailabilityRepository availabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.purohitRepository = purohitRepository;
        this.serviceRepository = serviceRepository;
        this.offeringRepository = offeringRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public BookingHoldResponse holdBooking(User user, BookingHoldRequest req) {
        Purohit purohit = purohitRepository.findById(req.getPurohitId())
                .orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid purohit"));
        PoojaService service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid service"));

        // verify that the purohit offers this service
        offeringRepository.findByPurohitIdAndServiceId(purohit.getId(), service.getId())
                .orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service not offered by selected purohit"));

        OffsetDateTime start;
        try {
            start = OffsetDateTime.parse(req.getDesiredStart());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid desiredStart");
        }
        OffsetDateTime end = start.plusMinutes(service.getDurationMinutes());

        // Check availability for that exact slot string
        String slot = start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                + "-" + end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        var avail = availabilityRepository.findByPurohitIdAndDateAndTimeSlot(purohit.getId(), start.toLocalDate(), slot)
                .orElseThrow(() -&gt; new ResponseStatusException(HttpStatus.CONFLICT, "Slot not available"));
        if (!avail.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot not available");
        }

        // overlap check with existing bookings
        List&lt;Booking&gt; overlaps = bookingRepository.findByPurohitIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                purohit.getId(), end, start
        );
        if (!overlaps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Overlapping booking exists");
        }

        Booking booking = Booking.builder()
                .user(user)
                .purohit(purohit)
                .service(service)
                .bookingDate(start.toLocalDate())
                .startTime(start)
                .endTime(end)
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .city(req.getCity())
                .state(req.getState())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .notes(req.getNotes())
                .status(BookingStatus.HOLD)
                .holdExpiresAt(OffsetDateTime.now().plusMinutes(10))
                .build();

        bookingRepository.save(booking);

        return new BookingHoldResponse(booking.getId(), booking.getStatus().name(),
                booking.getHoldExpiresAt() == null ? null : booking.getHoldExpiresAt().toString());
    }
}