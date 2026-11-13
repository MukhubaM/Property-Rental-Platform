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

import propertyrental.platform.listing.api.dto.request.CreateFacilityRequest;
import propertyrental.platform.listing.api.dto.request.UpdateFacilityRequest;
import propertyrental.platform.listing.api.dto.response.FacilityResponse;
import propertyrental.platform.listing.application.service.FacilityService;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public List<FacilityResponse> getAllFacilities() {

        return facilityService.getAllFacilities();
    }

    @GetMapping("/{id}")
    public FacilityResponse getFacilityById(@PathVariable("id") Long id) {

        return facilityService.getFacilityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityResponse createFacility(@Valid @RequestBody CreateFacilityRequest request) {

        return facilityService.createFacility(request);
    }

    @PutMapping("/{id}")
    public FacilityResponse updateFacility(@PathVariable("id")
                                           Long id,
                                           @Valid @RequestBody
                                           UpdateFacilityRequest request) {

        return facilityService.updateFacility(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFacility(@PathVariable("id") Long id) {
        facilityService.deleteFacility(id);
    }
}