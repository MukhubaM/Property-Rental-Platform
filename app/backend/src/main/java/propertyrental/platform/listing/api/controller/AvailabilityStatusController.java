package propertyrental.platform.listing.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;

@RestController
@RequestMapping("/api/v1/availability-statuses")
@RequiredArgsConstructor
@Tag(
        name = "Availability Statuses",
        description = "APIs for retrieving the availability statuses of rental units."
)
public class AvailabilityStatusController {

    private final AvailabilityStatusService availabilityStatusService;

    @GetMapping
    @Operation(
            summary = "Get all availability statuses",
            description = "Retrieves a list of all availability statuses available on the platform."
    )
    public List<AvailabilityStatusResponse> getAllAvailabilityStatuses() {

        return availabilityStatusService.getAllAvailabilityStatuses();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get availability status by ID",
            description = "Retrieves a specific availability status using its unique identifier."
    )
    public AvailabilityStatusResponse getAvailabilityStatusById(@PathVariable("id") Long id) {

        return availabilityStatusService.getAvailabilityStatusById(id);
    }
}