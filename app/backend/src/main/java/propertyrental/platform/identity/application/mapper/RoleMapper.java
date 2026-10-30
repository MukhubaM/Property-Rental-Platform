package propertyrental.platform.identity.application.mapper;

import org.springframework.stereotype.Component;
import propertyrental.platform.identity.api.dto.request.CreateRoleRequest;
import propertyrental.platform.identity.api.dto.request.UpdateRoleRequest;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.infrastructure.persistence.entity.RoleEntity;

@Component
public class RoleMapper {

    public RoleEntity toEntity(CreateRoleRequest request){
        RoleEntity role = new RoleEntity();

        role.setCode(request.code());
        role.setName(request.name());

        return role;
    }

    public RoleEntity toEntity(UpdateRoleRequest request){
        RoleEntity role = new RoleEntity();

        role.setId(request.id());
        role.setCode(request.code());
        role.setName(request.name());

        return role;
    }

    public RoleResponse toResponse(RoleEntity role) {
        return new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getName()
        );
    }
}
