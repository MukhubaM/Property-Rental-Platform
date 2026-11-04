package propertyrental.platform.identity.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(       // Removed role id, since it's taken from the URL path (PUT /api/v1/roles/{id}), it is not part of the request body
        @NotBlank(message = "code must not be blank")
        @Size(max = 50, message = "code must be at most 50 characters")
        String code,

        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name
) {}

