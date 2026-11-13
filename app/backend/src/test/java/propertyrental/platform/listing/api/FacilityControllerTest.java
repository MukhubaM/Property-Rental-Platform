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

import propertyrental.platform.listing.api.controller.FacilityController;
import propertyrental.platform.listing.api.dto.request.CreateFacilityRequest;
import propertyrental.platform.listing.api.dto.request.UpdateFacilityRequest;
import propertyrental.platform.listing.api.dto.response.FacilityResponse;
import propertyrental.platform.listing.application.service.FacilityService;
import propertyrental.platform.listing.domain.FacilityAlreadyExistsException;
import propertyrental.platform.listing.domain.FacilityInUseException;
import propertyrental.platform.listing.domain.FacilityNotFoundException;

@WebMvcTest(FacilityController.class)
class FacilityControllerTest {

    private static final String VALID_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Facility\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Facility\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Facility\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;

    // ---- get all / get by id ----
    @Test
    void getAllReturns200() throws Exception {
        when(facilityService.getAllFacilities())
                .thenReturn(List.of(new FacilityResponse(1L, "WIFI", "Wi-Fi")));

        mockMvc.perform(get("/api/v1/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("WIFI"))
                .andExpect(jsonPath("$[0].name").value("Wi-Fi"));
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(facilityService.getFacilityById(1L))
                .thenReturn(new FacilityResponse(1L, "WIFI", "Wi-Fi"));

        mockMvc.perform(get("/api/v1/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("WIFI"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(facilityService.getFacilityById(99L)).thenThrow(new FacilityNotFoundException(99L));

        mockMvc.perform(get("/api/v1/facilities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Facility not found"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedFacility() throws Exception {
        when(facilityService.createFacility(any(CreateFacilityRequest.class)))
                .thenReturn(new FacilityResponse(20L, "TEMP", "Temp Facility"));

        mockMvc.perform(post("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.code").value("TEMP"));
    }

    @Test
    void createReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(facilityService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(facilityService.createFacility(any(CreateFacilityRequest.class)))
                .thenThrow(new FacilityAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Facility already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedFacility() throws Exception {
        when(facilityService.updateFacility(eq(20L), any(UpdateFacilityRequest.class)))
                .thenReturn(new FacilityResponse(20L, "TEMP", "Temp Facility 2"));

        mockMvc.perform(put("/api/v1/facilities/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Temp Facility 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/facilities/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(facilityService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(facilityService.updateFacility(eq(99L), any(UpdateFacilityRequest.class)))
                .thenThrow(new FacilityNotFoundException(99L));

        mockMvc.perform(put("/api/v1/facilities/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherFacility() throws Exception {
        when(facilityService.updateFacility(eq(20L), any(UpdateFacilityRequest.class)))
                .thenThrow(new FacilityAlreadyExistsException("code", "WIFI"));

        mockMvc.perform(put("/api/v1/facilities/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/facilities/20"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new FacilityNotFoundException(99L))
                .when(facilityService).deleteFacility(99L);

        mockMvc.perform(delete("/api/v1/facilities/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenFacilityIsInUse() throws Exception {
        doThrow(new FacilityInUseException(1L))
                .when(facilityService).deleteFacility(1L);

        mockMvc.perform(delete("/api/v1/facilities/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Facility in use"));
    }
}
