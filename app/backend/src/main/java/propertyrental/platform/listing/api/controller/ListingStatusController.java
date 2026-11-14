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

import propertyrental.platform.listing.api.dto.request.CreateListingStatusRequest;
import propertyrental.platform.listing.api.dto.request.UpdateListingStatusRequest;
import propertyrental.platform.listing.api.dto.response.ListingStatusResponse;
import propertyrental.platform.listing.application.service.ListingStatusService;

@RestController
@RequestMapping("/api/v1/listing-statuses")
@RequiredArgsConstructor
@Tag(
        name = "Listing Statuses",
        description = "APIs for managing the statuses used to track property listing states on the platform."
)
public class ListingStatusController {

    private final ListingStatusService listingStatusService;

    @GetMapping
    @Operation(
            summary = "Get all listing statuses",
            description = "Retrieves a list of all listing statuses available on the platform."
    )
    public List<ListingStatusResponse> getAllListingStatuses() {

        return listingStatusService.getAllListingStatuses();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get listing status by ID",
            description = "Retrieves a specific listing status using its unique identifier."
    )
    public ListingStatusResponse getListingStatusById(@PathVariable("id") Long id) {

        return listingStatusService.getListingStatusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a listing status",
            description = "Creates a new listing status using the information provided in the request body."
    )
    public ListingStatusResponse createListingStatus(
            @Valid @RequestBody CreateListingStatusRequest request
    ) {

        return listingStatusService.createListingStatus(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a listing status",
            description = "Updates an existing listing status identified by its ID using the information provided in the request body."
    )
    public ListingStatusResponse updateListingStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateListingStatusRequest request
    ) {

        return listingStatusService.updateListingStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a listing status",
            description = "Deletes an existing listing status using its unique identifier."
    )
    public void deleteListingStatus(@PathVariable("id") Long id) {

        listingStatusService.deleteListingStatus(id);
    }
}