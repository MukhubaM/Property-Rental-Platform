package propertyrental.platform.listing.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// The facility id is taken from the URL path (PUT /api/v1/facilities/{id}), it is not part of the request body
public record UpdateFacilityRequest(

        @NotBlank(message = "code must not be blank")
        @Size(max = 50, message = "code must be at most 50 characters")
        String code,

        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name
) {}