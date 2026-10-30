package propertyrental.platform.identity.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.mapper.RoleMapper;
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
}
