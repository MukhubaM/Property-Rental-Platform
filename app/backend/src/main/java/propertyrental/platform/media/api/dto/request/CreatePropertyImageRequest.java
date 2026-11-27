package propertyrental.platform.media.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePropertyImageRequest(
        @NotBlank(message = "imageUrl must not be blank")
        @Size(max = 500, message = "imageUrl must be at most 500 characters")
        @Pattern(regexp = "^https?://\\S+$", message = "imageUrl must be an http or https URL")
        String imageUrl,

        @Size(max = 255, message = "altText must be at most 255 characters")
        String altText,

        @NotNull(message = "displayOrder must not be null")
        @Min(value = 0, message = "displayOrder must be zero or more")
        Integer displayOrder
) {}