package propertyrental.platform.identity.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import propertyrental.platform.identity.application.RoleService;
import propertyrental.platform.identity.domain.RoleEntity;
import propertyrental.platform.identity.domain.RoleNotFoundException;

@WebMvcTest(RoleController.class)
@Import(RoleMapper.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Test
    void returns200WithRoleWhenFound() throws Exception {
        RoleEntity role = mock(RoleEntity.class);
        when(role.getId()).thenReturn((short) 1);
        when(role.getCode()).thenReturn("ADMIN");
        when(role.getName()).thenReturn("Administrator");
        when(roleService.getById((short) 1)).thenReturn(role);

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.name").value("Administrator"));
    }

    @Test
    void returns404WhenRoleNotFound() throws Exception {
        when(roleService.getById((short) 99)).thenThrow(new RoleNotFoundException((short) 99));

        mockMvc.perform(get("/api/v1/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Role not found"));
    }

    @Test
    void returns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/v1/roles/abc"))
                .andExpect(status().isBadRequest());
    }
}
