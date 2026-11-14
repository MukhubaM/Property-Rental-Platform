package propertyrental.platform.listing.api.controller;

import java.util.List;

import jakarta.validation.Valid;

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

import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;
import propertyrental.platform.listing.api.dto.request.UpdatePropertyTypeRequest;
import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.service.PropertyTypeService;

@RestController
@RequestMapping("/api/v1/property-types")
@RequiredArgsConstructor
@Tag(
        name = "Property Types",
        description = "APIs for managing property types available on the platform."
)
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    @GetMapping
    @Operation(
            summary = "Get all property types",
            description = "Retrieves a list of all property types available on the platform."
    )
    public List<PropertyTypeResponse> getAllPropertyTypes() {

        return propertyTypeService.getAllPropertyTypes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a property type",
            description = "Creates a new property type using the information provided in the request body."
    )
    public PropertyTypeResponse createPropertyType(
            @Valid @RequestBody CreatePropertyTypeRequest request
    ) {

        return propertyTypeService.createPropertyType(request);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get property type by ID",
            description = "Retrieves a specific property type using its unique identifier."
    )
    public PropertyTypeResponse getPropertyTypeById(@PathVariable("id") Long id) {

        return propertyTypeService.getPropertyTypeById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a property type",
            description = "Deletes an existing property type using its unique identifier."
    )
    public void deletePropertyType(@PathVariable("id") Long id) {

        propertyTypeService.deletePropertyType(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a property type",
            description = "Updates an existing property type identified by its ID using the information provided in the request body."
    )
    public PropertyTypeResponse updatePropertyType(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdatePropertyTypeRequest request
    ) {

        return propertyTypeService.updatePropertyType(id, request);
    }
}