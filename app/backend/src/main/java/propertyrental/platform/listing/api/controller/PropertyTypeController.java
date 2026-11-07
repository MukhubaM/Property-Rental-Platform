package propertyrental.platform.listing.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.service.PropertyTypeService;
import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;

@RestController
@RequestMapping("/api/v1/property-types")
@RequiredArgsConstructor
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    @GetMapping
    public List<PropertyTypeResponse> getAllPropertyTypes() {

        return propertyTypeService.getAllPropertyTypes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyTypeResponse createPropertyType(@Valid @RequestBody CreatePropertyTypeRequest request) {

        return propertyTypeService.createPropertyType(request);
        }

    @GetMapping("/{id}")
    public PropertyTypeResponse getPropertyTypeById(@PathVariable("id") Long id) {

        return propertyTypeService.getPropertyTypeById(id);
    }
}