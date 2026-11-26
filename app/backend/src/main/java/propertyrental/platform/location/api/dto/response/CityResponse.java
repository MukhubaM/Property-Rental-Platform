package propertyrental.platform.location.api.dto.response;

public record CityResponse(
        Long id,
        Long provinceId,
        String name
) {}
