package propertyrental.platform.location.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProvinceRequest(
        @NotBlank(message = "code must not be blank")
        @Size(max = 10, message = "code must be at most 10 characters")
        String code,

        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name
) {}