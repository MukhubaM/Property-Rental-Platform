package propertyrental.platform.listing.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.listing.api.controller.AvailabilityStatusController;
import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;

@WebMvcTest(AvailabilityStatusController.class)
class AvailabilityStatusControllerTest {

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
}