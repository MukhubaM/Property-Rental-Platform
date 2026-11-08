package propertyrental.platform.listing.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.listing.api.controller.UnitTypeController;
import propertyrental.platform.listing.api.dto.request.CreateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.request.UpdateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.response.UnitTypeResponse;
import propertyrental.platform.listing.application.service.UnitTypeService;
import propertyrental.platform.listing.domain.UnitTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.UnitTypeInUseException;
import propertyrental.platform.listing.domain.UnitTypeNotFoundException;

@WebMvcTest(UnitTypeController.class)
class UnitTypeControllerTest {

    private static final String VALID_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Type\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Type\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Type\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitTypeService unitTypeService;

    // ---- get all / get by id ----
    @Test
    void getAllReturns200() throws Exception {
        when(unitTypeService.getAllUnitTypes())
                .thenReturn(List.of(new UnitTypeResponse(3L, "ROOM", "Room")));

        mockMvc.perform(get("/api/v1/unit-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].code").value("ROOM"))
                .andExpect(jsonPath("$[0].name").value("Room"));
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(unitTypeService.getUnitTypeById(3L))
                .thenReturn(new UnitTypeResponse(3L, "ROOM", "Room"));

        mockMvc.perform(get("/api/v1/unit-types/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ROOM"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(unitTypeService.getUnitTypeById(99L)).thenThrow(new UnitTypeNotFoundException(99L));

        mockMvc.perform(get("/api/v1/unit-types/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Unit type not found"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedUnitType() throws Exception {
        when(unitTypeService.createUnitType(any(CreateUnitTypeRequest.class)))
                .thenReturn(new UnitTypeResponse(10L, "TEMP", "Temp Type"));

        mockMvc.perform(post("/api/v1/unit-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("TEMP"));
    }

    @Test
    void createReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/unit-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(unitTypeService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(unitTypeService.createUnitType(any(CreateUnitTypeRequest.class)))
                .thenThrow(new UnitTypeAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/unit-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Unit type already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedUnitType() throws Exception {
        when(unitTypeService.updateUnitType(eq(10L), any(UpdateUnitTypeRequest.class)))
                .thenReturn(new UnitTypeResponse(10L, "TEMP", "Temp Type 2"));

        mockMvc.perform(put("/api/v1/unit-types/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Temp Type 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/unit-types/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(unitTypeService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(unitTypeService.updateUnitType(eq(99L), any(UpdateUnitTypeRequest.class)))
                .thenThrow(new UnitTypeNotFoundException(99L));

        mockMvc.perform(put("/api/v1/unit-types/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherUnitType() throws Exception {
        when(unitTypeService.updateUnitType(eq(10L), any(UpdateUnitTypeRequest.class)))
                .thenThrow(new UnitTypeAlreadyExistsException("code", "ROOM"));

        mockMvc.perform(put("/api/v1/unit-types/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/unit-types/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new UnitTypeNotFoundException(99L))
                .when(unitTypeService).deleteUnitType(99L);

        mockMvc.perform(delete("/api/v1/unit-types/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenUnitTypeIsInUse() throws Exception {
        doThrow(new UnitTypeInUseException(3L))
                .when(unitTypeService).deleteUnitType(3L);

        mockMvc.perform(delete("/api/v1/unit-types/3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Unit type in use"));
    }
}
