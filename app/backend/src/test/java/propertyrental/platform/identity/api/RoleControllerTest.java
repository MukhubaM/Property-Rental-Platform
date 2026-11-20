package propertyrental.platform.identity.api;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import propertyrental.platform.identity.api.dto.request.CreateRoleRequest;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.service.RoleService;
import propertyrental.platform.identity.domain.RoleAlreadyExistsException;
import propertyrental.platform.identity.domain.RoleNotFoundException;
import propertyrental.platform.identity.domain.RoleInUseException;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    private static final String CREATE_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Role\"}";
    private static final String CREATE_BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Role\"}";
    private static final String CREATE_LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Role\"}";

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

    // ---- create ----
    @Test
    void createReturns201WithCreatedRole() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class)))
                .thenReturn(new RoleResponse(10L, "TEMP", "Temp Role"));

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Role"));
    }

    @Test
    void createReturns400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(roleService);
    }

    @Test
    void createReturns400WhenCodeIsTooLong() throws Exception {
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(roleService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new RoleAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Role already exists"));
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