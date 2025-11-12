package com.vedicpooja.purohit;

import com.vedicpooja.purohit.dto.OnboardPurohitRequest;
import com.vedicpooja.schedule.dto.BulkUpsertAvailabilityRequest;
import com.vedicpooja.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purohits")
public class PurohitController {

    private final PurohitService service;

    public PurohitController(PurohitService service) {
        this.service = service;
    }

    @PostMapping("/onboard")
    public ResponseEntity&lt;Purohit&gt; onboard(Authentication auth, @Valid @RequestBody OnboardPurohitRequest req) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(service.onboard(user, req));
    }

    @PostMapping("/me/availability")
    public ResponseEntity&lt;String&gt; upsertAvailability(Authentication auth, @Valid @RequestBody BulkUpsertAvailabilityRequest req) {
        User user = (User) auth.getPrincipal();
        int count = service.upsertAvailability(user, req);
        return ResponseEntity.ok("{\"updated\":" + count + "}");
    }
}