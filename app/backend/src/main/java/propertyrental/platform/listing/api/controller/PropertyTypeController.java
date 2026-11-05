package propertyrental.platform.listing.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.service.PropertyTypeService;

@RestController
@RequestMapping("/api/v1/property-types")
@RequiredArgsConstructor
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;

    @GetMapping
    public List<PropertyTypeResponse> getAllPropertyTypes() {

        return propertyTypeService.getAllPropertyTypes();
    }


    @GetMapping("/{id}")
    public PropertyTypeResponse getPropertyTypeById(@PathVariable("id") Long id) {

        return propertyTypeService.getPropertyTypeById(id);
    }
}