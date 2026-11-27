package propertyrental.platform.media.api.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

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

import propertyrental.platform.media.api.dto.request.CreatePropertyImageRequest;
import propertyrental.platform.media.api.dto.request.UpdatePropertyImageRequest;
import propertyrental.platform.media.api.dto.response.PropertyImageResponse;
import propertyrental.platform.media.application.service.PropertyImageService;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/images")
@RequiredArgsConstructor
public class PropertyImageController {

    private final PropertyImageService propertyImageService;

    @GetMapping
    public List<PropertyImageResponse> getImages(@PathVariable("propertyId") UUID propertyId) {

        return propertyImageService.getImages(propertyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyImageResponse addImage(@PathVariable("propertyId")
                                          UUID propertyId,
                                          @Valid @RequestBody
                                          CreatePropertyImageRequest request) {

        return propertyImageService.addImage(propertyId, request);
    }

    @PutMapping("/{imageId}")
    public PropertyImageResponse updateImage(@PathVariable("propertyId")
                                             UUID propertyId,
                                             @PathVariable("imageId")
                                             UUID imageId,
                                             @Valid @RequestBody
                                             UpdatePropertyImageRequest request) {

        return propertyImageService.updateImage(propertyId, imageId, request);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("propertyId")
                            UUID propertyId,
                            @PathVariable("imageId")
                            UUID imageId) {

        propertyImageService.deleteImage(propertyId, imageId);
    }
}