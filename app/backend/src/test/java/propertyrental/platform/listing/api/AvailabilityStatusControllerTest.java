package propertyrental.platform.listing.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import propertyrental.platform.listing.api.controller.AvailabilityStatusController;
import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;
import propertyrental.platform.listing.api.dto.request.UpdateAvailabilityStatusRequest;
import propertyrental.platform.listing.api.dto.request.CreateAvailabilityStatusRequest;
import propertyrental.platform.listing.domain.AvailabilityStatusAlreadyExistsException;
import propertyrental.platform.listing.domain.AvailabilityStatusInUseException;

@WebMvcTest(AvailabilityStatusController.class)
class AvailabilityStatusControllerTest {

    private static final String UPDATE_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Status 2\"}";
    private static final String UPDATE_BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Status 2\"}";
    private static final String VALID_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Status\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Status\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Status\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityStatusService availabilityStatusService;

    // ---- get all ----
    @Test
    void getAllReturns200WithAllAvailabilityStatuses() throws Exception {
        when(availabilityStatusService.getAllAvailabilityStatuses()).thenReturn(List.of(
                new AvailabilityStatusResponse(1L, "AVAILABLE", "Available"),
                new AvailabilityStatusResponse(2L, "OCCUPIED", "Occupied")));

        mockMvc.perform(get("/api/v1/availability-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].name").value("Available"))
                .andExpect(jsonPath("$[1].code").value("OCCUPIED"));
    }

    @Test
    void getAllReturns200WithEmptyListWhenThereAreNoAvailabilityStatuses() throws Exception {
        when(availabilityStatusService.getAllAvailabilityStatuses()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/availability-statuses"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ---- get by id ----
    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(availabilityStatusService.getAvailabilityStatusById(1L))
                .thenReturn(new AvailabilityStatusResponse(1L, "AVAILABLE", "Available"));

        mockMvc.perform(get("/api/v1/availability-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("AVAILABLE"))
                .andExpect(jsonPath("$.name").value("Available"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(availabilityStatusService.getAvailabilityStatusById(99L))
                .thenThrow(new AvailabilityStatusNotFoundException(99L));

        mockMvc.perform(get("/api/v1/availability-statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Availability status not found"));
    }

    @Test
    void getByIdReturns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/v1/availability-statuses/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedAvailabilityStatus() throws Exception {
        when(availabilityStatusService.updateAvailabilityStatus(eq(5L), any(UpdateAvailabilityStatusRequest.class)))
                .thenReturn(new AvailabilityStatusResponse(5L, "TEMP", "Temp Status 2"));

        mockMvc.perform(put("/api/v1/availability-statuses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Status 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/availability-statuses/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATE_BLANK_CODE_BODY));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedAvailabilityStatus() throws Exception {
        when(availabilityStatusService.createAvailabilityStatus(any(CreateAvailabilityStatusRequest.class)))
                .thenReturn(new AvailabilityStatusResponse(5L, "TEMP", "Temp Status"));

        mockMvc.perform(post("/api/v1/availability-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Status"));
    }

    @Test
    void createReturns400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/availability-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(availabilityStatusService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(availabilityStatusService.updateAvailabilityStatus(eq(99L), any(UpdateAvailabilityStatusRequest.class)))
                .thenThrow(new AvailabilityStatusNotFoundException(99L));

        mockMvc.perform(put("/api/v1/availability-statuses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Availability status not found"));
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherAvailabilityStatus() throws Exception {
        when(availabilityStatusService.updateAvailabilityStatus(eq(5L), any(UpdateAvailabilityStatusRequest.class)))
                .thenThrow(new AvailabilityStatusAlreadyExistsException("code", "AVAILABLE"));

        mockMvc.perform(put("/api/v1/availability-statuses/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATE_BODY));
    }

    void createReturns400WhenCodeIsTooLong() throws Exception {
        mockMvc.perform(post("/api/v1/availability-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(availabilityStatusService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(availabilityStatusService.createAvailabilityStatus(any(CreateAvailabilityStatusRequest.class)))
                .thenThrow(new AvailabilityStatusAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/availability-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Availability status already exists"));
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/availability-statuses/5"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new AvailabilityStatusNotFoundException(99L))
                .when(availabilityStatusService).deleteAvailabilityStatus(99L);

        mockMvc.perform(delete("/api/v1/availability-statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Availability status not found"));
    }

    @Test
    void deleteReturns409WhenAvailabilityStatusIsInUse() throws Exception {
        doThrow(new AvailabilityStatusInUseException(1L))
                .when(availabilityStatusService).deleteAvailabilityStatus(1L);

        mockMvc.perform(delete("/api/v1/availability-statuses/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Availability status in use"));
    }
}