package propertyrental.platform.location.api;

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

import propertyrental.platform.location.api.controller.ProvinceController;
import propertyrental.platform.location.api.dto.request.CreateProvinceRequest;
import propertyrental.platform.location.api.dto.request.UpdateProvinceRequest;
import propertyrental.platform.location.api.dto.response.ProvinceResponse;
import propertyrental.platform.location.application.service.ProvinceService;
import propertyrental.platform.location.domain.ProvinceAlreadyExistsException;
import propertyrental.platform.location.domain.ProvinceInUseException;
import propertyrental.platform.location.domain.ProvinceNotFoundException;

@WebMvcTest(ProvinceController.class)
class ProvinceControllerTest {

    private static final String VALID_BODY = "{\"code\":\"TST\",\"name\":\"Test Province\"}";
    private static final String BLANK_CODE_BODY = "{\"code\":\"\",\"name\":\"Test Province\"}";
    private static final String LONG_CODE_BODY = "{\"code\":\"ABCDEFGHIJK\",\"name\":\"Test Province\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProvinceService provinceService;

    // ---- get all / get by id ----

    @Test
    void getAllReturns200() throws Exception {
        when(provinceService.getAllProvinces())
                .thenReturn(List.of(new ProvinceResponse(3L, "GP", "Gauteng")));

        mockMvc.perform(get("/api/v1/provinces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].code").value("GP"))
                .andExpect(jsonPath("$[0].name").value("Gauteng"));
    }

    @Test
    void getByIdReturns200WhenFound() throws Exception {
        when(provinceService.getProvinceById(3L))
                .thenReturn(new ProvinceResponse(3L, "GP", "Gauteng"));

        mockMvc.perform(get("/api/v1/provinces/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GP"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(provinceService.getProvinceById(99L)).thenThrow(new ProvinceNotFoundException(99L));

        mockMvc.perform(get("/api/v1/provinces/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Province not found"));
    }

    // ---- create ----

    @Test
    void createReturns201WithCreatedProvince() throws Exception {
        when(provinceService.createProvince(any(CreateProvinceRequest.class)))
                .thenReturn(new ProvinceResponse(10L, "TST", "Test Province"));

        mockMvc.perform(post("/api/v1/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.code").value("TST"));
    }

    @Test
    void createReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LONG_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(provinceService);
    }

    @Test
    void createReturns409WhenCodeOrNameAlreadyExists() throws Exception {
        when(provinceService.createProvince(any(CreateProvinceRequest.class)))
                .thenThrow(new ProvinceAlreadyExistsException("code", "TST"));

        mockMvc.perform(post("/api/v1/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Province already exists"));
    }

    // ---- update ----

    @Test
    void updateReturns200WithUpdatedProvince() throws Exception {
        when(provinceService.updateProvince(eq(10L), any(UpdateProvinceRequest.class)))
                .thenReturn(new ProvinceResponse(10L, "TST", "Test Province 2"));

        mockMvc.perform(put("/api/v1/provinces/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Province 2"));
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/provinces/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_CODE_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists());

        verifyNoInteractions(provinceService);
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        when(provinceService.updateProvince(eq(99L), any(UpdateProvinceRequest.class)))
                .thenThrow(new ProvinceNotFoundException(99L));

        mockMvc.perform(put("/api/v1/provinces/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturns409WhenCodeOrNameBelongsToAnotherProvince() throws Exception {
        when(provinceService.updateProvince(eq(10L), any(UpdateProvinceRequest.class)))
                .thenThrow(new ProvinceAlreadyExistsException("code", "GP"));

        mockMvc.perform(put("/api/v1/provinces/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    // ---- delete ----

    @Test
    void deleteReturns204WithNoBody() throws Exception {
        mockMvc.perform(delete("/api/v1/provinces/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteReturns404WhenNotFound() throws Exception {
        doThrow(new ProvinceNotFoundException(99L))
                .when(provinceService).deleteProvince(99L);

        mockMvc.perform(delete("/api/v1/provinces/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns409WhenProvinceIsInUse() throws Exception {
        doThrow(new ProvinceInUseException(3L))
                .when(provinceService).deleteProvince(3L);

        mockMvc.perform(delete("/api/v1/provinces/3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Province in use"));
    }
}