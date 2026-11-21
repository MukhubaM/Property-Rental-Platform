package propertyrental.platform.inquiry.api;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import propertyrental.platform.inquiry.api.controller.InquiryStatusController;
import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.service.InquiryStatusService;
import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.api.dto.request.CreateInquiryStatusRequest;
import propertyrental.platform.inquiry.domain.InquiryStatusAlreadyExistsException;

@WebMvcTest(InquiryStatusController.class)
class InquiryStatusControllerTest {

    private static final String CREATE_BODY = "{\"code\":\"TEMP\",\"name\":\"Temp Status\"}";
    private static final String CREATE_BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Temp Status\"}";
    private static final String CREATE_LONG_CODE_BODY = "{\"code\":\"" + "A".repeat(51) + "\",\"name\":\"Temp Status\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InquiryStatusService inquiryStatusService;

    // ---- get all ----
    @Test
    void getAllReturns200WithAllStatuses() throws Exception {
        when(inquiryStatusService.getAllInquiryStatuses()).thenReturn(List.of(
                new InquiryStatusResponse(1L, "NEW", "New"),
                new InquiryStatusResponse(2L, "RESPONDED", "Responded")));

        mockMvc.perform(get("/api/v1/inquiry-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("NEW"))
                .andExpect(jsonPath("$[0].name").value("New"))
                .andExpect(jsonPath("$[1].code").value("RESPONDED"));
    }

    @Test
    void getAllReturns200WithEmptyListWhenThereAreNoStatuses() throws Exception {
        when(inquiryStatusService.getAllInquiryStatuses()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/inquiry-statuses"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ---- get by id ----
    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(inquiryStatusService.getInquiryStatusById(1L))
                .thenReturn(new InquiryStatusResponse(1L, "NEW", "New"));

        mockMvc.perform(get("/api/v1/inquiry-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("NEW"))
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(inquiryStatusService.getInquiryStatusById(99L))
                .thenThrow(new InquiryStatusNotFoundException(99L));

        mockMvc.perform(get("/api/v1/inquiry-statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Inquiry status not found"));
    }

    @Test
    void getByIdReturns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/v1/inquiry-statuses/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---- create ----
    @Test
    void createReturns201WithCreatedInquiryStatus() throws Exception {
        when(inquiryStatusService.createInquiryStatus(any(CreateInquiryStatusRequest.class)))
                .thenReturn(new InquiryStatusResponse(5L, "TEMP", "Temp Status"));

        mockMvc.perform(post("/api/v1/inquiry-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.code").value("TEMP"))
                .andExpect(jsonPath("$.name").value("Temp Status"));
    }

    @Test
    void createReturns400WhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/inquiry-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(inquiryStatusService);
    }

    @Test
    void createReturns400WhenCodeIsTooLong() throws Exception {
        mockMvc.perform(post("/api/v1/inquiry-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(inquiryStatusService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(inquiryStatusService.createInquiryStatus(any(CreateInquiryStatusRequest.class)))
                .thenThrow(new InquiryStatusAlreadyExistsException("code", "TEMP"));

        mockMvc.perform(post("/api/v1/inquiry-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Inquiry status already exists"));
    }
}