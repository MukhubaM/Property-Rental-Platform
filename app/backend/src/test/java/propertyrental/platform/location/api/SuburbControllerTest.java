package propertyrental.platform.location.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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

import propertyrental.platform.location.api.controller.SuburbController;
import propertyrental.platform.location.api.dto.request.CreateSuburbRequest;
import propertyrental.platform.location.api.dto.request.UpdateSuburbRequest;
import propertyrental.platform.location.api.dto.response.SuburbResponse;
import propertyrental.platform.location.application.service.SuburbService;
import propertyrental.platform.location.domain.SuburbAlreadyExistsException;
import propertyrental.platform.location.domain.SuburbCityNotFoundException;
import propertyrental.platform.location.domain.SuburbInUseException;
import propertyrental.platform.location.domain.SuburbNotFoundException;

@WebMvcTest(SuburbController.class)
class SuburbControllerTest {

    private static final String VALID_BODY = "{\"cityId\":5,\"name\":\"Temp Suburb\"}";
    private static final String BLANK_NAME_BODY = "{\"cityId\":5,\"name\":\"\"}";
    private static final String MISSING_CITY_BODY = "{\"name\":\"Temp Suburb\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SuburbService suburbService;

    // ---- list/get by id ----
    @Test
    void getAllReturns200() throws Exception {
        when(suburbService.getAllSuburbs(null))
                .thenReturn(List.of(new SuburbResponse(1L, 5L, "Sandton")));

        mockMvc.perform(get("/api/v1/suburbs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cityId").value(5))
                .andExpect(jsonPath("$[0].name").value("Sandton"));
    }

    @Test
    void getAllPassesTheCityFilter() throws Exception {
        when(suburbService.getAllSuburbs(5L))
                .thenReturn(List.of(new SuburbResponse(1L, 5L, "Sandton")));

        mockMvc.perform(get("/api/v1/suburbs").param("cityId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(suburbService).getAllSuburbs(5L);
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(suburbService.getSuburbById(1L)).thenReturn(new SuburbResponse(1L, 5L, "Sandton"));

        mockMvc.perform(get("/api/v1/suburbs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sandton"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(suburbService.getSuburbById(99L)).thenThrow(new SuburbNotFoundException(99L));

        mockMvc.perform(get("/api/v1/suburbs/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Suburb not found"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedSuburb() throws Exception {
        when(suburbService.createSuburb(any(CreateSuburbRequest.class)))
                .thenReturn(new SuburbResponse(10L, 5L, "Temp Suburb"));

        mockMvc.perform(post("/api/v1/suburbs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.cityId").value(5));
    }

    @Test
    void createReturns400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/suburbs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_NAME_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verifyNoInteractions(suburbService);
    }

    @Test
    void createReturns400WhenCityIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/suburbs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MISSING_CITY_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cityId").exists());

        verifyNoInteractions(suburbService);
    }

    @Test
    void createReturns400WhenCityDoesNotExist() throws Exception {
        when(suburbService.createSuburb(any(CreateSuburbRequest.class)))
                .thenThrow(new SuburbCityNotFoundException(5L));

        mockMvc.perform(post("/api/v1/suburbs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("City does not exist"));
    }

    @Test
    void createReturns409WhenNameAlreadyExists() throws Exception {
        when(suburbService.createSuburb(any(CreateSuburbRequest.class)))
                .thenThrow(new SuburbAlreadyExistsException("Temp Suburb", 5L));

        mockMvc.perform(post("/api/v1/suburbs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Suburb already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedSuburb() throws Exception {
        when(suburbService.updateSuburb(eq(10L), any(UpdateSuburbRequest.class)))
                .thenReturn(new SuburbResponse(10L, 5L, "Temp Suburb 2"));

        mockMvc.perform(put("/api/v1/suburbs/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Temp Suburb 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/suburbs/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_NAME_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verifyNoInteractions(suburbService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(suburbService.updateSuburb(eq(99L), any(UpdateSuburbRequest.class)))
                .thenThrow(new SuburbNotFoundException(99L));

        mockMvc.perform(put("/api/v1/suburbs/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenNameBelongsToAnotherSuburb() throws Exception {
        when(suburbService.updateSuburb(eq(10L), any(UpdateSuburbRequest.class)))
                .thenThrow(new SuburbAlreadyExistsException("Temp Suburb", 5L));

        mockMvc.perform(put("/api/v1/suburbs/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/suburbs/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new SuburbNotFoundException(99L)).when(suburbService).deleteSuburb(99L);

        mockMvc.perform(delete("/api/v1/suburbs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenSuburbIsInUse() throws Exception {
        doThrow(new SuburbInUseException(1L)).when(suburbService).deleteSuburb(1L);

        mockMvc.perform(delete("/api/v1/suburbs/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Suburb in use"));
    }
}
