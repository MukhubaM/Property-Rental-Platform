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

import propertyrental.platform.listing.api.dto.request.CreateListingStatusRequest;
import propertyrental.platform.listing.api.dto.request.UpdateListingStatusRequest;
import propertyrental.platform.listing.api.dto.response.ListingStatusResponse;
import propertyrental.platform.listing.application.service.ListingStatusService;

@RestController
@RequestMapping("/api/v1/listing-statuses")
@RequiredArgsConstructor
public class ListingStatusController {

    private final ListingStatusService listingStatusService;

    @GetMapping
    public List<ListingStatusResponse> getAllListingStatuses() {

        return listingStatusService.getAllListingStatuses();
    }

    @GetMapping("/{id}")
    public ListingStatusResponse getListingStatusById(@PathVariable("id") Long id) {

        return listingStatusService.getListingStatusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingStatusResponse createListingStatus(@Valid @RequestBody CreateListingStatusRequest request) {

        return listingStatusService.createListingStatus(request);
    }

    @PutMapping("/{id}")
    public ListingStatusResponse updateListingStatus(@PathVariable("id")
                                                     Long id,
                                                     @Valid @RequestBody
                                                     UpdateListingStatusRequest request) {

        return listingStatusService.updateListingStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteListingStatus(@PathVariable("id") Long id) {

        listingStatusService.deleteListingStatus(id);
    }
}