package propertyrental.platform.identity.api;

import org.springframework.stereotype.Component;
import propertyrental.platform.identity.domain.RoleEntity;

@Component
public class RoleMapper {

    public RoleResponse toResponse(RoleEntity entity) {

        return new RoleResponse(entity.getId(), entity.getCode(), entity.getName());
    }
}
