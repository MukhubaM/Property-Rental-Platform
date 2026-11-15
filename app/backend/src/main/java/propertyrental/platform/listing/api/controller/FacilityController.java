package propertyrental.platform.listing.api.controller;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import propertyrental.platform.listing.api.dto.request.CreateFacilityRequest;
import propertyrental.platform.listing.api.dto.request.UpdateFacilityRequest;
import propertyrental.platform.listing.api.dto.response.FacilityResponse;
import propertyrental.platform.listing.application.service.FacilityService;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
@Tag(
        name = "Facilities",
        description = "APIs for managing facilities and amenities available for properties and rental units."
)
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    @Operation(
            summary = "Get all facilities",
            description = "Retrieves a list of all facilities available on the platform."
    )
    public List<FacilityResponse> getAllFacilities() {

        return facilityService.getAllFacilities();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get facility by ID",
            description = "Retrieves a specific facility using its unique identifier."
    )
    public FacilityResponse getFacilityById(@PathVariable("id") Long id) {

        return facilityService.getFacilityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a facility",
            description = "Creates a new facility using the information provided in the request body."
    )
    public FacilityResponse createFacility(
            @Valid @RequestBody CreateFacilityRequest request
    ) {

        return facilityService.createFacility(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a facility",
            description = "Updates an existing facility identified by its ID using the information provided in the request body."
    )
    public FacilityResponse updateFacility(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateFacilityRequest request
    ) {

        return facilityService.updateFacility(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a facility",
            description = "Deletes an existing facility using its unique identifier."
    )
    public void deleteFacility(@PathVariable("id") Long id) {

        facilityService.deleteFacility(id);
    }
}