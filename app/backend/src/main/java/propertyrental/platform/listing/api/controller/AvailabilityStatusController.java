package propertyrental.platform.listing.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;

@RestController
@RequestMapping("/api/v1/availability-statuses")
@RequiredArgsConstructor
public class AvailabilityStatusController {

    private final AvailabilityStatusService availabilityStatusService;

    @GetMapping
    public List<AvailabilityStatusResponse> getAllAvailabilityStatuses() {

        return availabilityStatusService.getAllAvailabilityStatuses();
    }

    @GetMapping("/{id}")
    public AvailabilityStatusResponse getAvailabilityStatusById(@PathVariable("id") Long id) {

        return availabilityStatusService.getAvailabilityStatusById(id);
    }
}