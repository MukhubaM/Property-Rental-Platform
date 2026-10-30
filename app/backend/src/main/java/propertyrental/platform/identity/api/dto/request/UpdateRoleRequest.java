package propertyrental.platform.identity.api.dto.request;

public record UpdateRoleRequest(
        Long id,
        String code,
        String name
) {}

