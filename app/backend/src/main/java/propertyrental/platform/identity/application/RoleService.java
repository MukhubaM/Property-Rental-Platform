package propertyrental.platform.identity.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propertyrental.platform.identity.domain.RoleEntity;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.infrastructure.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public RoleEntity getById(Short id) {

        return roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
    }
}