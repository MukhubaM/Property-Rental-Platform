package propertyrental.platform.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import propertyrental.platform.identity.domain.RoleEntity;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.infrastructure.RoleRepository;

class RoleServiceTest {

    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final RoleService service = new RoleService(roleRepository);

    @Test
    void returnsRoleWhenFound() {
        RoleEntity role = mock(RoleEntity.class);
        when(roleRepository.findById((short) 1)).thenReturn(Optional.of(role));

        assertThat(service.getById((short) 1)).isSameAs(role);
    }

    @Test
    void throwsWhenRoleDoesNotExist() {
        when(roleRepository.findById((short) 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById((short) 99))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("99");
    }
}
