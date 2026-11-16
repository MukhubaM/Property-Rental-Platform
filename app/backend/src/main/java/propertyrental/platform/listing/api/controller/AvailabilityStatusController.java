package propertyrental.platform.listing.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;
import propertyrental.platform.listing.api.dto.request.CreateAvailabilityStatusRequest;
import propertyrental.platform.listing.api.dto.request.UpdateAvailabilityStatusRequest;

@RestController
@RequestMapping("/api/v1/availability-statuses")
@RequiredArgsConstructor
@Tag(name = "Availability Statuses",
    description = "APIs for retrieving the availability statuses of rental units."
)
public class AvailabilityStatusController {

    private final AvailabilityStatusService availabilityStatusService;

    @GetMapping
    @Operation(summary = "Get all availability statuses",
               description = "Retrieves a list of all availability statuses available on the platform."
    )
    public List<AvailabilityStatusResponse> getAllAvailabilityStatuses() {

        return availabilityStatusService.getAllAvailabilityStatuses();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get availability status by ID",
               description = "Retrieves a specific availability status using its unique identifier."
    )
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityStatusResponse createAvailabilityStatus(@Valid @RequestBody CreateAvailabilityStatusRequest request) {

        return availabilityStatusService.createAvailabilityStatus(request);
    }
}