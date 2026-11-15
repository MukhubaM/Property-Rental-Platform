package propertyrental.platform.listing.api.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;
import propertyrental.platform.listing.api.dto.request.UpdateAvailabilityStatusRequest;

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

    @PutMapping("/{id}")
    public AvailabilityStatusResponse updateAvailabilityStatus(@PathVariable("id")
                                                               Long id,
                                                               @Valid @RequestBody
                                                               UpdateAvailabilityStatusRequest request) {

        return availabilityStatusService.updateAvailabilityStatus(id, request);
    }
}