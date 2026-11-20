package propertyrental.platform.identity.api;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.identity.api.controller.RoleController;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.service.RoleService;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.domain.RoleInUseException;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Test
    void getRoleByIdReturns200WhenFound() throws Exception {
        when(roleService.getRoleById(4L)).thenReturn(new RoleResponse(4L, "ADMIN", "Administrator"));

        mockMvc.perform(get("/api/v1/roles/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.code").value("ADMIN"))
                .andExpect(jsonPath("$.name").value("Administrator"));
    }

    @Test
    void getRoleByIdReturns404WhenNotFound() throws Exception {
        when(roleService.getRoleById(99L)).thenThrow(new RoleNotFoundException(99L));

        mockMvc.perform(get("/api/v1/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Role not found"));
    }

    @Test
    void getRoleByIdReturns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/v1/roles/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/roles/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenRoleNotFound() throws Exception {
        doThrow(new RoleNotFoundException(99L))
                .when(roleService).deleteRole(99L);

        mockMvc.perform(delete("/api/v1/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Role not found"));
    }

    @Test
    void deleteReturns409WhenRoleIsInUse() throws Exception {
        doThrow(new RoleInUseException(1L))
                .when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Role in use"));
    }
}