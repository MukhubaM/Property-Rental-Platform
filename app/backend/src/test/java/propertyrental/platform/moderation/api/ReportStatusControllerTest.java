package propertyrental.platform.moderation.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.moderation.api.controller.ReportStatusController;
import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.service.ReportStatusService;
import propertyrental.platform.moderation.domain.ReportStatusNotFoundException;
import propertyrental.platform.moderation.api.dto.request.CreateReportStatusRequest;
import propertyrental.platform.moderation.domain.ReportStatusAlreadyExistsException;
import propertyrental.platform.moderation.api.dto.request.UpdateReportStatusRequest;
import propertyrental.platform.moderation.domain.ReportStatusInUseException;

@WebMvcTest(ReportStatusController.class)
class ReportStatusControllerTest {

    private static final String UPDATE_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Status 2\"}";
    private static final String UPDATE_BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Status 2\"}";
    private static final String CREATE_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Status\"}";
    private static final String CREATE_BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Status\"}";
    private static final String CREATE_LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Status\"}";

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

    // ---- create ----
    @Test
    void createReturns201WithCreatedReportStatus() throws Exception {
        when(reportStatusService.createReportStatus(any(CreateReportStatusRequest.class)))
                .thenReturn(new ReportStatusResponse(5L, "TEMP", "Temp Status"));

        mockMvc.perform(post("/api/v1/report-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Status"));
    }

    @Test
    void createReturns400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/report-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(reportStatusService);
    }

    @Test
    void createReturns400WhenCodeIsTooLong() throws Exception {
        mockMvc.perform(post("/api/v1/report-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(reportStatusService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(reportStatusService.createReportStatus(any(CreateReportStatusRequest.class)))
                .thenThrow(new ReportStatusAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/report-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Report status already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedReportStatus() throws Exception {
        when(reportStatusService.updateReportStatus(eq(5L), any(UpdateReportStatusRequest.class)))
                .thenReturn(new ReportStatusResponse(5L, "TEMP", "Temp Status 2"));

        mockMvc.perform(put("/api/v1/report-statuses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Status 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/report-statuses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(reportStatusService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(reportStatusService.updateReportStatus(eq(99L), any(UpdateReportStatusRequest.class)))
                .thenThrow(new ReportStatusNotFoundException(99L));

        mockMvc.perform(put("/api/v1/report-statuses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report status not found"));
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherReportStatus() throws Exception {
        when(reportStatusService.updateReportStatus(eq(5L), any(UpdateReportStatusRequest.class)))
                .thenThrow(new ReportStatusAlreadyExistsException("code", "OPEN"));

        mockMvc.perform(put("/api/v1/report-statuses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Report status already exists"));
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/report-statuses/5"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new ReportStatusNotFoundException(99L))
                .when(reportStatusService).deleteReportStatus(99L);

        mockMvc.perform(delete("/api/v1/report-statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report status not found"));
    }

    @Test
    void deleteReturns409WhenReportStatusIsInUse() throws Exception {
        doThrow(new ReportStatusInUseException(1L))
                .when(reportStatusService).deleteReportStatus(1L);

        mockMvc.perform(delete("/api/v1/report-statuses/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Report status in use"));
    }
}
