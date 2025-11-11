package com.vedicpooja.catalog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCatalogService {

    private final PoojaServiceRepository poojaServiceRepository;

    public ServiceCatalogService(PoojaServiceRepository poojaServiceRepository) {
        this.poojaServiceRepository = poojaServiceRepository;
    }

    public List&lt;PoojaService&gt; listAll() {
        return poojaServiceRepository.findAll();
    }

    public PoojaService create(String name, String description, Integer durationMinutes, Integer basePriceCents, String currency) {
        PoojaService s = PoojaService.builder()
                .name(name)
                .description(description)
                .durationMinutes(durationMinutes)
                .basePriceCents(basePriceCents)
                .currency(currency == null ? "INR" : currency)
                .active(true)
                .build();
        return poojaServiceRepository.save(s);
    }
}