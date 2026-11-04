package propertyrental.platform.identity.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propertyrental.platform.identity.api.dto.request.UpdateRoleRequest;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.mapper.RoleMapper;
import propertyrental.platform.identity.domain.RoleAlreadyExistsException;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.infrastructure.persistence.entity.RoleEntity;
import propertyrental.platform.identity.infrastructure.persistence.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    public RoleResponse getRoleById(Long id) {

        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request){

        // This load the role to update or fail with 404 if the id doesn't exist
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));

        // This one rejects the update (409) if another role already uses the same code or name
        if (roleRepository.existsByCodeAndIdNot(request.code(), id)){
            throw new RoleAlreadyExistsException("code", request.code());
        }
        if (roleRepository.existsByNameAndIdNot(request.name(), id)){
            throw new RoleAlreadyExistsException("name", request.name());
        }

        // This apply the new values to the existing entity and save it
        roleMapper.updateEntity(role, request);
        return roleMapper.toResponse(roleRepository.save(role));
    }
}
