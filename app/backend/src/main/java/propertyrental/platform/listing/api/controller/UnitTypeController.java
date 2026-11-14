
package propertyrental.platform.listing.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Unit Types",
        description = "APIs for managing rental unit types, including creating, retrieving, updating, and deleting unit types."
)
public class UnitTypeController {

    private final UnitTypeService unitTypeService;

    @GetMapping
    @Operation(
            summary = "Get all unit types",
            description = "Retrieves a list of all available rental unit types."
    )
    public List<UnitTypeResponse> getAllUnitTypes() {
        return unitTypeService.getAllUnitTypes();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get unit type by ID",
            description = "Retrieves a specific rental unit type using its unique identifier."
    )
    public UnitTypeResponse getUnitTypeById(@PathVariable("id") Long id) {
        return unitTypeService.getUnitTypeById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a unit type",
            description = "Creates a new rental unit type using the information provided in the request body."
    )
    public UnitTypeResponse createUnitType(
            @Valid @RequestBody CreateUnitTypeRequest request) {
        return unitTypeService.createUnitType(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a unit type",
            description = "Updates an existing rental unit type identified by its ID using the information provided in the request body."
    )
    public UnitTypeResponse updateUnitType(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUnitTypeRequest request) {
        return unitTypeService.updateUnitType(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a unit type",
            description = "Deletes an existing rental unit type using its unique identifier."
    )
    public void deleteUnitType(@PathVariable("id") Long id) {
        unitTypeService.deleteUnitType(id);
    }
}
