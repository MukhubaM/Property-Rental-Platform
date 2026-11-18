package propertyrental.platform.moderation.api;

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

import propertyrental.platform.moderation.api.controller.ReportStatusController;
import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.service.ReportStatusService;
import propertyrental.platform.moderation.domain.ReportStatusNotFoundException;

@WebMvcTest(ReportStatusController.class)
class ReportStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportStatusService reportStatusService;

    // ---- get all ----
    @Test
    void getAllReturns200WithAllReportStatuses() throws Exception {
        when(reportStatusService.getAllReportStatuses()).thenReturn(List.of(
                new ReportStatusResponse(1L, "OPEN", "Open"),
                new ReportStatusResponse(2L, "UNDER_REVIEW", "Under Review")));

        mockMvc.perform(get("/api/v1/report-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("OPEN"))
                .andExpect(jsonPath("$[0].name").value("Open"))
                .andExpect(jsonPath("$[1].code").value("UNDER_REVIEW"));
    }

    @Test
    void getAllReturns200WithEmptyListWhenThereAreNoReportStatuses() throws Exception {
        when(reportStatusService.getAllReportStatuses()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/report-statuses"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ---- get by id ----
    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(reportStatusService.getReportStatusById(1L))
                .thenReturn(new ReportStatusResponse(1L, "OPEN", "Open"));

        mockMvc.perform(get("/api/v1/report-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("OPEN"))
                .andExpect(jsonPath("$.name").value("Open"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(reportStatusService.getReportStatusById(99L))
                .thenThrow(new ReportStatusNotFoundException(99L));

        mockMvc.perform(get("/api/v1/report-statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report status not found"));
    }

    @Test
    void getByIdReturns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/v1/report-statuses/abc"))
                .andExpect(status().isBadRequest());
    }
}
