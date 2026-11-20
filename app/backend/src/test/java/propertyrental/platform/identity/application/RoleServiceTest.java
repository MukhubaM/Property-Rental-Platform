package propertyrental.platform.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;

import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.mapper.RoleMapper;
import propertyrental.platform.identity.application.service.RoleService;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.infrastructure.persistence.entity.RoleEntity;
import propertyrental.platform.identity.infrastructure.persistence.repository.RoleRepository;
import propertyrental.platform.identity.domain.RoleInUseException;
import propertyrental.platform.identity.api.dto.request.CreateRoleRequest;
import propertyrental.platform.identity.domain.RoleAlreadyExistsException;

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

    // ---- create ----
    @Test
    void createSavesAndReturnsNewRole() {
        when(roleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> {
            RoleEntity saved = invocation.getArgument(0);
            saved.setId(10L);

            return saved;
        });

        RoleResponse response = roleService.createRole(new CreateRoleRequest("TEMP", "Temp Role"));

        assertThat(response).isEqualTo(new RoleResponse(10L, "TEMP", "Temp Role"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(roleRepository.existsByCode("TENANT")).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(new CreateRoleRequest("TENANT", "Another")))
                .isInstanceOf(RoleAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(roleRepository.existsByName("Tenant")).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(new CreateRoleRequest("NEW_CODE", "Tenant")))
                .isInstanceOf(RoleAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(roleRepository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingRole() {
        RoleEntity role = new RoleEntity();
        role.setId(10L);
        role.setCode("TEMP");
        role.setName("Temp Role");
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role));

        roleService.deleteRole(10L);

        verify(roleRepository).delete(role);
    }

    @Test
    void deleteThrowsWhenRoleDoesNotExist() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.deleteRole(99L))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("99");
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenRoleIsStillAssignedToUsers() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setCode("TENANT");
        role.setName("Tenant");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        doThrow(new DataIntegrityViolationException("fk_user_roles_role"))
                .when(roleRepository).flush();

        assertThatThrownBy(() -> roleService.deleteRole(1L))
                .isInstanceOf(RoleInUseException.class)
                .hasMessageContaining("1");
    }
}