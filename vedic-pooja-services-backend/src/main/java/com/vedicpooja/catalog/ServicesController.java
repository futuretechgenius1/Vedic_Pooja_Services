package com.vedicpooja.catalog;

import com.vedicpooja.catalog.dto.CreateServiceRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServicesController {

    private final ServiceCatalogService catalogService;

    public ServicesController(ServiceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/api/services")
    public ResponseEntity&lt;List&lt;PoojaService&gt;&gt; list() {
        return ResponseEntity.ok(catalogService.listAll());
    }

    @PostMapping("/api/admin/services")
    public ResponseEntity&lt;PoojaService&gt; create(@Valid @RequestBody CreateServiceRequest req) {
        var created = catalogService.create(req.getName(), req.getDescription(), req.getDurationMinutes(), req.getBasePriceCents(), req.getCurrency());
        return ResponseEntity.ok(created);
    }
}