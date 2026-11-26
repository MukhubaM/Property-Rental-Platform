package propertyrental.platform.location.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// The city id is taken from the URL path (PUT /api/v1/cities/{id}), it is not part of the request body
public record UpdateCityRequest(
        @NotNull(message = "provinceId must not be null")
        Long provinceId,

        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name
) {}
