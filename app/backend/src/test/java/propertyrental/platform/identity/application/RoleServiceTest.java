package propertyrental.platform.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.mapper.RoleMapper;
import propertyrental.platform.identity.application.service.RoleService;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.infrastructure.persistence.entity.RoleEntity;
import propertyrental.platform.identity.infrastructure.persistence.repository.RoleRepository;

class RoleServiceTest {

    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final RoleService roleService = new RoleService(roleRepository, new RoleMapper());

    @Test
    void getRoleByIdReturnsRoleWhenFound() {
        RoleEntity role = new RoleEntity();
        role.setId(4L);
        role.setCode("ADMIN");
        role.setName("Administrator");
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));

        assertThat(roleService.getRoleById(4L))
                .isEqualTo(new RoleResponse(4L, "ADMIN", "Administrator"));
    }

    @Test
    void getRoleByIdThrowsWhenRoleDoesNotExist() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleById(99L))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("99");
    }
}