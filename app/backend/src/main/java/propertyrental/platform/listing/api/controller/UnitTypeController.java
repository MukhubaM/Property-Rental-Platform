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

import propertyrental.platform.listing.api.dto.request.CreateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.request.UpdateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.response.UnitTypeResponse;
import propertyrental.platform.listing.application.service.UnitTypeService;

@RestController
@RequestMapping("/api/v1/unit-types")
@RequiredArgsConstructor
public class UnitTypeController {

    private final UnitTypeService unitTypeService;

    @GetMapping
    public List<UnitTypeResponse> getAllUnitTypes() {

        return unitTypeService.getAllUnitTypes();
    }

    @GetMapping("/{id}")
    public UnitTypeResponse getUnitTypeById(@PathVariable("id") Long id) {

        return unitTypeService.getUnitTypeById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitTypeResponse createUnitType(@Valid @RequestBody CreateUnitTypeRequest request) {

        return unitTypeService.createUnitType(request);
    }

    @PutMapping("/{id}")
    public UnitTypeResponse updateUnitType(@PathVariable("id")
                                           Long id,
                                           @Valid @RequestBody
                                           UpdateUnitTypeRequest request) {
        return unitTypeService.updateUnitType(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnitType(@PathVariable("id") Long id) {
        unitTypeService.deleteUnitType(id);
    }
}