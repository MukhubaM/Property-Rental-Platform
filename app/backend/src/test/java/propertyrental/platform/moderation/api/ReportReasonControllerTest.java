package propertyrental.platform.moderation.api;

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

import propertyrental.platform.moderation.api.controller.ReportReasonController;
import propertyrental.platform.moderation.api.dto.request.CreateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.request.UpdateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.response.ReportReasonResponse;
import propertyrental.platform.moderation.application.service.ReportReasonService;
import propertyrental.platform.moderation.domain.ReportReasonAlreadyExistsException;
import propertyrental.platform.moderation.domain.ReportReasonInUseException;
import propertyrental.platform.moderation.domain.ReportReasonNotFoundException;

@WebMvcTest(ReportReasonController.class)
class ReportReasonControllerTest {

    private static final String VALID_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Reason\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Reason\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Reason\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportReasonService reportReasonService;

    // ---- get all / get by id ----
    @Test
    void getAllReturns200() throws Exception {
        when(reportReasonService.getAllReportReasons())
                .thenReturn(List.of(new ReportReasonResponse(1L, "SUSPECTED_SCAM", "Suspected Scam")));

        mockMvc.perform(get("/api/v1/report-reasons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("SUSPECTED_SCAM"))
                .andExpect(jsonPath("$[0].name").value("Suspected Scam"));
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(reportReasonService.getReportReasonById(1L))
                .thenReturn(new ReportReasonResponse(1L, "SUSPECTED_SCAM", "Suspected Scam"));

        mockMvc.perform(get("/api/v1/report-reasons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUSPECTED_SCAM"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(reportReasonService.getReportReasonById(99L)).thenThrow(new ReportReasonNotFoundException(99L));

        mockMvc.perform(get("/api/v1/report-reasons/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Report reason not found"));
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedReportReason() throws Exception {
        when(reportReasonService.createReportReason(any(CreateReportReasonRequest.class)))
                .thenReturn(new ReportReasonResponse(10L, "TEMP", "Temp Reason"));

        mockMvc.perform(post("/api/v1/report-reasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("TEMP"));
    }

    @Test
    void createReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/report-reasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(reportReasonService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(reportReasonService.createReportReason(any(CreateReportReasonRequest.class)))
                .thenThrow(new ReportReasonAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/report-reasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Report reason already exists"));
    }

    // ---- update ----
    @Test
    void updateReturns200WithUpdatedReportReason() throws Exception {
        when(reportReasonService.updateReportReason(eq(10L), any(UpdateReportReasonRequest.class)))
                .thenReturn(new ReportReasonResponse(10L, "TEMP", "Temp Reason 2"));

        mockMvc.perform(put("/api/v1/report-reasons/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Temp Reason 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/report-reasons/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(reportReasonService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(reportReasonService.updateReportReason(eq(99L), any(UpdateReportReasonRequest.class)))
                .thenThrow(new ReportReasonNotFoundException(99L));

        mockMvc.perform(put("/api/v1/report-reasons/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherReportReason() throws Exception {
        when(reportReasonService.updateReportReason(eq(10L), any(UpdateReportReasonRequest.class)))
                .thenThrow(new ReportReasonAlreadyExistsException("code", "OTHER"));

        mockMvc.perform(put("/api/v1/report-reasons/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----
    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/report-reasons/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new ReportReasonNotFoundException(99L))
                .when(reportReasonService).deleteReportReason(99L);

        mockMvc.perform(delete("/api/v1/report-reasons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenReportReasonIsInUse() throws Exception {
        doThrow(new ReportReasonInUseException(1L))
                .when(reportReasonService).deleteReportReason(1L);

        mockMvc.perform(delete("/api/v1/report-reasons/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Report reason in use"));
    }
}